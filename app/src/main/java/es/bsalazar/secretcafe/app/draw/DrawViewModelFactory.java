package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 27/08/2018.
 */

public class DrawViewModelFactory extends BaseViewModelFactory{

    private FirestoreManager firestoreManager;

    public DrawViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.firestoreManager = firestoreManager;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DrawViewModel(firestoreManager);
    }
}
