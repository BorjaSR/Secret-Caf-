package es.bsalazar.secretcafe.app.discounts.detail;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 14/09/2018.
 */

public class DiscountDetailViewModel extends BaseViewModel {

    private MutableLiveData<Integer> discountStatus = new MutableLiveData<Integer>() {
    };

    DiscountDetailViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void observeDiscountStatus(String discountID) {
        firestoreManager.getDiscountRealtime(discountID, object -> discountStatus.setValue(object.getStatus()));
    }

    MutableLiveData<Integer> getDiscountStatus() {
        return discountStatus;
    }
}
