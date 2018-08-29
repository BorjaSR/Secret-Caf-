package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.MutableLiveData;
import java.util.List;
import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 27/08/2018.
 */

public class DrawViewModel extends BaseViewModel {

    private MutableLiveData<List<String>> IMEIs = new MutableLiveData<List<String>>() {
    };

    DrawViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.firestoreManager = firestoreManager;
    }

    void obtainImeis(){
        firestoreManager.getIMEIs(new FirestoreManager.OnCollectionChangedListener<String>() {
            @Override
            public void onCollectionChange(List<String> collection) {
                IMEIs.setValue(collection);
            }

            @Override
            public void onDocumentAdded(int index, String object) {

            }

            @Override
            public void onDocumentChanged(int index, String object) {

            }

            @Override
            public void onDocumentRemoved(int index, String object) {

            }
        });
    }

    MutableLiveData<List<String>> getIMEIsList() {
        return IMEIs;
    }
}
