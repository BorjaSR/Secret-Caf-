package es.bsalazar.secretcafe;

import android.content.Context;

import es.bsalazar.secretcafe.app.draw.DrawViewModelFactory;
import es.bsalazar.secretcafe.app.drinks.DrinksViewModelFactory;
import es.bsalazar.secretcafe.app.drinks.admin_drink.AddUpdateDrinkViewModelFactory;
import es.bsalazar.secretcafe.app.events.EventsViewModelFactory;
import es.bsalazar.secretcafe.app.events.admin_event.AddUpdateEventViewModelFactory;
import es.bsalazar.secretcafe.app.home.HomeViewModelFactory;
import es.bsalazar.secretcafe.app.home.admin_home.EditCategoryViewModelFactory;
import es.bsalazar.secretcafe.app.meals.MealsViewModelFactory;
import es.bsalazar.secretcafe.app.meals.admin_meal.AddUpdateMealViewModelFactory;
import es.bsalazar.secretcafe.app.offers.OffersViewModelFactory;
import es.bsalazar.secretcafe.app.offers.admin_offers.AddUpdateOfferViewModelFactory;
import es.bsalazar.secretcafe.data.SecretRepository;
import es.bsalazar.secretcafe.data.local.PreferencesManager;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.remote.StorageManager;

public class Injector {



    public static SecretRepository provideSecretRepository(Context context){
        return SecretRepository.getInstance(
                provideFirestoreManager(),
                provideStorageManager(),
                providePreferencesManager(context));
    }

    private static FirestoreManager provideFirestoreManager(){
        return FirestoreManager.getInstance();
    }

    private static StorageManager provideStorageManager(){
        return StorageManager.getInstance();
    }

    private static PreferencesManager providePreferencesManager(Context context){
        return PreferencesManager.getInstance(context);
    }

    public static EditCategoryViewModelFactory provideEditCategoryViewModelFactory(){
        return new EditCategoryViewModelFactory(provideFirestoreManager());
    }

    public static AddUpdateDrinkViewModelFactory provideCreateDrinkViewModelFactory(){
        return new AddUpdateDrinkViewModelFactory(provideFirestoreManager());
    }

    public static AddUpdateMealViewModelFactory provideCreateMealViewModelFactory(){
        return new AddUpdateMealViewModelFactory(provideFirestoreManager());
    }

    public static AddUpdateEventViewModelFactory provideAddUpdateEventViewModelFactory(){
        return new AddUpdateEventViewModelFactory(provideFirestoreManager());
    }

    public static AddUpdateOfferViewModelFactory provideAddUpdateOfferViewModelFactory(){
        return new AddUpdateOfferViewModelFactory(provideFirestoreManager());
    }

    public static DrinksViewModelFactory provideDrinksViewModelFactory(){
        return new DrinksViewModelFactory(provideFirestoreManager());
    }

    public static EventsViewModelFactory provideEventsViewModelFactory(){
        return new EventsViewModelFactory(provideFirestoreManager());
    }

    public static HomeViewModelFactory provideHomeViewModelFactory(){
        return new HomeViewModelFactory(provideFirestoreManager());
    }

    public static MealsViewModelFactory provideMealsViewModelFactory(){
        return new MealsViewModelFactory(provideFirestoreManager());
    }

    public static OffersViewModelFactory provideOffersViewModelFactory(){
        return new OffersViewModelFactory(provideFirestoreManager());
    }

    public static DrawViewModelFactory provideDrawViewModelFactory(Context context){
        return new DrawViewModelFactory(provideFirestoreManager(), provideSecretRepository(context));
    }
}
