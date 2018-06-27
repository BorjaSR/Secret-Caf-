package es.bsalazar.secretcafe.app.drinks.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.firebase.storage.StorageReference;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.SecretRepository;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;


class DrinkDetailViewModel extends BaseViewModel {

    private SecretRepository secretRepository;

    DrinkDetailViewModel(SecretRepository secretRepository, FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.secretRepository = secretRepository;
    }

    LiveData<Drink> getDrink(String drinkID) {
        return secretRepository.getDrink(drinkID);
    }

}
