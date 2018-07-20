package es.bsalazar.secretcafe.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.about.AboutActivity;
import es.bsalazar.secretcafe.app.about.WhoWeAreActivity;
import es.bsalazar.secretcafe.app.drinks.DrinksFragment;
import es.bsalazar.secretcafe.app.events.EventsFragment;
import es.bsalazar.secretcafe.app.meals.MealsFragment;
import es.bsalazar.secretcafe.app.home.admin_home.EditCategoryFragment;
import es.bsalazar.secretcafe.app.home.HomeFragment;
import es.bsalazar.secretcafe.app.offers.OffersFragment;
import es.bsalazar.secretcafe.data.SecretRepository;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private final Location secretLocation = new Location("");

    public static final int HOME_FRAGMENT = 0;
    public static final int EDIT_CATEGORY_FRAGMENT = 1;
    public static final int DRINKS_FRAGMENT = 2;
    public static final int MEALS_FRAGMENT = 3;
    public static final int EVENTS_FRAGMENT = 4;
    public static final int OFFERS_FRAGMENT = 5;

    private NavigationView navigationView;
    private int currentFragmentID = -1;
    private Fragment currentFragment;
    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mGeofenceList;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tm = ((TelephonyManager) getSystemService(this.TELEPHONY_SERVICE));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (BuildConfig.Admin && getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.administrator));

        FirebaseApp.initializeApp(this);

        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList = new ArrayList<>();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setInitialFragment();
        createGeofences();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        } else {
            registerGeofences();
            registerImei();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            setNavigationItemChecked();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            currentFragment.setExitTransition(new Fade());
            setInitialFragment();

        } else if (id == R.id.nav_drinks && currentFragmentID != DRINKS_FRAGMENT) {
            setFragment(DRINKS_FRAGMENT, null);

        } else if (id == R.id.nav_events && currentFragmentID != EVENTS_FRAGMENT) {
            setFragment(EVENTS_FRAGMENT, null);

        } else if (id == R.id.nav_sales && currentFragmentID != OFFERS_FRAGMENT) {
            setFragment(OFFERS_FRAGMENT, null);

        } else if (id == R.id.nav_food && currentFragmentID != MEALS_FRAGMENT) {
            setFragment(MEALS_FRAGMENT, null);

        } else if (id == R.id.nav_who_we_are) {
            startActivity(new Intent(this, WhoWeAreActivity.class));

        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                boolean allGranted = true;
                for (int premissionResponse : grantResults)
                    if (premissionResponse != PackageManager.PERMISSION_GRANTED)
                        allGranted = false;

                if (allGranted) {
                    registerGeofences();
                    registerImei();
                }
            }
        }
    }

    private void setInitialFragment() {
        if (currentFragmentID != HOME_FRAGMENT) {
            Fragment initialFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_container, initialFragment, getString(R.string.TAG_HOME_FRAGMENT))
                    .commit();


            currentFragment = initialFragment;
            currentFragmentID = HOME_FRAGMENT;
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    public void setFragment(int fragmentID, Bundle args) {
        Fragment newFragment = null;
        String fragmentTag = "NOT_TAGGED";

        switch (fragmentID) {
            case HOME_FRAGMENT:
                newFragment = new HomeFragment();
                fragmentTag = getString(R.string.TAG_HOME_FRAGMENT);
                navigationView.setCheckedItem(R.id.nav_home);
                break;

            case EDIT_CATEGORY_FRAGMENT:
                newFragment = new EditCategoryFragment();
                fragmentTag = getString(R.string.TAG_EDIT_CATEGORY_FRAGMENT);
                navigationView.setCheckedItem(R.id.nav_home);
                break;

            case DRINKS_FRAGMENT:
                newFragment = new DrinksFragment();
                navigationView.setCheckedItem(R.id.nav_drinks);
                fragmentTag = getString(R.string.TAG_DRINKS_FRAGMENT);
                break;

            case MEALS_FRAGMENT:
                newFragment = new MealsFragment();
                navigationView.setCheckedItem(R.id.nav_food);
                fragmentTag = getString(R.string.TAG_MEALS_FRAGMENT);
                break;

            case EVENTS_FRAGMENT:
                newFragment = new EventsFragment();
                navigationView.setCheckedItem(R.id.nav_events);
                fragmentTag = getString(R.string.TAG_EVENTS_FRAGMENT);
                break;

            case OFFERS_FRAGMENT:
                newFragment = new OffersFragment();
                fragmentTag = getString(R.string.TAG_OFFERS_FRAGMENT);
                navigationView.setCheckedItem(R.id.nav_sales);
                break;
        }

        if (newFragment != null) {
            if (args != null) newFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_container, newFragment, fragmentTag)
                    .addToBackStack(null)
                    .commit();

            currentFragment = newFragment;
            currentFragmentID = fragmentID;
        }
    }

    private void setNavigationItemChecked() {
        String fragmentTag = getTagFromActualFragment();

        if (fragmentTag.equals(getString(R.string.TAG_HOME_FRAGMENT)) ||
                fragmentTag.equals(getString(R.string.TAG_EDIT_CATEGORY_FRAGMENT)))
            navigationView.setCheckedItem(R.id.nav_home);

        else if (fragmentTag.equals(getString(R.string.TAG_DRINKS_FRAGMENT)))
            navigationView.setCheckedItem(R.id.nav_drinks);

        else if (fragmentTag.equals(getString(R.string.TAG_MEALS_FRAGMENT)))
            navigationView.setCheckedItem(R.id.nav_food);

        else if (fragmentTag.equals(getString(R.string.TAG_EVENTS_FRAGMENT)))
            navigationView.setCheckedItem(R.id.nav_events);

        else if (fragmentTag.equals(getString(R.string.TAG_OFFERS_FRAGMENT)))
            navigationView.setCheckedItem(R.id.nav_sales);
    }

    private String getTagFromActualFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        return fragments.size() > 0 ? fragments.get(fragments.size() - 1).getTag() : "";
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    private void createGeofences() {

        //SECRET
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("2222")
                .setCircularRegion(42.687477, -2.9427574, 100)
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

//        //RENAULT
//        mGeofenceList.add(new Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId("1111")
//                .setCircularRegion(40.5205859, -3.6627022, 200)
//                .setExpirationDuration(NEVER_EXPIRE)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build());
//
//        //CASA
//        mGeofenceList.add(new Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId("0000")
//                .setCircularRegion(40.4374185, -3.6744753, 200)
//                .setExpirationDuration(NEVER_EXPIRE)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling createGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void registerGeofences() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, aVoid -> Log.d("GEOFENCE", "Geofence añadido correctamente"))
                .addOnFailureListener(e -> Log.e("GEOFENCE", "ERROR añadiendo geofence"));
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.O)
    private void registerImei() {
        SecretRepository secretRepository = Injector.provideSecretRepository(this);
        secretRepository.saveImei(tm.getImei());
    }
}
