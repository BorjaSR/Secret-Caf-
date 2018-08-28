package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.WriterException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;

/**
 * Created by borja.salazar on 14/08/2018.
 */

public class DrawActivity extends AppCompatActivity {

    @BindView(R.id.info_active_users)
    TextView info_users;

    private DrawViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewModel();
        observeViewModel();

//        viewModel.obtainImeis();
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideDrawViewModelFactory(this))
                .get(DrawViewModel.class);
    }

    private void observeViewModel() {
//        viewModel.getIMEIsList().observe(this, this::actualizeInfo);
    }

    private void actualizeInfo(List<String> imeis) {
        info_users.setText(String.format(getString(R.string.draw_user_quantity), String.valueOf(imeis.size())));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}
