package es.bsalazar.secretcafe.app.meals;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirebaseResponse;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

/*
 * Created by Borja Salazar on 23/10/2018
 */

class MealsViewModel extends BaseViewModel {

    private MutableLiveData<List<Meal>> mealList = new MutableLiveData<List<Meal>>() {
    };
    private MutableLiveData<FirebaseResponse<Meal>> addMealResponse = new MutableLiveData<FirebaseResponse<Meal>>() {
    };
    private MutableLiveData<FirebaseResponse<Meal>> modifyMealResponse = new MutableLiveData<FirebaseResponse<Meal>>() {
    };
    private MutableLiveData<FirebaseResponse<Meal>> removeMealResponse = new MutableLiveData<FirebaseResponse<Meal>>() {
    };
    private MutableLiveData<ResultState> deleteMealResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ShowState> emptyList = new MutableLiveData<ShowState>() {
    };

    private boolean initialize;

    MealsViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void loadMeals() {
        initialize = true;

        loadingProgress.setValue(ShowState.SHOW);
        emptyList.setValue(ShowState.HIDE);

        firestoreManager.getMeals(new FirestoreManager.OnCollectionChangedListener<Meal>() {
            @Override
            public void onCollectionChange(List<Meal> collection) {
                if(initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    if (collection.size() > 0)
                        mealList.setValue(collection);
                    else
                        emptyList.setValue(ShowState.SHOW);

                    initialize = false;
                }
            }

            @Override
            public void onDocumentAdded(int index, Meal object) {
                if(!initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    emptyList.setValue(ShowState.HIDE);
                    FirebaseResponse<Meal> addMeal = new FirebaseResponse<>(index, object);
                    addMealResponse.setValue(addMeal);
                }
            }

            @Override
            public void onDocumentChanged(int index, Meal object) {
                if(!initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    FirebaseResponse<Meal> modifyMeal = new FirebaseResponse<>(index, object);
                    modifyMealResponse.setValue(modifyMeal);
                }
            }

            @Override
            public void onDocumentRemoved(int index, Meal object) {
                if(!initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    FirebaseResponse<Meal> removeMeal = new FirebaseResponse<>(index, object);
                    removeMealResponse.setValue(removeMeal);
                }
            }
        });
    }

    void deleteMeal(String mealID) {
        loadingProgress.setValue(ShowState.SHOW);

        firestoreManager.deleteMeal(mealID);
        StorageManager.getInstance().deleteMealImage(mealID, () -> {
            loadingProgress.setValue(ShowState.HIDE);
            deleteMealResult.setValue(ResultState.OK);
        });
    }

    MutableLiveData<List<Meal>> getMealList() {
        return mealList;
    }

    public MutableLiveData<FirebaseResponse<Meal>> getAddMealResponse() {
        return addMealResponse;
    }

    public MutableLiveData<FirebaseResponse<Meal>> getModifyMealResponse() {
        return modifyMealResponse;
    }

    public MutableLiveData<FirebaseResponse<Meal>> getRemoveMealResponse() {
        return removeMealResponse;
    }

    MutableLiveData<ResultState> getDeleteMealResult() {
        return deleteMealResult;
    }

    MutableLiveData<ShowState> getEmptyList() {
        return emptyList;
    }
}
