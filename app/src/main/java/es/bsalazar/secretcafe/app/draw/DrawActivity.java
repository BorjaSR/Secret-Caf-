package es.bsalazar.secretcafe.app.draw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.WriterException;

import butterknife.ButterKnife;
import es.bsalazar.secretcafe.R;

/**
 * Created by borja.salazar on 14/08/2018.
 */

public class DrawActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
