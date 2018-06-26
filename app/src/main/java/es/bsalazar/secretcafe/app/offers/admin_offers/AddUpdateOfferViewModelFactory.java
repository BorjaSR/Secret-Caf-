package es.bsalazar.secretcafe.app.offers.admin_offers;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 25/04/2018.
 */

public class AddUpdateOfferViewModelFactory extends BaseViewModelFactory {

    public AddUpdateOfferViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddUpdateOfferViewModel(firestoreManager);
    }
}
