package es.bsalazar.secretcafe.app.draw;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;

/**
 * Created by borja.salazar on 27/08/2018.
 */

public class DrawViewModel extends BaseViewModel {

    private FirestoreManager firestoreManager;

    DrawViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.firestoreManager = firestoreManager;
    }
}
