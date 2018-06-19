package es.bsalazar.secretcafe.app.drinks;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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
import es.bsalazar.secretcafe.app.drinks.admin_drink.AddUpdateDrinkActivity;
import es.bsalazar.secretcafe.data.FirebaseResponse;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.app.base.BaseFragment;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;

public class DrinksFragment extends BaseFragment<DrinksViewModel> implements DrinksAdapter.OnDrinkListener {

    //region Views
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.drinks_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.empty_list)
    LinearLayout empty_list;
    //endregion

    private boolean scheduleAnim = false;
    private DrinksAdapter adapter;

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

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_drinks));

        return inflater.inflate(R.layout.fragment_drinks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecycler();
        setupSwipeRefresh();

        viewModel.loadDrinks();
    }
    //endregion

    //refion Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drinks_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(getActivity(), AddUpdateDrinkActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    @Override
    public DrinksViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideDrinksViewModelFactory())
                .get(DrinksViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getDrinksList().observe(this, this::presentDrinkList);
        viewModel.getDeleteDrinkResult().observe(this, this::handlerDeleteResult);

        viewModel.getAddDrinkResponse().observe(this, this::addDrinkToList);
        viewModel.getModifyDrinkResponse().observe(this, this::modifyDrinkToList);
        viewModel.getRemoveDrinkResponse().observe(this, this::removeDrinkToList);
    }

    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new DrinksAdapter(mContext);
        recyclerView.setAdapter(adapter);

        adapter.setOnDrinkListener(this);

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

    private void setupSwipeRefresh() {
        swipe.setOnRefreshListener(() -> {
            scheduleAnim = true;
            viewModel.loadDrinks();
        });
    }

    private void presentDrinkList(List<Drink> drinkList) {
        swipe.setRefreshing(false);
        if (scheduleAnim) recyclerView.scheduleLayoutAnimation();
        scheduleAnim = false;

        adapter.setDrinks(drinkList);
    }

    private void addDrinkToList(FirebaseResponse<Drink> response){
        adapter.addDrink(response.getIndex(), response.getResponse());
    }

    private void modifyDrinkToList(FirebaseResponse<Drink> response){
        adapter.modifyDrink(response.getIndex(), response.getResponse());
    }

    private void removeDrinkToList(FirebaseResponse<Drink> response){
        adapter.removeDrink(response.getIndex(), response.getResponse());
    }

    private void handlerDeleteResult(ResultState resultState) {
        if (resultState == ResultState.OK) {
            showSnackbar("Bebida eliminada");
        } else if(resultState == ResultState.KO)
            showSnackbar("Hubo un error inesperado...");
    }

    private void showRemoveConfirmDialog(int itemPosition){
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.remove_confirm_dialog_title))
                .setMessage(getString(R.string.remove_confirm_dialog_message))
                .setPositiveButton(getString(R.string.continue_text), (dialogInterface, i) -> {
                    viewModel.deleteDrink(adapter.getItem(itemPosition).getId());
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    adapter.notifyItemChanged(itemPosition);
                }).create();

        alertDialog.show();
    }

    //region Implements DrinksListener
    @Override
    public void onClickDrinkListener(Drink drink) {
        DrinkDetailFragmentDialog drinkDetailFragmentDialog = new DrinkDetailFragmentDialog();

//        Bundle args = new Bundle();
//        args.putInt("commentID", commentID);
//        args.putBoolean("focus", needFocus);
//        args.putString("likes", new Gson().toJson(likes));

//        drinkDetailFragmentDialog.setArguments(args);

        drinkDetailFragmentDialog.show(getFragmentManager(), "COMMENTS");
    }

    @Override
    public void onLongClickDrinkListener(Drink drink) {
        Intent intent = new Intent(getActivity(), AddUpdateDrinkActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_DRINK_ID, drink.getId());
        startActivity(intent);
    }
    //endregion
}
