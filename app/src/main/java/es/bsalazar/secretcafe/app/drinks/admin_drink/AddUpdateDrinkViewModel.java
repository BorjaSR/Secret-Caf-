package es.bsalazar.secretcafe.app.drinks.admin_drink;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.FirestoreManager;
import es.bsalazar.secretcafe.data.StorageManager;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class AddUpdateDrinkViewModel extends BaseViewModel {

    private MutableLiveData<ResultState> saveDrinkResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ResultState> editDrinkResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<Drink> drinkToEdit = new MutableLiveData<Drink>() {
    };

    AddUpdateDrinkViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void saveDrink(Drink drink, final Uri imagePath) {

        if (imagePath != null)
            drink.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        FirestoreManager.getInstance().saveDrink(drink, responseDrink -> {

            if (responseDrink != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadDrinkImage(imagePath, responseDrink.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveDrinkResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveDrinkResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    saveDrinkResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                saveDrinkResult.setValue(ResultState.KO);
            }
        });
    }

    void getDrink(String drinkID) {
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.getDrink(drinkID, drink -> {
            loadingProgress.setValue(ShowState.HIDE);
            drinkToEdit.setValue(drink);
        });
    }

    void updateDrink(Drink drink, final Uri imagePath) {

        if (imagePath != null)
            drink.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.updateDrink(drink, responseDrink -> {

            if (responseDrink != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadDrinkImage(imagePath, drink.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editDrinkResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editDrinkResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    editDrinkResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                editDrinkResult.setValue(ResultState.KO);
            }
        });
    }

    MutableLiveData<ResultState> getSaveDrinkResult() {
        return saveDrinkResult;
    }

    MutableLiveData<ResultState> getEditDrinkResult() {
        return editDrinkResult;
    }

    MutableLiveData<Drink> getDrinkToEdit() {
        return drinkToEdit;
    }
}
