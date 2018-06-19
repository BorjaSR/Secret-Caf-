package es.bsalazar.secretcafe.app.events;

import android.app.AlertDialog;
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
import es.bsalazar.secretcafe.data.FirebaseResponse;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ShowState;

public class EventsFragment extends BaseFragment<EventsViewModel> implements EventsAdapter.OnEventClickListener{

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.empty_list)
    LinearLayout empty_list;
    @BindView(R.id.recycler_events)
    RecyclerView recyclerView;

    private boolean scheduleAnim = false;
    private EventsAdapter adapter;

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

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_events));

        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRecycler();
        setSwipeRefreshing();

        viewModel.loadEvents();
    }

    @Override
    public EventsViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideEventsViewModelFactory())
                .get(EventsViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getEmptyList().observe(this, this::toogleEmptyList);

        viewModel.getAddEventResponse().observe(this, this::addEventToList);
        viewModel.getModifyEventResponse().observe(this, this::modifyEventToList);
        viewModel.getRemoveEventResponse().observe(this, this::removeEventToList);
        viewModel.getEventList().observe(this, this::presentEventList);
    }

    //region Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meals_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(getContext(), AddUpdateEventActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    private void initializeRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new EventsAdapter(mContext);
        recyclerView.setAdapter(adapter);
        adapter.setOnEventClickListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                showRemoveConfirmDialog(viewHolder.getAdapterPosition());
            }
        });

        if (BuildConfig.Admin)
            itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void presentEventList(List<Event> meals) {
        swipe.setRefreshing(false);
        if (scheduleAnim) recyclerView.scheduleLayoutAnimation();
        scheduleAnim = false;
        adapter.setEvents(meals);
    }

    private void setSwipeRefreshing() {
        swipe.setOnRefreshListener(() -> {
            scheduleAnim = true;
            viewModel.loadEvents();
        });
    }

    private void toogleEmptyList(ShowState showState) {
        empty_list.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    private void toogleLoadingProgress(ShowState showState) {
        swipe.setRefreshing(showState == ShowState.SHOW);
    }

    private void addEventToList(FirebaseResponse<Event> response){
        adapter.addEvent(response.getIndex(), response.getResponse());
    }

    private void modifyEventToList(FirebaseResponse<Event> response){
        adapter.modifyEvent(response.getIndex(), response.getResponse());
    }

    private void removeEventToList(FirebaseResponse<Event> response){
        adapter.removeEvent(response.getIndex(), response.getResponse());
    }

    private void showRemoveConfirmDialog(int itemPosition){
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.remove_confirm_dialog_title))
                .setMessage(getString(R.string.remove_confirm_dialog_message))
                .setPositiveButton(getString(R.string.continue_text), (dialogInterface, i) -> {
                    viewModel.deleteEvent(adapter.getItem(itemPosition).getId());
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    adapter.notifyItemChanged(itemPosition);
                }).create();

        alertDialog.show();
    }

    @Override
    public void onClickEventListener(Event event) {

    }

    @Override
    public void onLongClickEventListener(Event event) {
        Intent intent = new Intent(getActivity(), AddUpdateEventActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_EVENT_ID, event.getId());
        startActivity(intent);
    }
}
