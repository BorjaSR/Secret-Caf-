package es.bsalazar.secretcafe.app.events.detail;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.utils.Constants;

public class EventDetailActivity extends AppCompatActivity {

    private final SimpleDateFormat CURRENT_DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    private final SimpleDateFormat DISPLAY_DATE_FORMAT =
            new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());


    @BindView(R.id.scroll)
    ScrollView scroll;
    @BindView(R.id.sticky_header_detail)
    LinearLayout sticky_header_detail;
    @BindView(R.id.event_header)
    LinearLayout event_header;
    @BindView(R.id.sticky_name_event_detail)
    TextView sticky_name;
    @BindView(R.id.image_event_detail)
    ImageView image;
    @BindView(R.id.name_event_detail)
    TextView name;
    @BindView(R.id.event_description_detail)
    TextView description;
    @BindView(R.id.event_price_detail)
    TextView price;
    @BindView(R.id.event_date)
    TextView event_date;
    @BindView(R.id.event_time)
    TextView event_time;

    private Unbinder unbinder;
    private Event event;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        unbinder = ButterKnife.bind(this);

        event = (Event) getIntent().getSerializableExtra(Constants.EXTRA_KEY_EVENT);
        byte[] byteArray = getIntent().getByteArrayExtra(Constants.EXTRA_KEY_BYTE_ARRAY);

        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image.setImageBitmap(bmp);
        } else
            image.setImageDrawable(getDrawable(R.drawable.default_image));

        setView();
    }

    @OnClick(R.id.container_event_detail)
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
                setMargins(scroll, 0, name.getHeight(), 0, 0);
                sticky_header_detail.setVisibility(View.VISIBLE);
                event_header.setVisibility(View.GONE);
            }else {
                setMargins(scroll, 0, 0, 0, 0);
                sticky_header_detail.setVisibility(View.GONE);
                event_header.setVisibility(View.VISIBLE);
            }
        });


        if (event != null) {
            name.setText(event.getName());
            sticky_name.setText(event.getName());
            description.setText(event.getDescription());
            description.setText(getString(R.string.lorem_ipsum) + getString(R.string.lorem_ipsum) + getString(R.string.lorem_ipsum));

            event_date.setText(parseDate(event.getDate()));

            StringBuilder timeBuilder = new StringBuilder(event.getStartTime());
            if (event.getEndTime() != null && !TextUtils.isEmpty(event.getEndTime())) {
                timeBuilder.append(" - ");
                timeBuilder.append(event.getEndTime());
            }
            event_time.setText(timeBuilder.toString());

            if (event.getPrice() > 0) {
                NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
                price.setText(String.format(getString(R.string.ticket_event), formatoImporte.format(event.getPrice())));
            } else {
                price.setText(getString(R.string.free_event));
            }
        }

    }

    private String parseDate(String original) {
        String parseDate = "";
        try {
            parseDate = DISPLAY_DATE_FORMAT.format(CURRENT_DATE_FORMAT.parse(original));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseDate;
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
