package es.bsalazar.secretcafe.app.offers;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindView;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.MainActivity;
import es.bsalazar.secretcafe.app.base.BaseFragment;
import es.bsalazar.secretcafe.app.events.admin_event.AddUpdateEventActivity;
import es.bsalazar.secretcafe.app.offers.admin_offers.AddUpdateOfferActivity;
import es.bsalazar.secretcafe.data.FirebaseResponse;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ShowState;

public class OffersFragment extends BaseFragment<OffersViewModel> implements OffersAdapter.OnOfferClickListener {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.empty_list)
    LinearLayout empty_list;
    @BindView(R.id.recycler_offers)
    RecyclerView recyclerView;

    private OffersAdapter adapter;
    private boolean scheduleAnim;

    //region Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.RIGHT);

            setEnterTransition(slide);
            setReenterTransition(slide);
            setReturnTransition(slide);
            setExitTransition(slide);

            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (BuildConfig.Admin)
            setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_offers));

        return inflater.inflate(R.layout.fragment_offers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRecycler();
        setSwipeRefreshing();

        viewModel.loadOffers();
    }
    //endregion

    private void initializeRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new OffersAdapter(mContext);
        recyclerView.setAdapter(adapter);
        adapter.setOnOfferClickListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.deleteOffer(adapter.getItem(viewHolder.getAdapterPosition()).getId());
            }
        });

        if (BuildConfig.Admin)
            itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setSwipeRefreshing() {
        swipe.setOnRefreshListener(() -> {
            scheduleAnim = true;
            viewModel.loadOffers();
        });
    }
    //region Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.offers_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(getContext(), AddUpdateOfferActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    //region BaseFragment implement
    @Override
    public OffersViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideOffersViewModelFactory())
                .get(OffersViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getEmptyList().observe(this, this::toogleEmptyList);

        viewModel.getAddOfferResponse().observe(this, this::addOfferToList);
        viewModel.getModifyOfferResponse().observe(this, this::modifyOfferToList);
        viewModel.getRemoveOfferResponse().observe(this, this::removeOfferToList);
        viewModel.getOffersList().observe(this, this::presentEventList);
    }
    //endregion

    //region OnOfferClickListener implement
    @Override
    public void onClickOfferListener(Offer offer) {

    }

    @Override
    public void onLongClickOfferListener(Offer offer) {
        Intent intent = new Intent(getActivity(), AddUpdateOfferActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_OFFER_ID, offer.getId());
        startActivity(intent);
    }
    //endregion

    //region Observer methods
    private void presentEventList(List<Offer> offers) {
        swipe.setRefreshing(false);
        if (scheduleAnim) recyclerView.scheduleLayoutAnimation();
        scheduleAnim = false;
        adapter.setOffers(offers);
    }

    private void toogleEmptyList(ShowState showState) {
        empty_list.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    private void toogleLoadingProgress(ShowState showState) {
        swipe.setRefreshing(showState == ShowState.SHOW);
    }

    private void addOfferToList(FirebaseResponse<Offer> response){
        adapter.addOffer(response.getIndex(), response.getResponse());
    }

    private void modifyOfferToList(FirebaseResponse<Offer> response){
        adapter.modifyOffer(response.getIndex(), response.getResponse());
    }

    private void removeOfferToList(FirebaseResponse<Offer> response){
        adapter.removeOffer(response.getIndex(), response.getResponse());
    }
    //endregion
}
