package es.bsalazar.secretcafe.app.drinks.detail;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.SecretRepository;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

public class DrinkDetailViewModelFactory extends BaseViewModelFactory {

    private SecretRepository secretRepository;

    public DrinkDetailViewModelFactory(SecretRepository secretRepository, FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.secretRepository = secretRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DrinkDetailViewModel(secretRepository, firestoreManager);
    }
}
