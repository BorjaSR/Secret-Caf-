package es.bsalazar.secretcafe.data.remote;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.data.entities.Category;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.data.entities.Offer;

/**
 * Created by borja.salazar on 16/03/2018.
 */

public class FirestoreManager {

    private final String TAG = "Firestore Manager";
    private static final String CATEGORIES_COLLECTION = BuildConfig.FIREBASE_PREFIX + "Categories";
    public static final String DRINK_COLLECTION = BuildConfig.FIREBASE_PREFIX + "Drinks";
    public static final String MEAL_COLLECTION = BuildConfig.FIREBASE_PREFIX + "Meals";
    public static final String EVENTS_COLLECTION = BuildConfig.FIREBASE_PREFIX + "Events";
    public static final String OFFERS_COLLECTION = BuildConfig.FIREBASE_PREFIX + "Offers";

    private FirebaseFirestore db;
    private static FirestoreManager instance;


    //region Constructor
    public static FirestoreManager getInstance() {
        if (instance == null)
            instance = new FirestoreManager();
        return instance;
    }

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }
    //endregion

    //region Getters
    public void getCategories(final OnCollectionChangedListener<Category> listener) {
        db.collection(CATEGORIES_COLLECTION)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                listener.onDocumentAdded(documentChange.getNewIndex(), new Category(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                listener.onDocumentChanged(documentChange.getNewIndex(), new Category(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                listener.onDocumentRemoved(documentChange.getOldIndex(), new Category(documentChange.getDocument().getId(), documentChange.getDocument()));
                            }
                        }

                        final ArrayList<Category> categories = new ArrayList<>();
                        for (DocumentSnapshot doc : value)
                            categories.add(new Category(doc.getId(), doc));
                        listener.onCollectionChange(categories);
                    }
                });
    }

    public void getDrinks(final OnCollectionChangedListener<Drink> listener) {
        db.collection(DRINK_COLLECTION)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                listener.onDocumentAdded(documentChange.getNewIndex(), new Drink(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                listener.onDocumentChanged(documentChange.getNewIndex(), new Drink(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                listener.onDocumentRemoved(documentChange.getOldIndex(), new Drink(documentChange.getDocument().getId(), documentChange.getDocument()));
                            }
                        }

                        final ArrayList<Drink> drinkArrayList = new ArrayList<>();
                        for (DocumentSnapshot doc : value)
                            drinkArrayList.add(new Drink(doc.getId(), doc));
                        listener.onCollectionChange(drinkArrayList);
                    }
                });
    }

    public void getMeals(final OnCollectionChangedListener<Meal> listener) {
        db.collection(MEAL_COLLECTION)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {

                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                listener.onDocumentAdded(documentChange.getNewIndex(), new Meal(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                listener.onDocumentChanged(documentChange.getNewIndex(), new Meal(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                listener.onDocumentRemoved(documentChange.getOldIndex(), new Meal(documentChange.getDocument().getId(), documentChange.getDocument()));
                            }
                        }

                        ArrayList<Meal> meals = new ArrayList<>();
                        for (DocumentSnapshot doc : value)
                            meals.add(new Meal(doc.getId(), doc));
                        listener.onCollectionChange(meals);
                    }
                });
    }

    public void getEvents(final OnCollectionChangedListener<Event> listener) {
        db.collection(EVENTS_COLLECTION)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {

                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                listener.onDocumentAdded(documentChange.getNewIndex(), new Event(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                listener.onDocumentChanged(documentChange.getNewIndex(), new Event(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                listener.onDocumentRemoved(documentChange.getOldIndex(), new Event(documentChange.getDocument().getId(), documentChange.getDocument()));
                            }
                        }

                        ArrayList<Event> eventCollection = new ArrayList<>();
                        for (DocumentSnapshot doc : value)
                            eventCollection.add(new Event(doc.getId(), doc));
                        listener.onCollectionChange(eventCollection);

                    }
                });
    }

    public void getOffers(final OnCollectionChangedListener<Offer> listener) {
        db.collection(OFFERS_COLLECTION)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {

                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                listener.onDocumentAdded(documentChange.getNewIndex(), new Offer(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                listener.onDocumentChanged(documentChange.getNewIndex(), new Offer(documentChange.getDocument().getId(), documentChange.getDocument()));

                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                listener.onDocumentRemoved(documentChange.getOldIndex(), new Offer(documentChange.getDocument().getId(), documentChange.getDocument()));
                            }
                        }

                        ArrayList<Offer> offersCollection = new ArrayList<>();
                        for (DocumentSnapshot doc : value)
                            offersCollection.add(new Offer(doc.getId(), doc));
                        listener.onCollectionChange(offersCollection);

                    }
                });
    }

    public void getCategory(String categoryID, final OnDocumentLoadedListener<Category> callback) {
        db.collection(CATEGORIES_COLLECTION)
                .document(categoryID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            callback.onDocumentLoaded(new Category(document.getId(), document));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }

    public void getDrink(String drinkID, final OnDocumentLoadedListener<Drink> callback) {
        db.collection(DRINK_COLLECTION)
                .document(drinkID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            callback.onDocumentLoaded(new Drink(document.getId(), document));
                        } else {
                            callback.onDocumentLoaded(null);
                        }
                    } else {
                        callback.onDocumentLoaded(null);
                    }
                });
    }

    private MutableLiveData<Drink> drinkAsked = new MutableLiveData<Drink>(){};

    public MutableLiveData<Drink> getDrinkv2(String drinkID) {
        db.collection(DRINK_COLLECTION)
                .document(drinkID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            drinkAsked.setValue(new Drink(document.getId(), document));
                        } else {
                            drinkAsked.setValue(null);
                        }
                    } else {
                        drinkAsked.setValue(null);
                    }
                });

        return drinkAsked;
    }

    public void getMeal(String mealID, final OnDocumentLoadedListener<Meal> callback) {
        db.collection(MEAL_COLLECTION)
                .document(mealID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            callback.onDocumentLoaded(new Meal(document.getId(), document));
                        } else {
                            callback.onDocumentLoaded(null);
                        }
                    } else {
                        callback.onDocumentLoaded(null);
                    }
                });
    }

    public void getEvent(String eventID, final OnDocumentLoadedListener<Event> callback) {
        db.collection(EVENTS_COLLECTION)
                .document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            callback.onDocumentLoaded(new Event(document.getId(), document));
                        } else {
                            callback.onDocumentLoaded(null);
                        }
                    } else {
                        callback.onDocumentLoaded(null);
                    }
                });
    }

    public void getOffer(String offerID, final OnDocumentLoadedListener<Offer> callback) {
        db.collection(OFFERS_COLLECTION)
                .document(offerID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            callback.onDocumentLoaded(new Offer(document.getId(), document));
                        } else {
                            callback.onDocumentLoaded(null);
                        }
                    } else {
                        callback.onDocumentLoaded(null);
                    }
                });
    }

    //endregion

    //region Save Entities

    public void saveDrink(final Drink drink, final OnDocumentSavedListener<Drink> listener) {

        Map<String, Object> drinkMap = drink.getMap();

        db.collection(DRINK_COLLECTION)
                .add(drinkMap)
                .addOnSuccessListener(documentReference -> {
                    drink.setId(documentReference.getId());
                    listener.onDocumentSaved(drink);
                })
                .addOnFailureListener(e -> {
                    listener.onDocumentSaved(null);
                    Log.w(TAG, "Error writing document", e);
                });
    }

    public void saveMeal(final Meal meal, final OnDocumentSavedListener<Meal> listener) {

        Map<String, Object> mealMap = meal.getMap();

        db.collection(MEAL_COLLECTION)
                .add(mealMap)
                .addOnSuccessListener(documentReference -> {
                    meal.setId(documentReference.getId());
                    listener.onDocumentSaved(meal);
                })
                .addOnFailureListener(e -> {
                    listener.onDocumentSaved(null);
                    Log.w(TAG, "Error writing document", e);
                });
    }

    public void saveEvent(final Event event, final OnDocumentSavedListener<Event> listener) {
        Map<String, Object> mealMap = event.getMap();

        db.collection(EVENTS_COLLECTION)
                .add(mealMap)
                .addOnSuccessListener(documentReference -> {
                    event.setId(documentReference.getId());
                    listener.onDocumentSaved(event);
                })
                .addOnFailureListener(e -> {
                    listener.onDocumentSaved(null);
                    Log.w(TAG, "Error writing document", e);
                });
    }

    public void saveOffer(final Offer offer, final OnDocumentSavedListener<Offer> listener) {
        Map<String, Object> offerMap = offer.getMap();

        db.collection(OFFERS_COLLECTION)
                .add(offerMap)
                .addOnSuccessListener(documentReference -> {
                    offer.setId(documentReference.getId());
                    listener.onDocumentSaved(offer);
                })
                .addOnFailureListener(e -> {
                    listener.onDocumentSaved(null);
                    Log.w(TAG, "Error writing document", e);
                });
    }

    //endregion

    //region Update Entities
    public void updateCategory(Category category, OnDocumentSavedListener<Category> listener) {
        Map<String, Object> categoryMap = category.getMap();

        db.collection(CATEGORIES_COLLECTION).document(category.getId())
                .set(categoryMap)
                .addOnSuccessListener(aVoid -> listener.onDocumentSaved(category))
                .addOnFailureListener(e -> listener.onDocumentSaved(null));
    }

    public void updateDrink(Drink drink, OnDocumentSavedListener<Drink> listener) {

        Map<String, Object> drinkMap = drink.getMap();

        db.collection(DRINK_COLLECTION).document(drink.getId())
                .set(drinkMap)
                .addOnSuccessListener(aVoid -> listener.onDocumentSaved(new Drink()))
                .addOnFailureListener(e -> listener.onDocumentSaved(null));
    }

    public void updateMeal(Meal meal, OnDocumentSavedListener<Meal> listener) {

        Map<String, Object> mealMap = meal.getMap();

        db.collection(MEAL_COLLECTION).document(meal.getId())
                .set(mealMap)
                .addOnSuccessListener(aVoid -> listener.onDocumentSaved(new Meal()))
                .addOnFailureListener(e -> listener.onDocumentSaved(null));
    }

    public void updateEvent(Event event, OnDocumentSavedListener<Event> listener) {

        Map<String, Object> mealMap = event.getMap();

        db.collection(EVENTS_COLLECTION).document(event.getId())
                .set(mealMap)
                .addOnSuccessListener(aVoid -> listener.onDocumentSaved(event))
                .addOnFailureListener(e -> listener.onDocumentSaved(null));
    }

    public void updateOffer(Offer offer, OnDocumentSavedListener<Offer> listener) {

        Map<String, Object> offerMap = offer.getMap();

        db.collection(OFFERS_COLLECTION).document(offer.getId())
                .set(offerMap)
                .addOnSuccessListener(aVoid -> listener.onDocumentSaved(offer))
                .addOnFailureListener(e -> listener.onDocumentSaved(null));
    }
    //endregion

    //region Delete
    public void deleteDrink(String drinkID) {
        db.collection(DRINK_COLLECTION).document(drinkID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public void deleteMeal(String mealID) {
        db.collection(MEAL_COLLECTION).document(mealID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public void deleteEvent(String eventID) {
        db.collection(EVENTS_COLLECTION).document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public void deleteOffer(String offerID) {
        db.collection(OFFERS_COLLECTION).document(offerID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }
    //endregion

    //region Interfaces
    public interface OnCollectionChangedListener<T> {
        void onCollectionChange(List<T> collection);
        void onDocumentAdded(int index, T object);
        void onDocumentChanged(int index, T object);
        void onDocumentRemoved(int index, T object);
    }

    public interface OnDocumentLoadedListener<T> {
        void onDocumentLoaded(T document);
    }

    public interface OnDocumentSavedListener<T> {
        void onDocumentSaved(T document);
    }
    //endregion
}
