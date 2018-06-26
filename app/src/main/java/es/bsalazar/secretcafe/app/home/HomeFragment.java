package es.bsalazar.secretcafe.app.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.MainActivity;
import es.bsalazar.secretcafe.app.base.BaseFragment;
import es.bsalazar.secretcafe.data.remote.FirebaseResponse;
import es.bsalazar.secretcafe.data.remote.FirestoreManager;
import es.bsalazar.secretcafe.data.entities.Category;
import es.bsalazar.secretcafe.utils.ShowState;

public class HomeFragment extends BaseFragment<HomeViewModel> implements CategoriesAdapter.OnCategoryItemListener {

    @BindView(R.id.categories_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    CategoriesAdapter adapter;
    boolean performAnim;

    //region Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            setReturnTransition(new Fade());
            setExitTransition(new Fade());

            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }

        performAnim = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.secret_cafe_name));

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycler();
        setupSwipeRefresh();

        viewModel.loadCategories();
    }
    //endregion

    //region implements BaseFragment
    @Override
    public HomeViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideHomeViewModelFactory())
                .get(HomeViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getLoadingProgress().observe(this, this::toogleLoading);
        viewModel.getCategories().observe(this, this::presentData);
        viewModel.getModifyCategoryResponse().observe(this, this::modifyCategoryToList);
    }

    //endregion

    //region implements CategoryListener
    @Override
    public void onClickCategory(int position, Category category) {
        int fragmentID = -1;
        if (category.getToCategory() != null) {
            if (category.getToCategory().equals(FirestoreManager.DRINK_COLLECTION))
                fragmentID = MainActivity.DRINKS_FRAGMENT;
            if (category.getToCategory().equals(FirestoreManager.MEAL_COLLECTION))
                fragmentID = MainActivity.MEALS_FRAGMENT;
            if (category.getToCategory().equals(FirestoreManager.EVENTS_COLLECTION))
                fragmentID = MainActivity.EVENTS_FRAGMENT;
            if (category.getToCategory().equals(FirestoreManager.OFFERS_COLLECTION))
                fragmentID = MainActivity.OFFERS_FRAGMENT;
        }

        ((MainActivity) mContext).setFragment(fragmentID, null);
    }

    @Override
    public void onLongClickCategory(int position, Category category, View sharedView) {
        Bundle args = new Bundle();
        args.putString("categoryID", category.getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            args.putString("transitionName", sharedView.getTransitionName());

        ((MainActivity) mContext).setFragment(MainActivity.EDIT_CATEGORY_FRAGMENT, args);
    }
    //endregion

    //region private methods
    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new CategoriesAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setListener(this);
    }

    private void setupSwipeRefresh() {
        swipe.setOnRefreshListener(() -> {
            recyclerView.scheduleLayoutAnimation();
            viewModel.loadCategories();
        });
    }

    private void presentData(List<Category> categories) {
        if (performAnim)
            recyclerView.scheduleLayoutAnimation();

        adapter.setCategories(categories);
        performAnim = false;
    }

    private void toogleLoading(ShowState showState) {
        swipe.setRefreshing(showState == ShowState.SHOW);
    }

    private void modifyCategoryToList(FirebaseResponse<Category> response){
        adapter.modifyCategory(response.getIndex(), response.getResponse());
    }
    //endregion
}
