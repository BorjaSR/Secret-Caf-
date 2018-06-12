package es.bsalazar.secretcafe.app.base;

import android.arch.lifecycle.ViewModelProvider;

import es.bsalazar.secretcafe.data.FirestoreManager;

/**
 * Created by borja.salazar on 12/04/2018.
 */

public class BaseViewModelFactory extends ViewModelProvider.NewInstanceFactory  {

    protected FirestoreManager firestoreManager;

    public BaseViewModelFactory(FirestoreManager firestoreManager) {
        this.firestoreManager = firestoreManager;
    }
}
