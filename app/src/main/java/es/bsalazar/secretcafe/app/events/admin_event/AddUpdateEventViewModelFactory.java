package es.bsalazar.secretcafe.app.events.admin_event;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 19/04/2018.
 */

public class AddUpdateEventViewModelFactory extends BaseViewModelFactory {

    public AddUpdateEventViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddUpdateEventViewModel(firestoreManager);
    }
}
