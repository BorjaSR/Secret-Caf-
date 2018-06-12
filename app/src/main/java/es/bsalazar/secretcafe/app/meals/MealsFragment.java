package es.bsalazar.secretcafe.app.meals;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
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
import es.bsalazar.secretcafe.app.drinks.admin_drink.AddUpdateDrinkActivity;
import es.bsalazar.secretcafe.app.meals.admin_meal.AddUpdateMealActivity;
import es.bsalazar.secretcafe.data.FirebaseResponse;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class MealsFragment extends BaseFragment<MealsViewModel> implements MealsAdapter.OnMealClickListener {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.empty_list)
    LinearLayout empty_list;
    @BindView(R.id.recycler_meals)
    RecyclerView recyclerView;

    private boolean scheduleAnim = false;
    private MealsAdapter adapter;

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

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_meals));

        return inflater.inflate(R.layout.fragment_meals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecycler();
        setupSwipeRefresh();

        viewModel.loadMeals();
    }


    //refion Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meals_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(getContext(), AddUpdateMealActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    @Override
    public MealsViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideMealsViewModelFactory())
                .get(MealsViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getEmptyList().observe(this, this::toogleEmptyList);

        viewModel.getMealList().observe(this, this::presentMealList);
        viewModel.getAddMealResponse().observe(this, this::addMealToList);
        viewModel.getModifyMealResponse().observe(this, this::modifyMealToList);
        viewModel.getRemoveMealResponse().observe(this, this::removeMealToList);
        viewModel.getDeleteMealResult().observe(this, this::handeDeleteResult);
    }


    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new MealsAdapter(mContext);
        recyclerView.setAdapter(adapter);
        adapter.setOnMealClickListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.deleteMeal(adapter.getItem(viewHolder.getAdapterPosition()).getId());
            }
        });

        if (BuildConfig.Admin)
            itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void presentMealList(List<Meal> meals) {
        swipe.setRefreshing(false);
        if (scheduleAnim) recyclerView.scheduleLayoutAnimation();
        scheduleAnim = false;
        adapter.setMeals(meals);

    }

    private void setupSwipeRefresh() {
        swipe.setOnRefreshListener(() -> {
            scheduleAnim = true;
            viewModel.loadMeals();
        });
    }

    private void toogleEmptyList(ShowState showState) {
        empty_list.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    private void toogleLoadingProgress(ShowState showState) {
        swipe.setRefreshing(showState == ShowState.SHOW);
    }

    private void addMealToList(FirebaseResponse<Meal> response) {
        adapter.addMeal(response.getIndex(), response.getResponse());
    }

    private void modifyMealToList(FirebaseResponse<Meal> response) {
        adapter.modifyMeal(response.getIndex(), response.getResponse());
    }

    private void removeMealToList(FirebaseResponse<Meal> response) {
        adapter.removeMeal(response.getIndex(), response.getResponse());
    }

    private void handeDeleteResult(ResultState resultState) {
        if (resultState == ResultState.OK)
            Snackbar.make(recyclerView, getString(R.string.meal_removed), BaseTransientBottomBar.LENGTH_SHORT).show();
        else {
            adapter.notifyDataSetChanged();
            Snackbar.make(recyclerView, getString(R.string.default_error), BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickMealListener(Meal meal) {

    }

    @Override
    public void onLongClickMealListener(Meal meal) {
        Intent intent = new Intent(getActivity(), AddUpdateMealActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_MEAL_ID, meal.getId());
        startActivity(intent);
    }
}
