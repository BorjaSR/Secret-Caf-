package es.bsalazar.secretcafe.app.offers.detail;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.utils.Constants;

public class OfferDetailActivity extends AppCompatActivity {

    private final SimpleDateFormat CURRENT_DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    private final SimpleDateFormat DISPLAY_DATE_FORMAT =
            new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

    @BindView(R.id.scroll)
    ScrollView scroll;
    @BindView(R.id.sticky_header_detail)
    RelativeLayout sticky_header_detail;
    @BindView(R.id.sticky_name_offer_detail)
    TextView sticky_name;
    @BindView(R.id.sticky_image_offer_detail)
    ImageView sticky_image;
    @BindView(R.id.image_offer_detail)
    ImageView image;
    @BindView(R.id.name_offer_detail)
    TextView name;
    @BindView(R.id.offer_description_detail)
    TextView description;
    @BindView(R.id.offer_price_detail)
    TextView price;
    @BindView(R.id.offer_products_container)
    LinearLayout products_container;

    private Unbinder unbinder;
    private Offer offer;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        unbinder = ButterKnife.bind(this);

        offer = (Offer) getIntent().getSerializableExtra(Constants.EXTRA_KEY_OFFER);
        byte[] byteArray = getIntent().getByteArrayExtra(Constants.EXTRA_KEY_BYTE_ARRAY);

        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image.setImageBitmap(bmp);
            sticky_image.setImageBitmap(bmp);
        } else {
            image.setImageDrawable(getDrawable(R.drawable.default_image));
            sticky_image.setImageDrawable(getDrawable(R.drawable.default_image));
        }

        setView();
    }

    @OnClick(R.id.container_offer_detail)
    public void pressBackground() {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setView() {

        //SCROLL BEHAVIOR
        scroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= image.getHeight() - name.getHeight()) {
                sticky_header_detail.setVisibility(View.VISIBLE);
            } else
                sticky_header_detail.setVisibility(View.GONE);
        });

        if (offer != null) {
            name.setText(offer.getName());
            sticky_name.setText(offer.getName());
            description.setText(offer.getDescription());

            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
            price.setText(String.format(getString(R.string.ticket_event), formatoImporte.format(offer.getPrice())));
        }

        //LIST OF PRODUCTS
        products_container.removeAllViews();
        for (String product : offer.getOffers())
            products_container.addView(getProductView(product));

    }

    private View getProductView(String productOffer) {
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup ingredientView = (ViewGroup) inflater.inflate(R.layout.item_product_offer, null);

        ((TextView) ingredientView.findViewById(R.id.product_offer_name)).setText(productOffer);
        ingredientView.setTag(productOffer);

        return ingredientView;
    }
}
