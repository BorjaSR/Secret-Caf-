package es.bsalazar.secretcafe.app.discounts;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import java.util.List;

import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.SecretRepository;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.utils.ShowState;

/**
 * Created by borja.salazar on 07/09/2018.
 */

public class DiscountsViewModel extends BaseViewModel{


    private MutableLiveData<List<Winner>> discounts = new MutableLiveData<List<Winner>>() {
    };

    DiscountsViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void getMyDiscounts(String instanceID){
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.getDiscountsByInstanceID(instanceID, collection -> {
            loadingProgress.setValue(ShowState.HIDE);
            discounts.setValue(collection);
        });
    }

    MutableLiveData<List<Winner>> getDiscounts() {
        return discounts;
    }
}
