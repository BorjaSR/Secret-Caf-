package es.bsalazar.secretcafe.app.discounts.detail;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.utils.Constants;

public class DiscountDetailActivity extends AppCompatActivity {

    @BindView(R.id.discount_expired)
    TextView discount_expired;
    @BindView(R.id.qr_discount)
    ImageView qr_discount;
    @BindView(R.id.discount_code)
    TextView discount_code;
    @BindView(R.id.discount_status_image)
    ImageView discount_status_image;

    private Unbinder unbinder;
    private DiscountDetailViewModel viewModel;
    private Winner discount;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_detail);

        unbinder = ButterKnife.bind(this);

        discount = (Winner) getIntent().getSerializableExtra(Constants.EXTRA_KEY_WINNER);

        setupViewModel();
        observeViewModel();

        setView();

        viewModel.observeDiscountStatus(discount.getId());
    }

    @OnClick(R.id.container_discount_detail)
    public void pressBackground() {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideDiscountDetailViewModelFactory())
                .get(DiscountDetailViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getDiscountStatus().observe(this, this::handleDiscountStatusChange);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setView() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", new Locale("es", "ES"));
        discount_expired.setText(String.format(getString(R.string.expired_on), sdf.format(discount.getExpiredDate())));

        try {
            qr_discount.setImageBitmap(createBarcode(discount.getDiscountCode()));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        discount_code.setText(discount.getDiscountCode());

        if (discount.getStatus() == Winner.DISCOUNT_SPENT)
            discount_status_image.setVisibility(View.VISIBLE);
        else
            discount_status_image.setVisibility(View.GONE);

    }

    Bitmap createBarcode(String data) throws WriterException {
        int size = 300;
        MultiFormatWriter barcodeWriter = new MultiFormatWriter();

        BitMatrix barcodeBitMatrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);
        Bitmap barcodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                barcodeBitmap.setPixel(x, y, barcodeBitMatrix.get(x, y) ?
                        Color.BLACK : Color.WHITE);
            }
        }
        return barcodeBitmap;
    }

    private void handleDiscountStatusChange(int discountStatus) {
        if (discountStatus != this.discount.getStatus()) {
            if (discountStatus == Winner.DISCOUNT_SPENT)
                animateShowAccept();
//                discount_status_image.setVisibility(View.VISIBLE);
            else
                animateHideAccept();
//                discount_status_image.setVisibility(View.GONE);
        }

        discount.setStatus(discountStatus);
    }

    private void animateShowAccept() {
        discount_status_image.setVisibility(View.VISIBLE);
        discount_status_image.animate().alpha(1).start();
    }

    private void animateHideAccept() {
        discount_status_image.animate().alpha(0).start();
    }
}
