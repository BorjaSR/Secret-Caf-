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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;

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

public class DiscountsActivity extends AppCompatActivity implements DiscountsAdapter.OnDiscountListener, SwipeRefreshLayout.OnRefreshListener {

    //    @BindView(R.id.qr_code)
//    ImageView qr_code;
    @BindView(R.id.discounts_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.empty_list)
    LinearLayout empty_list;

    private DiscountsViewModel viewModel;
    private DiscountsAdapter adapter;

    private boolean scheduleAnim = false;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discounts);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareRecyclerView();
        setupViewModel();
        observeViewModel();

        viewModel.getMyDiscounts(InstanceID.getInstance(this).getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        if(winners.size() > 0){
            empty_list.setVisibility(View.GONE);
            swipe.setRefreshing(false);

            if (scheduleAnim) recyclerView.scheduleLayoutAnimation();
            scheduleAnim = false;

            adapter.setWinners(winners);

        }else
            empty_list.setVisibility(View.VISIBLE);
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
        scheduleAnim = true;
        viewModel.getMyDiscounts(InstanceID.getInstance(this).getId());
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
