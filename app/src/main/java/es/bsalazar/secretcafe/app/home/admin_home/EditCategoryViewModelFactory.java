package es.bsalazar.secretcafe.app.home.admin_home;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 21/04/2018.
 */

public class EditCategoryViewModelFactory extends BaseViewModelFactory {

    public EditCategoryViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditCategoryViewModel(firestoreManager);
    }
}
