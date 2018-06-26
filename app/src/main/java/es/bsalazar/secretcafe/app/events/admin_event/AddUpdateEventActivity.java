package es.bsalazar.secretcafe.app.events.admin_event;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Event;
import es.bsalazar.secretcafe.utils.Constants;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class AddUpdateEventActivity extends AppCompatActivity {

    private final int GALERY_INPUT = 1;

    @BindView(R.id.progress)
    LinearLayout progress;
    @BindView(R.id.event_image)
    ImageView event_image;
    @BindView(R.id.event_name)
    TextInputEditText event_name;
    @BindView(R.id.event_date)
    TextView event_date;
    @BindView(R.id.event_start_time)
    TextView event_start_time;
    @BindView(R.id.event_end_time)
    TextView event_end_time;
    @BindView(R.id.event_price)
    TextInputEditText event_price;
    @BindView(R.id.event_description)
    TextInputEditText event_description;

    private Uri imagePath = null;
    private Unbinder unbinder;
    private AddUpdateEventViewModel viewModel;

    private boolean InEditMode = false;
    private String eventID = "";
    private Event eventToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        unbinder = ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();
        setupViewModel();
        observeViewModel();

        if (InEditMode) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_edit_event));
            viewModel.getEvent(eventID);
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(event_name.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null)
            eventID = getIntent().getExtras().getString(Constants.EXTRA_KEY_EVENT_ID, "");

        if (!TextUtils.isEmpty(eventID)) InEditMode = true;
    }

    public void setupViewModel() {
        viewModel = ViewModelProviders.of(this,
                Injector.provideAddUpdateEventViewModelFactory())
                .get(AddUpdateEventViewModel.class);
    }

    public void observeViewModel() {

        viewModel.getEventToEdit().observe(this, this::setEditView);
        viewModel.getSaveEventResult().observe(this, this::handlerResult);
        viewModel.getEditEventResult().observe(this, this::handlerResult);
        viewModel.getLoadingProgress().observe(this, this::toogleLoadingProgress);
    }

    private void setEditView(Event event) {
        eventToEdit = event;

        event_name.setText(eventToEdit.getName());
        event_date.setText(eventToEdit.getDate());
        event_start_time.setText(eventToEdit.getStartTime());
        event_end_time.setText(eventToEdit.getEndTime());
        event_price.setText(String.valueOf(eventToEdit.getPrice()));
        event_description.setText(eventToEdit.getDescription());

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToEventImage(eventToEdit.getId()))
                .signature(new MediaStoreSignature("", eventToEdit.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(event_image);
    }

    //region EVENTS
    @OnClick(R.id.event_date_layout)
    public void showDatePicker() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(parseTime(i2)).append("/").append(parseTime(i1+1)).append("/").append(i);
            event_date.setText(builder.toString());
        }, year, month, day)
                .show();
    }


    @OnClick(R.id.event_start_time_layout)
    public void showStartTimePicker() {
        showTimePicker((timePicker, i, i1) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(parseTime(i)).append(":").append(parseTime(i1));
            event_start_time.setText(builder.toString());
        });
    }

    @OnClick(R.id.event_end_time_layout)
    public void showEndTimePicker() {
        showTimePicker((timePicker, i, i1) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(parseTime(i)).append(":").append(parseTime(i1));
            event_end_time.setText(builder.toString());
        });
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(this, listener, hour, minute, true).show();
    }

    private String parseTime(int oldTime){
        if(oldTime < 10)
            return "0" + oldTime;
        return String.valueOf(oldTime);
    }

    @OnClick(R.id.event_image)
    public void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALERY_INPUT);
    }

    @OnClick(R.id.save_button)
    public void saveEvent() {

        View aux = getCurrentFocus();
        if (aux != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(aux.getWindowToken(), 0);
        }

        if (!InEditMode) {
            Event event = new Event();
            event.setName(event_name.getText().toString());
            event.setDescription(event_description.getText().toString());
            event.setDate(event_date.getText().toString());
            event.setStartTime(event_start_time.getText().toString());
            event.setEndTime(event_end_time.getText().toString());
            event.setPrice(TextUtils.isEmpty(event_price.getText().toString()) ? 0 : Double.valueOf(event_price.getText().toString()));

            viewModel.saveEvent(event, imagePath);

        } else {
            eventToEdit.setName(event_name.getText().toString());
            eventToEdit.setDescription(event_description.getText().toString());
            eventToEdit.setDate(event_date.getText().toString());
            eventToEdit.setStartTime(event_start_time.getText().toString());
            eventToEdit.setEndTime(event_end_time.getText().toString());
            eventToEdit.setPrice(Double.valueOf(event_price.getText().toString()));

            viewModel.updateEvent(eventToEdit, imagePath);
        }
    }
    //endregino

    private void handlerResult(ResultState resultState) {
        if (resultState == ResultState.OK) {
            showSnackbar(InEditMode ? "Evento modificada!" : "Evento añadida!");
            finish();
        } else
            showSnackbar("Hubo un error inesperado...");
    }

    private void toogleLoadingProgress(ShowState showState) {
        progress.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALERY_INPUT) {
                imagePath = data.getData();
                try {
                    Bitmap comment_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    event_image.setImageBitmap(comment_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void showSnackbar(String msg) {
        Snackbar.make(event_image, msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}
