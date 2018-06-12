package es.bsalazar.secretcafe.app.drinks;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.bsalazar.secretcafe.data.FirebaseResponse;
import es.bsalazar.secretcafe.data.FirestoreManager;
import es.bsalazar.secretcafe.data.StorageManager;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class DrinksViewModel extends BaseViewModel {

    private MutableLiveData<List<Drink>> drinksList = new MutableLiveData<List<Drink>>() {
    };
    private MutableLiveData<FirebaseResponse<Drink>> addDrinkResponse = new MutableLiveData<FirebaseResponse<Drink>>() {
    };
    private MutableLiveData<FirebaseResponse<Drink>> modifyDrinkResponse = new MutableLiveData<FirebaseResponse<Drink>>() {
    };
    private MutableLiveData<FirebaseResponse<Drink>> removeDrinkResponse = new MutableLiveData<FirebaseResponse<Drink>>() {
    };
    private MutableLiveData<ResultState> deleteDrinkResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ShowState> emptyList = new MutableLiveData<ShowState>() {
    };
    private boolean initialize;

    DrinksViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void loadDrinks() {
        initialize = true;
        loadingProgress.setValue(ShowState.SHOW);
        emptyList.setValue(ShowState.HIDE);

        firestoreManager.getDrinks(new FirestoreManager.OnCollectionChangedListener<Drink>() {
            @Override
            public void onCollectionChange(List<Drink> collection) {
                if (initialize){
                    loadingProgress.setValue(ShowState.HIDE);
                    if (collection.size() > 0)
                        drinksList.setValue(collection);
                    else
                        emptyList.setValue(ShowState.SHOW);
                    initialize = false;
                }
            }

            @Override
            public void onDocumentAdded(int index, Drink object) {
                if(!initialize) {
                    FirebaseResponse<Drink> addMeal = new FirebaseResponse<>(index, object);
                    addDrinkResponse.setValue(addMeal);
                }
            }

            @Override
            public void onDocumentChanged(int index, Drink object) {
                if(!initialize) {
                    FirebaseResponse<Drink> addMeal = new FirebaseResponse<>(index, object);
                    modifyDrinkResponse.setValue(addMeal);
                }
            }

            @Override
            public void onDocumentRemoved(int index, Drink object) {
                if(!initialize) {
                    FirebaseResponse<Drink> addMeal = new FirebaseResponse<>(index, object);
                    removeDrinkResponse.setValue(addMeal);
                }
            }
        });
    }


    void deleteDrink(String drinkID) {
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.deleteDrink(drinkID);
        deleteDrinkResult.setValue(ResultState.LOADING);
        StorageManager.getInstance().deleteDrinkImage(drinkID, () -> {
            loadingProgress.setValue(ShowState.HIDE);
            deleteDrinkResult.setValue(ResultState.OK);
        });
    }


    MutableLiveData<List<Drink>> getDrinksList() {
        return drinksList;
    }

    MutableLiveData<ResultState> getDeleteDrinkResult() {
        return deleteDrinkResult;
    }

    MutableLiveData<FirebaseResponse<Drink>> getAddDrinkResponse() {
        return addDrinkResponse;
    }

    MutableLiveData<FirebaseResponse<Drink>> getModifyDrinkResponse() {
        return modifyDrinkResponse;
    }

    MutableLiveData<FirebaseResponse<Drink>> getRemoveDrinkResponse() {
        return removeDrinkResponse;
    }
}
