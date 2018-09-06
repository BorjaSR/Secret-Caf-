package es.bsalazar.secretcafe.app.draw;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.zxing.WriterException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;

/**
 * Created by borja.salazar on 14/08/2018.
 */

public class DrawActivity extends AppCompatActivity {

    @BindView(R.id.info_active_users)
    TextView info_users;
    @BindView(R.id.sb_quantity)
    SeekBar seekBar;
    @BindView(R.id.numberUserInfo)
    TextView numberUserInfo;

    private int numberUserToDraw;
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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numberUserInfo.setText(String.valueOf(progress));
                numberUserToDraw = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        viewModel.obtainImeis();
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideDrawViewModelFactory(this))
                .get(DrawViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getIMEIsList().observe(this, this::actualizeInfo);
    }

    private void actualizeInfo(List<String> imeis) {
        info_users.setText(String.format(getString(R.string.draw_user_quantity), String.valueOf(imeis.size())));
        seekBar.setMax(imeis.size());
        numberUserToDraw = (int)(imeis.size() * 0.2);
        seekBar.setProgress(numberUserToDraw);
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

    //region Events
    @OnClick(R.id.draw_button)
    public void drawDiscount(){
        viewModel.drawDiscounts(numberUserToDraw, new ArrayList<>());
    }
    //endregion
}
