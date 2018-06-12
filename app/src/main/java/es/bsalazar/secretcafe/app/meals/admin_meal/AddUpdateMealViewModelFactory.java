package es.bsalazar.secretcafe.app.meals.admin_meal;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.FirestoreManager;

public class AddUpdateMealViewModelFactory extends BaseViewModelFactory {

    public AddUpdateMealViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddUpdateMealViewModel(firestoreManager);
    }
}
