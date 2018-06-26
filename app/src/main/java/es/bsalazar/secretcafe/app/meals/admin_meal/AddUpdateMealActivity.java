package es.bsalazar.secretcafe.app.meals.admin_meal;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class AddUpdateMealActivity extends AppCompatActivity {

    private final int GALERY_INPUT = 1;

    //region Views
    @BindView(R.id.meal_image)
    ImageView meal_image;
    @BindView(R.id.meal_name)
    TextInputEditText meal_name;
    @BindView(R.id.meal_price)
    TextInputEditText meal_price;
    @BindView(R.id.meal_description)
    TextInputEditText meal_description;
    @BindView(R.id.progress)
    LinearLayout progress;
    //endregion

    private Uri imagePath = null;
    private Unbinder unbinder;
    private AddUpdateMealViewModel viewModel;

    private boolean InEditMode = false;
    private String mealID = "";
    private Meal mealToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);
        unbinder = ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();
        setupViewModel();
        observeViewModel();

        if (InEditMode) {
            getSupportActionBar().setTitle("Editar comida");
            viewModel.getMeal(mealID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mealID = getIntent().getExtras().getString(Constants.EXTRA_KEY_MEAL_ID, "");
        }

        if (!TextUtils.isEmpty(mealID)) InEditMode = true;
    }

    public void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideCreateMealViewModelFactory())
                .get(AddUpdateMealViewModel.class);
    }

    public void observeViewModel() {
        viewModel.getSaveMealResult().observe(this, this::handlerResult);
        viewModel.getEditMealResult().observe(this, this::handlerResult);
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getMealToEdit().observe(this, this::setView);
    }


    @OnClick(R.id.meal_image)
    public void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALERY_INPUT);
    }

    @OnClick(R.id.save_button)
    public void saveDrink(View view) {
        if (TextUtils.isEmpty(meal_name.getText().toString())) {
            showSnackbar("El nombre es obligatorio primo!");

        } else if (!InEditMode) {
            Meal meal = new Meal();
            meal.setName(meal_name.getText().toString());
            meal.setDescription(meal_description.getText().toString());
            meal.setPrice(Double.valueOf(meal_price.getText().toString()));
            viewModel.saveMeal(meal, imagePath);

        } else {
            mealToEdit.setName(meal_name.getText().toString());
            mealToEdit.setDescription(meal_description.getText().toString());
            mealToEdit.setPrice(Double.valueOf(meal_price.getText().toString()));
            viewModel.updateMeal(mealToEdit, imagePath);
        }
    }

    private void handlerResult(ResultState resultState) {
        if (resultState == ResultState.OK) {
            showSnackbar(InEditMode ? "Bebida modificada!" : "Bebida añadida!");
            finish();
        } else
            showSnackbar("Hubo un error inesperado...");
    }

    private void toogleLoadingProgress(ShowState showState) {
        progress.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    private void setView(Meal meal) {
        mealToEdit = meal;

        meal_name.setText(mealToEdit.getName());
        meal_price.setText(String.valueOf(mealToEdit.getPrice()));
        meal_description.setText(mealToEdit.getDescription());

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToMealImage(mealToEdit.getId()))
                .signature(new MediaStoreSignature("", mealToEdit.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(meal_image);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALERY_INPUT) {
                imagePath = data.getData();
                try {
                    Bitmap comment_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    meal_image.setImageBitmap(comment_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void showSnackbar(String msg) {
        Snackbar.make(meal_image, msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}
