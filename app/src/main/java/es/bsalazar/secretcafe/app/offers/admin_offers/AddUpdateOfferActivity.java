package es.bsalazar.secretcafe.app.offers.admin_offers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.StorageManager;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class AddUpdateOfferActivity extends AppCompatActivity {

    private final int GALERY_INPUT = 1;

    //region Views
    @BindView(R.id.product_offer_container)
    LinearLayout product_offer_container;
    @BindView(R.id.progress)
    LinearLayout progress;
    @BindView(R.id.offer_image)
    ImageView offer_image;
    @BindView(R.id.offer_name)
    TextInputEditText offer_name;
    @BindView(R.id.offer_description)
    TextInputEditText offer_description;
    @BindView(R.id.offer_price)
    TextInputEditText offer_price;
    @BindView(R.id.et_product_offer)
    EditText et_product_offer;
    //endregion

    private Uri imagePath = null;
    private Unbinder unbinder;
    private AddUpdateOfferViewModel viewModel;

    private boolean InEditMode = false;
    private String offerID = "";
    private Offer offerToEdit = null;
    private ArrayList<String> productsOffer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        unbinder = ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();
        setupViewModel();
        observeViewModel();

        if (InEditMode) {
            getSupportActionBar().setTitle("Editar oferta");
            viewModel.getOffer(offerID);
        }

        et_product_offer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addProductOfferView(et_product_offer.getText().toString());
                et_product_offer.setText("");
                return true;
            }
        });
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
            offerID = getIntent().getExtras().getString(Constants.EXTRA_KEY_OFFER_ID, "");
        }

        if (!TextUtils.isEmpty(offerID)) InEditMode = true;
    }

    public void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideAddUpdateOfferViewModelFactory())
                .get(AddUpdateOfferViewModel.class);
    }

    public void observeViewModel() {
        viewModel.getSaveOfferResult().observe(this, this::handlerResult);
        viewModel.getEditOfferResult().observe(this, this::handlerResult);
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
        viewModel.getOfferToEdit().observe(this, this::setView);
    }

    private void addProductOfferView(final String productOffer) {
        productsOffer.add(productOffer);

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        ViewGroup productView = (ViewGroup) inflater.inflate(R.layout.item_admin_product_offer, null);

        ((TextView) productView.findViewById(R.id.product_offer_name)).setText(productOffer);
        productView.setTag(productOffer);

        productView.findViewById(R.id.delete_product).setOnClickListener(view -> {
            removeProductOfferView(productView, productOffer);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            TransitionManager.beginDelayedTransition(product_offer_container);

        product_offer_container.addView(productView);
    }

    private void removeProductOfferView(View productView, String productOffer){
        productsOffer.remove(productOffer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            TransitionManager.beginDelayedTransition(product_offer_container);

        product_offer_container.removeView(productView);
    }

    @OnClick(R.id.offer_image)
    public void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALERY_INPUT);
    }

    @OnClick(R.id.save_button)
    public void saveOffer() {

        View aux = getCurrentFocus();
        if (aux != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(aux.getWindowToken(), 0);
        }

        if (TextUtils.isEmpty(offer_name.getText().toString())) {
            showSnackbar(getString(R.string.obligatory_name));

        } else if (!InEditMode) {
            Offer offer = new Offer();
            offer.setName(offer_name.getText().toString());
            offer.setDescription(offer_description.getText().toString());
            offer.setPrice(Double.valueOf(offer_price.getText().toString()));
            offer.setOffers(productsOffer);

            viewModel.saveOffer(offer, imagePath);

        } else {
            offerToEdit.setName(offer_name.getText().toString());
            offerToEdit.setDescription(offer_description.getText().toString());
            offerToEdit.setPrice(Double.valueOf(offer_price.getText().toString()));
            offerToEdit.setOffers(productsOffer);

            viewModel.updateOffer(offerToEdit, imagePath);
        }
    }

    private void handlerResult(ResultState resultState) {
        if (resultState == ResultState.OK) {
            showSnackbar(InEditMode ? "Oferta modificada!" : "Oferta añadida!");
            finish();
        } else
            showSnackbar("Hubo un error inesperado...");
    }

    private void toogleLoadingProgress(ShowState showState) {
        progress.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    private void setView(Offer offer) {
        offerToEdit = offer;

        offer_name.setText(offerToEdit.getName());
        offer_price.setText(String.valueOf(offerToEdit.getPrice()));
        offer_description.setText(offerToEdit.getDescription());

        productsOffer.clear();
        for (String productOffer : offerToEdit.getOffers())
            addProductOfferView(productOffer);

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToOfferImage(offerToEdit.getId()))
                .signature(new MediaStoreSignature("", offerToEdit.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(offer_image);
    }

    Bitmap comment_bitmap;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALERY_INPUT) {
                imagePath = data.getData();
                try {
                    comment_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    offer_image.setImageBitmap(comment_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void showSnackbar(String msg) {
        Snackbar.make(offer_image, msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}
