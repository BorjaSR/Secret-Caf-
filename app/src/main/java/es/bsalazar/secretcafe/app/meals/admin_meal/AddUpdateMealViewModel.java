package es.bsalazar.secretcafe.app.meals.admin_meal;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.FirestoreManager;
import es.bsalazar.secretcafe.data.StorageManager;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class AddUpdateMealViewModel extends BaseViewModel {

    private MutableLiveData<ResultState> saveMealResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ResultState> editMealResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<Meal> mealToEdit = new MutableLiveData<Meal>() {
    };

    AddUpdateMealViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void saveMeal(Meal meal, final Uri imagePath) {

        if (imagePath != null)
            meal.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        FirestoreManager.getInstance().saveMeal(meal, responseMeal -> {

            if (responseMeal != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadMealImage(imagePath, responseMeal.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveMealResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveMealResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    saveMealResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                saveMealResult.setValue(ResultState.KO);
            }
        });
    }

    void getMeal(String mealID) {
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.getMeal(mealID, meal -> {
            loadingProgress.setValue(ShowState.HIDE);
            mealToEdit.setValue(meal);
        });
    }

    void updateMeal(Meal meal, final Uri imagePath) {

        if (imagePath != null)
            meal.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.updateMeal(meal, responseMeal -> {

            if (responseMeal != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadMealImage(imagePath, meal.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editMealResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editMealResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    editMealResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                editMealResult.setValue(ResultState.KO);
            }
        });
    }

    MutableLiveData<ResultState> getSaveMealResult() {
        return saveMealResult;
    }

    MutableLiveData<ResultState> getEditMealResult() {
        return editMealResult;
    }

    MutableLiveData<Meal> getMealToEdit() {
        return mealToEdit;
    }
}
