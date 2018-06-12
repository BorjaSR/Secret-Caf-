package es.bsalazar.secretcafe.app.home;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.FirebaseResponse;
import es.bsalazar.secretcafe.data.FirestoreManager;
import es.bsalazar.secretcafe.data.entities.Category;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.utils.ShowState;

/**
 * Created by borja.salazar on 12/04/2018.
 */

class HomeViewModel extends BaseViewModel {

    private MutableLiveData<List<Category>> categoriesList = new MutableLiveData<List<Category>>() {
    };
    private MutableLiveData<FirebaseResponse<Category>> modifyCategoryResponse = new MutableLiveData<FirebaseResponse<Category>>() {
    };

    HomeViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    private boolean initialize;

    void loadCategories() {
        initialize = true;
        loadingProgress.setValue(ShowState.SHOW);
        FirestoreManager.getInstance().getCategories(new FirestoreManager.OnCollectionChangedListener<Category>() {
            @Override
            public void onCollectionChange(List<Category> collection) {
                if (initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    categoriesList.setValue(collection);
                    initialize = false;
                }
            }

            @Override
            public void onDocumentAdded(int index, Category object) {

            }

            @Override
            public void onDocumentChanged(int index, Category object) {
                if(!initialize){
                    modifyCategoryResponse.setValue(new FirebaseResponse<>(index, object));
                }
            }

            @Override
            public void onDocumentRemoved(int index, Category object) {

            }
        });
    }

    MutableLiveData<List<Category>> getCategories() {
        return categoriesList;
    }

    public MutableLiveData<FirebaseResponse<Category>> getModifyCategoryResponse() {
        return modifyCategoryResponse;
    }
}
