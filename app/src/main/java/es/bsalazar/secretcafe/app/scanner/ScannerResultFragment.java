package es.bsalazar.secretcafe.app.scanner;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.base.BaseFragment;
import es.bsalazar.secretcafe.app.base.BaseViewModel;
import es.bsalazar.secretcafe.data.entities.Offer;
import es.bsalazar.secretcafe.utils.Constants;

/**
 * Created by borja.salazar on 13/09/2018.
 */

public class ScannerResultFragment extends BaseFragment {

    @BindView(R.id.image_discount_accepted)
    ImageView image_success;
    @BindView(R.id.image_discount_rejected)
    ImageView image_rejected;
    @BindView(R.id.discount_result_title)
    TextView discount_result_title;
    @BindView(R.id.discount_result_detail)
    TextView discount_result_detail;

    private int scannerResult;

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

        if (getArguments() != null)
            scannerResult = getArguments().getInt(Constants.EXTRA_KEY_SCANNER_RESULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (BuildConfig.Admin)
            setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_scanner_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(view);

        setView();
    }
    //endregion


    private void setView() {

        //RESULT SUCCES
        if(scannerResult == ScannerActivity.SCANNER_RESULT_SUCCESS){
            image_success.setVisibility(View.VISIBLE);
            image_rejected.setVisibility(View.GONE);
            discount_result_title.setText(getString(R.string.discount_accepted));
            discount_result_detail.setText(getString(R.string.discount_accepted_detail));

        //RESULT FAILURE
        } else {
            image_success.setVisibility(View.GONE);
            image_rejected.setVisibility(View.VISIBLE);
            discount_result_title.setText(getString(R.string.discount_rejected));

            if(scannerResult == ScannerActivity.SCANNER_RESULT_EXPIRED)
                discount_result_detail.setText(getString(R.string.discount_expired_detail));
            else if(scannerResult == ScannerActivity.SCANNER_RESULT_SPENT)
                discount_result_detail.setText(getString(R.string.discount_already_used_detail));
            else if(scannerResult == ScannerActivity.SCANNER_RESULT_NOT_EXIST)
                discount_result_detail.setText(getString(R.string.discount_not_exist_detail));
            else if(scannerResult == ScannerActivity.SCANNER_RESULT_FAILURE)
                discount_result_detail.setText(getString(R.string.some_went_wrong));

        }
    }

    @Override
    public BaseViewModel setupViewModel() {
        return null;
    }

    @Override
    public void observeViewModel() {

    }
}
