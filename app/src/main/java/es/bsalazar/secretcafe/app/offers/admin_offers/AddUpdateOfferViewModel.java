package es.bsalazar.secretcafe.app.offers.admin_offers;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class AddUpdateOfferViewModel extends BaseViewModel {


    private MutableLiveData<Offer> offerToEdit = new MutableLiveData<Offer>() {
    };
    private MutableLiveData<ResultState> saveOfferResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ResultState> editOfferResult = new MutableLiveData<ResultState>() {
    };

    AddUpdateOfferViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void saveOffer(Offer offer, final Uri imagePath) {

        if (imagePath != null)
            offer.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        FirestoreManager.getInstance().saveOffer(offer, document -> {

            if (document != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadOfferImage(imagePath, document.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveOfferResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveOfferResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    saveOfferResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                saveOfferResult.setValue(ResultState.KO);
            }
        });
    }

    void getOffer(String offerID) {
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.getOffer(offerID, document -> {
            loadingProgress.setValue(ShowState.HIDE);
            offerToEdit.setValue(document);
        });
    }

    void updateOffer(Offer offer, final Uri imagePath) {

        if (imagePath != null)
            offer.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.updateOffer(offer, responseDocument -> {

            if (responseDocument != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadOfferImage(imagePath, offer.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editOfferResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editOfferResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    editOfferResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                editOfferResult.setValue(ResultState.KO);
            }
        });
    }

    MutableLiveData<Offer> getOfferToEdit() {
        return offerToEdit;
    }

    MutableLiveData<ResultState> getSaveOfferResult() {
        return saveOfferResult;
    }

    MutableLiveData<ResultState> getEditOfferResult() {
        return editOfferResult;
    }
}
