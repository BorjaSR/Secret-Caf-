package es.bsalazar.secretcafe.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private final String LAST_GEO_NOTIF_TIMESTAMP_KEY = "LAST_GEO_NOTIF_TIMESTAMP";
    private final String IMEI_SAVED_KEY = "IMEI_SAVED";

    private static final Object LOCK = new Object();
    private static PreferencesManager instance;
    private SharedPreferences sharedPreferences;

    public synchronized static PreferencesManager getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new PreferencesManager(context);
            }
        }
        return instance;
    }

    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences("MY_PREFERENCE_KEY", Context.MODE_PRIVATE);
    }

    public void setGeoNotificationTimestamp(long timestamp){
        sharedPreferences.edit().putLong(LAST_GEO_NOTIF_TIMESTAMP_KEY, timestamp).apply();
    }

    public long getGeoNotificationTimestamp(){
        return sharedPreferences.getLong(LAST_GEO_NOTIF_TIMESTAMP_KEY, 0);
    }

    public void setIMEIsaved(){
        sharedPreferences.edit().putBoolean(IMEI_SAVED_KEY, true).apply();
    }

    public boolean isIMEIsaved(){
        return sharedPreferences.getBoolean(IMEI_SAVED_KEY, false);
    }
}
