package es.bsalazar.secretcafe.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import es.bsalazar.secretcafe.BuildConfig;

public class StorageManager {

    private String CATEGORIES_FOLDER = BuildConfig.FIREBASE_PREFIX + "categories/";
    private String DRINKS_FOLDER = BuildConfig.FIREBASE_PREFIX + "drinks/";
    private String MEALS_FOLDER = BuildConfig.FIREBASE_PREFIX + "meals/";
    private String EVENTS_FOLDER = BuildConfig.FIREBASE_PREFIX + "events/";
    private String OFFERS_FOLDER = BuildConfig.FIREBASE_PREFIX + "offers/";
    private String JPG = ".jpg";

    private StorageReference reference;
    private static StorageManager instance;

    public static StorageManager getInstance() {
        if (instance == null)
            instance = new StorageManager();
        return instance;
    }

    private StorageManager() {
        // Create a storage reference from our app
        reference = FirebaseStorage.getInstance().getReference();
    }

    //region Get Images
    public StorageReference getReferenceToCategoryImage(String fileName) {
        return reference.child(CATEGORIES_FOLDER + fileName + JPG);
    }

    public StorageReference getReferenceToDrinkImage(String fileName) {
        return reference.child(DRINKS_FOLDER + fileName + JPG);
    }

    public StorageReference getReferenceToMealImage(String fileName) {
        return reference.child(MEALS_FOLDER + fileName + JPG);
    }

    public StorageReference getReferenceToEventImage(String fileName) {
        return reference.child(EVENTS_FOLDER + fileName + JPG);
    }

    public StorageReference getReferenceToOfferImage(String fileName) {
        return reference.child(OFFERS_FOLDER + fileName + JPG);
    }
    //endregion

    //region Upload Images
    public void uploadCategoryImage(Uri filePath, String fileName, OnUploadImageListener listener) {
        uploadImage(CATEGORIES_FOLDER, filePath, fileName, listener);
    }

    public void uploadDrinkImage(Uri filePath, String fileName, OnUploadImageListener listener) {
        uploadImage(DRINKS_FOLDER, filePath, fileName, listener);
    }

    public void uploadMealImage(Uri filePath, String fileName, OnUploadImageListener listener) {
        uploadImage(MEALS_FOLDER, filePath, fileName, listener);
    }

    public void uploadEventImage(Uri filePath, String fileName, OnUploadImageListener listener) {
        uploadImage(EVENTS_FOLDER, filePath, fileName, listener);
    }

    public void uploadOfferImage(Uri filePath, String fileName, OnUploadImageListener listener) {
        uploadImage(OFFERS_FOLDER, filePath, fileName, listener);
    }

    private void uploadImage(String folder, Uri filePath, String fileName, final OnUploadImageListener listener) {
        StorageReference ref = reference.child(folder + fileName + JPG);
        ref.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> listener.onImageUploadedSuccess())
                .addOnFailureListener(e -> listener.onImageUploadedFailure())
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    Log.d("[IMAGE UPDAET PROGRESS]", String.valueOf(progress));
                    listener.onImageUploadedProgress(progress);
                });
    }
    //endregion

    //region Delete Images
    public void deleteCategoryImage(String fileName, OnDeleteImageListener listener) {
        deleteImage(CATEGORIES_FOLDER, fileName, listener);
    }

    public void deleteDrinkImage(String fileName, OnDeleteImageListener listener) {
        deleteImage(DRINKS_FOLDER, fileName, listener);
    }

    public void deleteMealImage(String fileName, OnDeleteImageListener listener) {
        deleteImage(MEALS_FOLDER, fileName, listener);
    }

    public void deleteEventImage(String fileName, OnDeleteImageListener listener) {
        deleteImage(EVENTS_FOLDER, fileName, listener);
    }

    public void deleteOfferImage(String fileName, OnDeleteImageListener listener) {
        deleteImage(OFFERS_FOLDER, fileName, listener);
    }

    private void deleteImage(String folder, String fileName, final OnDeleteImageListener listener) {
        StorageReference deleteFile = reference.child(folder + fileName + JPG);
        deleteFile.delete()
                .addOnSuccessListener(aVoid -> listener.onImageDeletedSuccess())
                .addOnFailureListener(e -> listener.onImageDeletedSuccess());
    }
    //endregion

    //region Interfaces
    public interface OnUploadImageListener {
        void onImageUploadedSuccess();

        void onImageUploadedFailure();

        void onImageUploadedProgress(double progress);
    }

    public interface OnDeleteImageListener {
        void onImageDeletedSuccess();
    }
    //endregion
}
