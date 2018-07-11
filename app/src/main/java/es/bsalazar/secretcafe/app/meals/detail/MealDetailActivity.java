package es.bsalazar.secretcafe.app.meals.detail;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.entities.Meal;
import es.bsalazar.secretcafe.utils.Constants;

public class MealDetailActivity extends AppCompatActivity {

    @BindView(R.id.scroll)
    ScrollView scroll;
    @BindView(R.id.sticky_header_detail)
    LinearLayout sticky_header_detail;
    @BindView(R.id.sticky_name_meal_detail)
    TextView sticky_name;
    @BindView(R.id.image_meal_detail)
    ImageView image;
    @BindView(R.id.name_meal_detail)
    TextView name;
    @BindView(R.id.meal_description_detail)
    TextView description;
    @BindView(R.id.meal_price_detail)
    TextView price;

    private Unbinder unbinder;
    private Meal meal;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        unbinder = ButterKnife.bind(this);

        meal = (Meal) getIntent().getSerializableExtra(Constants.EXTRA_KEY_MEAL);
        byte[] byteArray = getIntent().getByteArrayExtra(Constants.EXTRA_KEY_BYTE_ARRAY);

        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image.setImageBitmap(bmp);
        } else
            image.setImageDrawable(getDrawable(R.drawable.default_image));

        setView();
    }

    @OnClick(R.id.container_meal_detail)
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
            if (scrollY >= image.getHeight())
                sticky_header_detail.setVisibility(View.VISIBLE);
            else
                sticky_header_detail.setVisibility(View.GONE);
        });


        if (meal != null) {
            name.setText(meal.getName());
            sticky_name.setText(meal.getName());
            description.setText(meal.getDescription());
            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
            price.setText(formatoImporte.format(meal.getPrice()));
        }
    }
}
