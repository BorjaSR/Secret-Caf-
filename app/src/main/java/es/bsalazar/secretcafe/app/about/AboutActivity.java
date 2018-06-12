package es.bsalazar.secretcafe.app.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version_name)
    TextView version;
    @BindView(R.id.admin_indicatior)
    TextView admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        version.setText(String.format(getString(R.string.version_name), BuildConfig.VERSION_NAME));
        admin.setVisibility(BuildConfig.Admin ? View.VISIBLE : View.GONE);
    }
}
