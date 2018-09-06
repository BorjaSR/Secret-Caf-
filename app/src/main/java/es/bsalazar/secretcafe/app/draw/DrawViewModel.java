package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.SecretRepository;
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

    void drawDiscounts(int winnerNumbers, List<String> imeis){
        imeis.addAll(IMEIs.getValue());
        ArrayList<String> winners = new ArrayList<>();
        double percent = (winnerNumbers * 100d) / imeis.size();
        Random rand = new Random();

        for (int i = 0; i < imeis.size(); i++){
            int restantes = imeis.size() - i;

            if(winners.size() < winnerNumbers){
                if (winners.size() + restantes == winnerNumbers){
                    winners.add(imeis.get(i));
                } else {
                    int n = rand.nextInt(100);
                    if(n < percent)
                        winners.add(imeis.get(i));
                }
            }
        }



        Log.d("Resultado de sorteo", winners.toString());
    }

    MutableLiveData<List<String>> getIMEIsList() {
        return IMEIs;
    }
}
