package es.bsalazar.secretcafe.app.events;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.remote.FirebaseResponse;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class EventsViewModel extends BaseViewModel {

    private MutableLiveData<List<Event>> eventList = new MutableLiveData<List<Event>>() {
    };
    private MutableLiveData<FirebaseResponse<Event>> addEventResponse = new MutableLiveData<FirebaseResponse<Event>>() {
    };
    private MutableLiveData<FirebaseResponse<Event>> modifyEventResponse = new MutableLiveData<FirebaseResponse<Event>>() {
    };
    private MutableLiveData<FirebaseResponse<Event>> removeEventResponse = new MutableLiveData<FirebaseResponse<Event>>() {
    };
    private MutableLiveData<ResultState> deleteEventResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ShowState> emptyList = new MutableLiveData<ShowState>() {
    };

    private boolean initialize;

    EventsViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void loadEvents() {
        initialize = true;

        loadingProgress.setValue(ShowState.SHOW);
        emptyList.setValue(ShowState.HIDE);
        firestoreManager.getEvents(new FirestoreManager.OnCollectionChangedListener<Event>() {
            @Override
            public void onCollectionChange(List<Event> collection) {
                if (initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    if (collection.size() > 0)
                        eventList.setValue(collection);
                    else
                        emptyList.setValue(ShowState.SHOW);

                    initialize = false;
                }
            }

            @Override
            public void onDocumentAdded(int index, Event object) {
                if (!initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    emptyList.setValue(ShowState.HIDE);
                    FirebaseResponse<Event> addEvent = new FirebaseResponse<>(index, object);
                    addEventResponse.setValue(addEvent);
                }
            }

            @Override
            public void onDocumentChanged(int index, Event object) {
                if (!initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    FirebaseResponse<Event> modifyEvent = new FirebaseResponse<>(index, object);
                    modifyEventResponse.setValue(modifyEvent);
                }
            }

            @Override
            public void onDocumentRemoved(int index, Event object) {
                if (!initialize) {
                    loadingProgress.setValue(ShowState.HIDE);
                    FirebaseResponse<Event> removeEvent = new FirebaseResponse<>(index, object);
                    removeEventResponse.setValue(removeEvent);
                }

            }
        });
    }

    void deleteEvent(String eventID) {
        loadingProgress.setValue(ShowState.SHOW);
        deleteEventResult.setValue(ResultState.LOADING);

        firestoreManager.deleteEvent(eventID);
        StorageManager.getInstance().deleteEventImage(eventID, () -> {
            loadingProgress.setValue(ShowState.HIDE);
            deleteEventResult.setValue(ResultState.OK);
        });
    }

    MutableLiveData<FirebaseResponse<Event>> getAddEventResponse() {
        return addEventResponse;
    }

    MutableLiveData<FirebaseResponse<Event>> getModifyEventResponse() {
        return modifyEventResponse;
    }

    MutableLiveData<FirebaseResponse<Event>> getRemoveEventResponse() {
        return removeEventResponse;
    }

    MutableLiveData<List<Event>> getEventList() {
        return eventList;
    }

    MutableLiveData<ResultState> getDeleteEventResult() {
        return deleteEventResult;
    }

    MutableLiveData<ShowState> getEmptyList() {
        return emptyList;
    }
}
