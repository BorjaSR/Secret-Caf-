package es.bsalazar.secretcafe.app.drinks.admin_drink;

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
import android.widget.Toast;

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
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class AddUpdateDrinkActivity extends AppCompatActivity {

    private final int GALERY_INPUT = 1;

    //region Views
    @BindView(R.id.drink_image)
    ImageView drink_image;
    @BindView(R.id.drink_name)
    TextInputEditText drink_name;
    @BindView(R.id.drink_price)
    TextInputEditText drink_price;
    @BindView(R.id.drink_description)
    TextInputEditText drink_description;
    @BindView(R.id.progress)
    LinearLayout progress;
    //endregion

    private Uri imagePath = null;
    private Unbinder unbinder;
    private AddUpdateDrinkViewModel viewModel;

    private boolean InEditMode = false;
    private String drinkID = "";
    private Drink drinkToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drink);
        unbinder = ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();
        setupViewModel();
        observeViewModel();

        if (InEditMode){
            getSupportActionBar().setTitle("Editar bebida");
            viewModel.getDrink(drinkID);
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

    private void getExtras(){
        if (getIntent() != null && getIntent().getExtras() != null){
            drinkID = getIntent().getExtras().getString(Constants.EXTRA_KEY_DRINK_ID, "");
        }

        if (!TextUtils.isEmpty(drinkID)) InEditMode = true;
    }

    public void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideCreateDrinkViewModelFactory())
                .get(AddUpdateDrinkViewModel.class);
    }

    public void observeViewModel() {
        viewModel.getSaveDrinkResult().observe(this, this::handlerResult);
        viewModel.getEditDrinkResult().observe(this, this::handlerResult);
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getDrinkToEdit().observe(this, this::setView);
    }


    @OnClick(R.id.drink_image)
    public void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALERY_INPUT);
    }

    @OnClick(R.id.save_button)
    public void saveDrink(View view) {
        if(TextUtils.isEmpty(drink_name.getText().toString())){
            showSnackbar("El nombre es obligatorio primo!");

        } else if(!InEditMode) {
            Drink drink = new Drink();
            drink.setName(drink_name.getText().toString());
            drink.setDescription(drink_description.getText().toString());
            drink.setPrice(Double.valueOf(drink_price.getText().toString()));
            viewModel.saveDrink(drink, imagePath);

        } else {
            drinkToEdit.setName(drink_name.getText().toString());
            drinkToEdit.setDescription(drink_description.getText().toString());
            drinkToEdit.setPrice(Double.valueOf(drink_price.getText().toString()));
            viewModel.updateDrink(drinkToEdit, imagePath);
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
        if (showState == ShowState.SHOW)
            progress.setVisibility(View.VISIBLE);
        else
            progress.setVisibility(View.GONE);
    }

    private void setView(Drink drink){
        drinkToEdit = drink;

        drink_name.setText(drinkToEdit.getName());
        drink_price.setText(String.valueOf(drinkToEdit.getPrice()));
        drink_description.setText(drinkToEdit.getDescription());

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToDrinkImage(drinkToEdit.getId()))
                .signature(new MediaStoreSignature("", drinkToEdit.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(drink_image);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALERY_INPUT) {
                imagePath = data.getData();
                try {
                    Bitmap comment_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    drink_image.setImageBitmap(comment_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void showSnackbar(String msg){
        Snackbar.make(drink_image, msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
