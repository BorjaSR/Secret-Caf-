package es.bsalazar.secretcafe.app.offers;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirebaseResponse;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class OffersViewModel extends BaseViewModel {

    private boolean initialize;

    private MutableLiveData<List<Offer>> offersList = new MutableLiveData<List<Offer>>() {
    };
    private MutableLiveData<FirebaseResponse<Offer>> addOfferResponse = new MutableLiveData<FirebaseResponse<Offer>>() {
    };
    private MutableLiveData<FirebaseResponse<Offer>> modifyOfferResponse = new MutableLiveData<FirebaseResponse<Offer>>() {
    };
    private MutableLiveData<FirebaseResponse<Offer>> removeOfferResponse = new MutableLiveData<FirebaseResponse<Offer>>() {
    };
    private MutableLiveData<ResultState> deleteOfferResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ShowState> emptyList = new MutableLiveData<ShowState>() {
    };

    OffersViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void loadOffers() {
        initialize = true;

        loadingProgress.setValue(ShowState.SHOW);
        emptyList.setValue(ShowState.HIDE);
        firestoreManager.getOffers(new FirestoreManager.OnCollectionChangedListener<Offer>() {
            @Override
            public void onCollectionChange(List<Offer> collection) {
                if(initialize){
                    loadingProgress.setValue(ShowState.HIDE);
                    if (collection.size() > 0)
                        offersList.setValue(collection);
                    else
                        emptyList.setValue(ShowState.SHOW);

                    initialize = false;
                }
            }

            @Override
            public void onDocumentAdded(int index, Offer object) {
                if(!initialize){
                    loadingProgress.setValue(ShowState.HIDE);
                    emptyList.setValue(ShowState.HIDE);
                    FirebaseResponse<Offer> addOffer = new FirebaseResponse<>(index, object);
                    addOfferResponse.setValue(addOffer);
                }
            }

            @Override
            public void onDocumentChanged(int index, Offer object) {
                if(!initialize){
                    loadingProgress.setValue(ShowState.HIDE);
                    FirebaseResponse<Offer> modifyOffer = new FirebaseResponse<>(index, object);
                    modifyOfferResponse.setValue(modifyOffer);
                }
            }

            @Override
            public void onDocumentRemoved(int index, Offer object) {
                if(!initialize){
                    loadingProgress.setValue(ShowState.HIDE);
                    FirebaseResponse<Offer> removeOffer = new FirebaseResponse<>(index, object);
                    removeOfferResponse.setValue(removeOffer);
                }
            }
        });
    }


    void deleteOffer(String offerID) {
        loadingProgress.setValue(ShowState.SHOW);
        deleteOfferResult.setValue(ResultState.LOADING);

        firestoreManager.deleteOffer(offerID);
        StorageManager.getInstance().deleteOfferImage(offerID, () -> {
            loadingProgress.setValue(ShowState.HIDE);
            deleteOfferResult.setValue(ResultState.OK);
        });
    }

    public MutableLiveData<List<Offer>> getOffersList() {
        return offersList;
    }

    public MutableLiveData<FirebaseResponse<Offer>> getAddOfferResponse() {
        return addOfferResponse;
    }

    public MutableLiveData<FirebaseResponse<Offer>> getModifyOfferResponse() {
        return modifyOfferResponse;
    }

    public MutableLiveData<FirebaseResponse<Offer>> getRemoveOfferResponse() {
        return removeOfferResponse;
    }

    public MutableLiveData<ResultState> getDeleteOfferResult() {
        return deleteOfferResult;
    }

    public MutableLiveData<ShowState> getEmptyList() {
        return emptyList;
    }
}
