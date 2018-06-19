package es.bsalazar.secretcafe.app.drinks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.bsalazar.secretcafe.R;

public class DrinkDetailFragmentDialog extends DialogFragment {

    private Context mContext;

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
//        getDialog().getWindow().getAttributes().windowAnimations = R.style.CommentsDialogAnimation;

        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_drink_detail, container,
                false);

        return view;
    }
}
