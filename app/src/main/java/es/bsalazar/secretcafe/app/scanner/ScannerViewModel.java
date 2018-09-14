package es.bsalazar.secretcafe.app.scanner;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.List;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.utils.ResultState;

class ScannerViewModel extends BaseViewModel {

    private MutableLiveData<ResultState> resultChangeDiscount = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<Winner> discount = new MutableLiveData<Winner>() {
    };

    ScannerViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void checkDiscount(String discountCode){
        firestoreManager.getDiscountsByCode(discountCode, document -> {
            discount.setValue(document);
        });
    }

    void changeDiscount(){
        firestoreManager.changeDiscountStatus(discount.getValue(), document -> {
            if(document != null)
                resultChangeDiscount.setValue(ResultState.OK);
            else
                resultChangeDiscount.setValue(ResultState.KO);
        });
    }

    MutableLiveData<Winner> getDiscount() {
        return discount;
    }

    public MutableLiveData<ResultState> getResultChangeDiscount() {
        return resultChangeDiscount;
    }
}
