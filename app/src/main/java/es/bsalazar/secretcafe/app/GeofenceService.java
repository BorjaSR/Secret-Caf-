package es.bsalazar.secretcafe.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.util.StateSet;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.SecretRepository;

public class GeofenceService extends IntentService {

    private final String TAG = "GEOFENCE SERVICE";
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    private SecretRepository secretRepository;

    public GeofenceService() {
        super("GEOFENCE SERVICE");
    }

    public GeofenceService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.e(TAG, "ERROR: " + String.valueOf(geofencingEvent.getErrorCode()));
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                String type = geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? "ENTER" : "EXIT";

                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                for (Geofence geofence : triggeringGeofences) {
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals("2222"))
                        sendNotification(0x2222, getString(R.string.notif_title), getString(R.string.notif_msg));
                }

            } else {
                // Log the error.
                Log.e(TAG, "NOT VALID GEOFENCE TYPE");
            }
        } else {
            // Log the error.
            Log.e(TAG, "GEOFENCING EVENT DOESN'T EXIST");
        }
    }

    private void sendNotification(int id, String title, String subtitle) {
        secretRepository = Injector.provideSecretRepository(getApplicationContext());
        long lastNotifDate = secretRepository.getGeoNotificationTimestamp();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if(lastNotifDate + ONE_DAY < System.currentTimeMillis()){
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                //NOTIFICATION
                Notification notification = null;
                notification = new Notification.Builder(this)
                        .setContentTitle(title)
                        .setContentText(subtitle)
                        .setSmallIcon(R.drawable.logo_secret_notif)
                        //                .setColor(Color.parseColor("#4B8A08"))
                        //                .setVibrate(new long[]{1000, 500})
                        //                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        //                .setLights(Color.RED, 3000, 3000)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

                notificationManager.notify(id, notification);
                secretRepository.setGeoNotificationTimestamp(System.currentTimeMillis());
            }
        }
    }
}
