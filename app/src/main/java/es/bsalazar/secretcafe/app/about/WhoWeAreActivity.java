package es.bsalazar.secretcafe.app.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import es.bsalazar.secretcafe.R;

public class WhoWeAreActivity extends AppCompatActivity {

//    @BindView(R.id.map_image)
//    ImageView map;

//    private final String MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?zoom=16&size=600x300&maptype=roadmap&markers=color:0xdc448d%7Clabel:S%7CSecret+Caf%C3%A9+Miranda+de+Ebro&key=AIzaSyA4qJrLSXJKKIvxAEwNbgQyxZhuF3-WvN8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_we_are);
//        ButterKnife.bind(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Glide.with(this)
//                .load(MAP_URL)
//                .placeholder(R.drawable.default_image)
//                .into(map);
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
}
