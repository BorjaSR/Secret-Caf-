package es.bsalazar.secretcafe.app.drinks;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.FirestoreManager;

/**
 * Created by borja.salazar on 03/04/2018.
 */

public class DrinksViewModelFactory extends BaseViewModelFactory {

    public DrinksViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DrinksViewModel(firestoreManager);
    }
}
