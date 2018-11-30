package es.bsalazar.secretcafe.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

public class NotificationsService extends Service {

    private FirestoreManager firestoreManager;

    public NotificationsService() {
        firestoreManager = FirestoreManager.getInstance();
    }

    boolean flag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        firestoreManager.getDrinks(new FirestoreManager.OnCollectionChangedListener<Drink>() {
            @Override
            public void onCollectionChange(List<Drink> collection) {
                flag = true;
            }

            @Override
            public void onDocumentAdded(int index, Drink object) {
                if (flag)
                    sendNotification(0004, "Bebida añadida", "Se ha añadido una nueva bebida");
            }

            @Override
            public void onDocumentChanged(int index, Drink object) {

            }

            @Override
            public void onDocumentRemoved(int index, Drink object) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(int id, String title, String subtitle) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            //NOTIFICATION
            Notification notification = null;
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(subtitle)
                    .setSmallIcon(R.drawable.logo_secret_notif)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(id, notification);

        }
    }
}
