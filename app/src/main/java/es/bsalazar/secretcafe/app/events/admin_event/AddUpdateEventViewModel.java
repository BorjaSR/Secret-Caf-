package es.bsalazar.secretcafe.app.events.admin_event;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import java.util.Calendar;
import java.util.Date;

import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.entities.Notification;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

class AddUpdateEventViewModel extends BaseViewModel {

    private MutableLiveData<Event> eventToEdit = new MutableLiveData<Event>() {
    };
    private MutableLiveData<ResultState> saveEventResult = new MutableLiveData<ResultState>() {
    };
    private MutableLiveData<ResultState> editEventResult = new MutableLiveData<ResultState>() {
    };
    private boolean notify = false;

    AddUpdateEventViewModel(FirestoreManager firestoreManager) {
        super(firestoreManager);
    }

    void saveEvent(Event event, final Uri imagePath) {

        if (imagePath != null)
            event.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        FirestoreManager.getInstance().saveEvent(event, document -> {

            if (document != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadEventImage(imagePath, document.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveEventResult.setValue(ResultState.OK);
                            if(notify) notifyEvent(event);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            saveEventResult.setValue(ResultState.OK);
                            if(notify) notifyEvent(event);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    saveEventResult.setValue(ResultState.OK);
                    if(notify) notifyEvent(event);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                saveEventResult.setValue(ResultState.KO);
            }
        });
    }

    void getEvent(String eventID) {
        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.getEvent(eventID, event -> {
            loadingProgress.setValue(ShowState.HIDE);
            eventToEdit.setValue(event);
        });
    }

    void updateEvent(Event event, final Uri imagePath) {

        if (imagePath != null)
            event.setDateImageUpdate(System.currentTimeMillis());

        loadingProgress.setValue(ShowState.SHOW);
        firestoreManager.updateEvent(event, responseDocument -> {

            if (responseDocument != null) {
                if (imagePath != null) {
                    StorageManager.getInstance().uploadEventImage(imagePath, event.getId(), new StorageManager.OnUploadImageListener() {
                        @Override
                        public void onImageUploadedSuccess() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editEventResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedFailure() {
                            loadingProgress.setValue(ShowState.HIDE);
                            editEventResult.setValue(ResultState.OK);
                        }

                        @Override
                        public void onImageUploadedProgress(double progress) {
                        }
                    });
                } else {
                    loadingProgress.setValue(ShowState.HIDE);
                    editEventResult.setValue(ResultState.OK);

                }
            } else {
                loadingProgress.setValue(ShowState.HIDE);
                editEventResult.setValue(ResultState.KO);
            }
        });
    }

    private void notifyEvent(Event event){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(Calendar.MONTH, 1);

        Notification notification = new Notification();
        notification.setTitle(event.getName());
        notification.setDescription(event.getDescription());
        notification.setType(Notification.EVENT);
        notification.setElementId(event.getId());
        notification.setExpiredDate(calendar.getTimeInMillis());

        firestoreManager.saveNotification(notification, document -> {
            //Nothing to do
        });
    }

    MutableLiveData<Event> getEventToEdit() {
        return eventToEdit;
    }

    MutableLiveData<ResultState> getSaveEventResult() {
        return saveEventResult;
    }

    MutableLiveData<ResultState> getEditEventResult() {
        return editEventResult;
    }

    void setNotify(boolean notify) {
        this.notify = notify;
    }
}
