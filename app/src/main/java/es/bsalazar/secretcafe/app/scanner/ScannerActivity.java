package es.bsalazar.secretcafe.app.scanner;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity {

    public static final int SCANNER_RESULT_FAILURE = -1;
    public static final int SCANNER_RESULT_SUCCESS = 0;
    public static final int SCANNER_RESULT_SPENT = 1;
    public static final int SCANNER_RESULT_NOT_EXIST = 2;
    public static final int SCANNER_RESULT_EXPIRED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.scanner_content, new ScannerFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeToFragmentResult(int result){

        Bundle args = new Bundle();
        args.putInt(Constants.EXTRA_KEY_SCANNER_RESULT, result);

        ScannerResultFragment fragment = new ScannerResultFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.scanner_content, fragment, "ScannerResultFragment")
                .commit();
    }
}
