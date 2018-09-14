package es.bsalazar.secretcafe.app.scanner;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import butterknife.BindView;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.MainActivity;
import es.bsalazar.secretcafe.app.base.BaseFragment;
import es.bsalazar.secretcafe.data.entities.Winner;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by borja.salazar on 13/09/2018.
 */

public class ScannerFragment extends BaseFragment<ScannerViewModel> implements ZXingScannerView.ResultHandler {

    @BindView(R.id.scanner)
    ZXingScannerView scannerView;

    //region Lifecycle

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade slide = new Fade();

            setEnterTransition(slide);
            setReenterTransition(slide);
            setReturnTransition(slide);
            setExitTransition(slide);

            setAllowEnterTransitionOverlap(true);
            setAllowReturnTransitionOverlap(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (BuildConfig.Admin)
            setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }
    //endregion


    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        scannerView.setResultHandler(this);
        // Start camera on resume
        scannerView.startCamera();
    }

    @Override
    public ScannerViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideScannerViewModelFactory())
                .get(ScannerViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getDiscount().observe(this, this::handleDiscountCheckResult);
        viewModel.getResultChangeDiscount().observe(this, this::handleDiscountCheckResult);
    }

    @Override
    public void handleResult(Result result) {
        viewModel.checkDiscount(result.getText());
    }

    private void handleDiscountCheckResult(Winner discount) {
        if (discount != null) {
            if (discount.getStatus() == Winner.DISCOUNT_SPENT) {
                if (getActivity() != null)
                    ((ScannerActivity) getActivity()).changeToFragmentResult(ScannerActivity.SCANNER_RESULT_SPENT);
            } else if (discount.getExpiredDate() < System.currentTimeMillis()) {
                if (getActivity() != null)
                    ((ScannerActivity) getActivity()).changeToFragmentResult(ScannerActivity.SCANNER_RESULT_EXPIRED);
            } else {
                viewModel.changeDiscount();
            }
        } else {
            if (getActivity() != null)
                ((ScannerActivity) getActivity()).changeToFragmentResult(ScannerActivity.SCANNER_RESULT_NOT_EXIST);
        }
    }

    private void handleDiscountCheckResult(ResultState resultState) {
        if (getActivity() != null) {
            ((ScannerActivity) getActivity()).changeToFragmentResult(resultState == ResultState.OK ?
                    ScannerActivity.SCANNER_RESULT_SUCCESS :
                    ScannerActivity.SCANNER_RESULT_FAILURE);
        }
    }
}
