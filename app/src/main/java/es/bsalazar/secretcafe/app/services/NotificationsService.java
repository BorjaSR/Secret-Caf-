package es.bsalazar.secretcafe.app.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.MainActivity;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/*
 * Created by Borja Salazar on 07/11/2018
 */

public class NotificationsService extends Service {

    private FirestoreManager firestoreManager;

    public NotificationsService() {
        firestoreManager = FirestoreManager.getInstance();
    }

    boolean flag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        firestoreManager.getNotifications(new FirestoreManager.OnCollectionChangedListener<es.bsalazar.secretcafe.data.entities.Notification>() {
            @Override
            public void onCollectionChange(List<es.bsalazar.secretcafe.data.entities.Notification> collection) {
                flag = true;
            }

            @Override
            public void onDocumentAdded(int index, es.bsalazar.secretcafe.data.entities.Notification notification) {
                if (flag)
                    sendNotification(1, notification.getTitle(), notification.getDescription());
            }

            @Override
            public void onDocumentChanged(int index, es.bsalazar.secretcafe.data.entities.Notification object) {

            }

            @Override
            public void onDocumentRemoved(int index, es.bsalazar.secretcafe.data.entities.Notification object) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(int id, String title, String subtitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            NotificationManager notificationManager =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

            //NOTIFICATION
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(subtitle)
                    .setSmallIcon(R.drawable.logo_secret_notif)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification.setChannelId(CHANNEL_ID);
            }

            notificationManager.notify(id, notification.build());
        }
    }
}
