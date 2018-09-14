package es.bsalazar.secretcafe.app.discounts.detail;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.app.discounts.DiscountsViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 14/09/2018.
 */

public class DiscountDetailViewModelFactory extends BaseViewModelFactory {

    public DiscountDetailViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DiscountDetailViewModel(firestoreManager);
    }
}
