package es.bsalazar.secretcafe.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

/**
 * Created by asanz on 27/08/2015.
 */
public class ImpactTextView extends AppCompatTextView {

    public ImpactTextView(Context context) {
        super(context);
        font();
    }

    public ImpactTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        font();
    }

    public ImpactTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            font();
    }

    private void font() {
        Typeface myCustomFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/impact.ttf");
        this.setTypeface(myCustomFont);
    }

    @Override
    public boolean isInEditMode() {
        return false;
    }
}
