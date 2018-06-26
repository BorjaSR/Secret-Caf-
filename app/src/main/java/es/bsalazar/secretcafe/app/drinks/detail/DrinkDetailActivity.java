package es.bsalazar.secretcafe.app.drinks.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ShowState;

public class DrinkDetailActivity extends AppCompatActivity {

    @BindView(R.id.image_drink_detail)
    ImageView image;
    @BindView(R.id.name_drink_detail)
    TextView name;
    @BindView(R.id.drink_description_detail)
    TextView description;
    @BindView(R.id.drink_price_detail)
    TextView price;

    private Unbinder unbinder;
    private DrinkDetailViewModel viewModel;
    private String drinkID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);
        unbinder = ButterKnife.bind(this);

        if (getIntent().getExtras() != null)
            drinkID = getIntent().getExtras().getString(Constants.EXTRA_KEY_DRINK_ID, null);

        setupViewModel();
        observeViewModel();
    }

    @OnClick(R.id.container_drink_detail)
    public void pressBackground() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideDrinkDetailViewModelFactory(this))
                .get(DrinkDetailViewModel.class);
    }

    public void observeViewModel() {
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getDrink(drinkID).observe(this, this::setView);
    }

    private void toogleLoadingProgress(ShowState showState) {
//        if (showState == ShowState.SHOW)
//            progress.setVisibility(View.VISIBLE);
//        else
//            progress.setVisibility(View.GONE);
    }

    private void setView(Drink drink) {
        if(drink != null){
            name.setText(drink.getName());
            description.setText(drink.getDescription());
            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
            price.setText(formatoImporte.format(drink.getPrice()));
        }
    }
}
