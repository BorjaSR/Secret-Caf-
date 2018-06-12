package es.bsalazar.secretcafe.app.meals;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.FirestoreManager;

/**
 * Created by borja.salazar on 13/04/2018.
 */

public class MealsViewModelFactory extends BaseViewModelFactory {

    public MealsViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MealsViewModel(firestoreManager);
    }
}
