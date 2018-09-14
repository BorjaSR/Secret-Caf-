package es.bsalazar.secretcafe.app.discounts;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.bsalazar.secretcafe.app.base.BaseViewModelFactory;
import es.bsalazar.secretcafe.app.draw.DrawViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 07/09/2018.
 */

public class DiscountViewModelFactory extends BaseViewModelFactory {

    public DiscountViewModelFactory(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DiscountsViewModel(firestoreManager);
    }
}
