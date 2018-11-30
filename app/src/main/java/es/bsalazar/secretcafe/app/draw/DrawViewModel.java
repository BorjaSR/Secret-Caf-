package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.utils.ResultState;

/**
 * Created by borja.salazar on 27/08/2018.
 */

public class DrawViewModel extends BaseViewModel {

    private MutableLiveData<List<String>> instanceIDs = new MutableLiveData<List<String>>() {
    };

    private MutableLiveData<ResultState> drawResult = new MutableLiveData<ResultState>() {
    };

    DrawViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.firestoreManager = firestoreManager;
    }

    void obtainImeis() {
        firestoreManager.getInstanceID(new FirestoreManager.OnCollectionChangedListener<String>() {
            @Override
            public void onCollectionChange(List<String> collection) {
                instanceIDs.setValue(collection);
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

    void drawDiscounts(int winnerNumbers, List<String> instanceIDs) {
        instanceIDs.addAll(this.instanceIDs.getValue());
        ArrayList<String> winnersInstanceIDs = new ArrayList<>();
        double percent = (winnerNumbers * 100d) / instanceIDs.size();
        Random rand = new Random();

        for (int i = 0; i < instanceIDs.size(); i++) {
            int restantes = instanceIDs.size() - i;

            if (winnersInstanceIDs.size() < winnerNumbers) {
                if (winnersInstanceIDs.size() + restantes == winnerNumbers) {
                    winnersInstanceIDs.add(instanceIDs.get(i));
                } else {
                    int n = rand.nextInt(100);
                    if (n < percent)
                        winnersInstanceIDs.add(instanceIDs.get(i));
                }
            }
        }

        Date date_expired = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date_expired);
        calendar.add(Calendar.MONTH, 1);
        long expiredTimetamp = calendar.getTimeInMillis();

        ArrayList<Winner> winners = new ArrayList<>();
        for (String winnerInstanceID : winnersInstanceIDs) {
            winners.add(new Winner(winnerInstanceID, UUID.randomUUID().toString(), expiredTimetamp));
        }

        firestoreManager.saveWinners(winners, isSucces -> drawResult.setValue(isSucces ? ResultState.OK : ResultState.KO));
    }

    MutableLiveData<List<String>> getIMEIsList() {
        return instanceIDs;
    }

    MutableLiveData<ResultState> getDrawResult() {
        return drawResult;
    }
}
