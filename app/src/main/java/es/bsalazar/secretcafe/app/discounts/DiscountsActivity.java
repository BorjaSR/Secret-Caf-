package es.bsalazar.secretcafe.app.discounts;

import android.Manifest;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.discounts.detail.DiscountDetailActivity;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.utils.Constants;

/**
 * Created by borja.salazar on 14/08/2018.
 */

public class DiscountsActivity extends AppCompatActivity implements DiscountsAdapter.OnDiscountListener, SwipeRefreshLayout.OnRefreshListener{

    //    @BindView(R.id.qr_code)
//    ImageView qr_code;
    @BindView(R.id.discounts_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    private DiscountsViewModel viewModel;
    private DiscountsAdapter adapter;
    private TelephonyManager tm;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discounts);

        tm = ((TelephonyManager) getSystemService(this.TELEPHONY_SERVICE));
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareRecyclerView();
        setupViewModel();
        observeViewModel();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            viewModel.getMyDiscounts(tm.getImei());
        }
    }

    private void prepareRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiscountsAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnDiscountListener(this);
        swipe.setOnRefreshListener(this);
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideDiscountViewModelFactory())
                .get(DiscountsViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getDiscounts().observe(this, this::handleDiscountResult);
    }

    private void handleDiscountResult(List<Winner> winners) {
        adapter.setWinners(winners);
        swipe.setRefreshing(false);
    }


    //region OnDiscountClickListener implements
    @Override
    public void onClickDiscountListener(Winner winner, View... sharedViews) {
        animateIntent(winner, sharedViews);
    }
    //endregion

    //region OnRefresh implements
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onRefresh() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            viewModel.getMyDiscounts(tm.getImei());
        }
    }
    //endregion


    public void animateIntent(Winner winner, View... sharedViews) {

        // Ordinary Intent for launching a new activity
        Intent intent = new Intent(this, DiscountDetailActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_WINNER, winner);

        ActivityOptionsCompat multipleShared = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair.create(sharedViews[0], getString(R.string.transitionName_cardBackground))
        );

        //Start the Intent
        ActivityCompat.startActivity(this, intent, multipleShared.toBundle());
    }
}
