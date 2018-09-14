package es.bsalazar.secretcafe.app.scanner;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.app.offers.OffersViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 12/09/2018.
 */

public class ScannerViewModelFactory extends BaseViewModelFactory {

    public ScannerViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ScannerViewModel(firestoreManager);
    }
}
