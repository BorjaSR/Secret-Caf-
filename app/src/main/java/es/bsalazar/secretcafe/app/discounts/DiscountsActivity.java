package es.bsalazar.secretcafe.app.discounts;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.BarcodeMatrix;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.R;

/**
 * Created by borja.salazar on 14/08/2018.
 */

public class DiscountsActivity extends AppCompatActivity {

    @BindView(R.id.qr_code)
    ImageView qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discounts);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            qr_code.setImageBitmap(createBarcode("Esto no funciona ni de fly"));
        } catch (WriterException e) {
            e.printStackTrace();
        }
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
}
