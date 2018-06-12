package es.bsalazar.secretcafe.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.bsalazar.secretcafe.R;


public class CategoryTitleView extends LinearLayout {

    TextView tv_title;
    LinearLayout corner_down, background_down, corner_up;

    private final String INITIAL_COLOR = "#ff409c";
    private int bannerColor;
    private String title;

    public CategoryTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null)
            inflater.inflate(R.layout.category_title, this, true);


        corner_down = findViewById(R.id.corner_down);
        background_down = findViewById(R.id.background_down);
        corner_up = findViewById(R.id.corner_up);
        tv_title = findViewById(R.id.category_title);

        bannerColor = Color.parseColor(INITIAL_COLOR);
        setBannerColor(bannerColor);
    }

    public void setTitle(String title){
        this.title = title;
        tv_title.setText(this.title);
    }

    public String getTitle() {
        return title;
    }

    public int getBannerColor() {
        return bannerColor;
    }

    public void setBannerColor(int color){
        bannerColor = color;

        String hexSolidCOlor = "#" + Integer.toHexString(color);
        String hexAlphaCOlor = "#aa" + Integer.toHexString(color).substring(2);

        int colorSolid = Color.parseColor(hexSolidCOlor);
        int colorAlpha = Color.parseColor(hexAlphaCOlor);

        tv_title.setBackgroundColor(colorAlpha);
        background_down.setBackgroundColor(colorAlpha);

        corner_down.getBackground().setColorFilter(colorSolid, PorterDuff.Mode.SRC_IN);
        corner_up.getBackground().setColorFilter(colorSolid, PorterDuff.Mode.SRC_IN);
    }
}
