package es.bsalazar.secretcafe.data;

import android.arch.lifecycle.MutableLiveData;

import com.google.firebase.storage.StorageReference;

import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.data.local.PreferencesManager;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;

public class SecretRepository {

    //region Constructor

    private static final Object LOCK = new Object();
    private static SecretRepository instance;
    private FirestoreManager firestoreManager;
    private StorageManager storageManager;
    private PreferencesManager preferencesManager;

    public synchronized static SecretRepository getInstance(
            FirestoreManager firestoreManager, StorageManager storageManager, PreferencesManager preferencesManager) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new SecretRepository(firestoreManager, storageManager, preferencesManager);
            }
        }
        return instance;
    }

    private SecretRepository(FirestoreManager firestoreManager, StorageManager storageManager, PreferencesManager preferencesManager) {
        this.firestoreManager = firestoreManager;
        this.storageManager = storageManager;
        this.preferencesManager = preferencesManager;
    }

    //endregion
    public MutableLiveData<Drink> getDrink(String drinkID){
        return firestoreManager.getDrinkv2(drinkID);
    }

    public void setGeoNotificationTimestamp(long timestamp){
        preferencesManager.setGeoNotificationTimestamp(timestamp);
    }

    private long getGeoNotificationTimestamp(){
        return preferencesManager.getGeoNotificationTimestamp();
    }

    //region Images references

    public StorageReference getDrinkImageReference(String drinkID){
        return storageManager.getReferenceToDrinkImage(drinkID);
    }

    //endregion
}
