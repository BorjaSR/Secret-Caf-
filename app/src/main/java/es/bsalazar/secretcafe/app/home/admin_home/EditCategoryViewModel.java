package es.bsalazar.secretcafe.app.home.admin_home;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Category;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class EditCategoryViewModel extends BaseViewModel {

    private MutableLiveData<Category> categoryToEdit = new MutableLiveData<Category>(){};
    private MutableLiveData<ResultState> editCategoryResult = new MutableLiveData<ResultState>() {};

    EditCategoryViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void getCategory(String categoryID) {
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.getCategory(categoryID, category -> {
            loadingProgress.setValue(ShowState.HIDE);
            categoryToEdit.setValue(category);
        });
    }

    void updateCatagory(Category category, final Uri imagePath){
        loadingProgress.setValue(ShowState.SHOW);
        if (imagePath != null) {
            StorageManager.getInstance().uploadCategoryImage(imagePath, category.getId(), new StorageManager.OnUploadImageListener() {
                @Override
                public void onImageUploadedSuccess() {
                    category.setDateImageUpdate(System.currentTimeMillis());
                    FirestoreManager.getInstance().updateCategory(category, document -> {
                        loadingProgress.setValue(ShowState.HIDE);
                        editCategoryResult.setValue(ResultState.OK);
                    });
                }

                @Override
                public void onImageUploadedFailure() {
                    loadingProgress.setValue(ShowState.HIDE);
                    editCategoryResult.setValue(ResultState.KO);
                }

                @Override
                public void onImageUploadedProgress(double progress) {

                }
            });

        } else {
            FirestoreManager.getInstance().updateCategory(category, document -> {
                loadingProgress.setValue(ShowState.HIDE);
                editCategoryResult.setValue(ResultState.OK);
            });
        }
    }

    MutableLiveData<Category> getCategoryToEdit() {
        return categoryToEdit;
    }

    public MutableLiveData<ResultState> getEditCategoryResult() {
        return editCategoryResult;
    }
}
