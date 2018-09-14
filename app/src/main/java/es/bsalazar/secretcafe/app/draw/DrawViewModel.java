package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.SecretRepository;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.utils.ResultState;

/**
 * Created by borja.salazar on 27/08/2018.
 */

public class DrawViewModel extends BaseViewModel {

    private MutableLiveData<List<String>> IMEIs = new MutableLiveData<List<String>>() {
    };

    private MutableLiveData<ResultState> drawResult = new MutableLiveData<ResultState>() {
    };

    DrawViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
        this.firestoreManager = firestoreManager;
    }

    void obtainImeis() {
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

    void drawDiscounts(int winnerNumbers, List<String> imeis) {
        imeis.addAll(IMEIs.getValue());
        ArrayList<String> winnersIMEI = new ArrayList<>();
        double percent = (winnerNumbers * 100d) / imeis.size();
        Random rand = new Random();

        for (int i = 0; i < imeis.size(); i++) {
            int restantes = imeis.size() - i;

            if (winnersIMEI.size() < winnerNumbers) {
                if (winnersIMEI.size() + restantes == winnerNumbers) {
                    winnersIMEI.add(imeis.get(i));
                } else {
                    int n = rand.nextInt(100);
                    if (n < percent)
                        winnersIMEI.add(imeis.get(i));
                }
            }
        }

        Date date_expired = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date_expired);
        calendar.add(Calendar.MONTH, 1);
        long expiredTimetamp = calendar.getTimeInMillis();

        ArrayList<Winner> winners = new ArrayList<>();
        for (String winnerImei : winnersIMEI) {
            winners.add(new Winner(winnerImei, UUID.randomUUID().toString(), expiredTimetamp));
        }

        firestoreManager.saveWinners(winners, isSucces -> drawResult.setValue(isSucces ? ResultState.OK : ResultState.KO));
    }

    MutableLiveData<List<String>> getIMEIsList() {
        return IMEIs;
    }

    MutableLiveData<ResultState> getDrawResult() {
        return drawResult;
    }
}
