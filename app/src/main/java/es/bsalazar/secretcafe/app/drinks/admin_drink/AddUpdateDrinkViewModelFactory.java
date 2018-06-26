package es.bsalazar.secretcafe.app.drinks.admin_drink;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

public class AddUpdateDrinkViewModelFactory extends BaseViewModelFactory {

    public AddUpdateDrinkViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddUpdateDrinkViewModel(firestoreManager);
    }
}
