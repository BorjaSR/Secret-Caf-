package es.bsalazar.secretcafe.app.home.admin_home;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.OnClick;
import es.bsalazar.secretcafe.Injector;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.app.base.BaseFragment;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Category;
import es.bsalazar.secretcafe.utils.CategoryTitleView;
import es.bsalazar.secretcafe.utils.ResultState;
import es.bsalazar.secretcafe.utils.ShowState;

public class EditCategoryFragment extends BaseFragment<EditCategoryViewModel> {

    private final int GALERY_INPUT = 1;

    private Uri imagePath;
    private Context mContext;
    private String categoryID;
    private Category category;
    int selectedColor;

    @BindView(R.id.clickable_item)
    FrameLayout clickable_item;
    @BindView(R.id.category_sample)
    CardView cv_item;
    @BindView(R.id.title_view)
    CategoryTitleView sample_titleView;
    @BindView(R.id.category_image)
    ImageView sample_category_image;
    @BindView(R.id.color_pick)
    LinearLayout color_pick;
    @BindView(R.id.edit_category_name)
    EditText edit_category_name;
    @BindView(R.id.progress)
    FrameLayout progress;

    //region Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);

            setEnterTransition(slide);
            setReenterTransition(slide);
            setReturnTransition(slide);
            setExitTransition(slide);

            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }

        categoryID = getArguments().getString("categoryID", "NOT_FOUND");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mContext = container.getContext();

        return inflater.inflate(R.layout.fragment_edit_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            cv_item.setTransitionName(getArguments().getString("transitionName", "NOT_FOUND"));

        viewModel.getCategory(categoryID);
    }

    @Override
    public EditCategoryViewModel setupViewModel() {
        return ViewModelProviders.of(this,
                Injector.provideEditCategoryViewModelFactory())
                .get(EditCategoryViewModel.class);
    }

    @Override
    public void observeViewModel() {
        viewModel.getCategoryToEdit().observe(this, this::setupView);
        viewModel.getEditCategoryResult().observe(this, this::handleResult);
        viewModel.getLoadingProgress().observe(this, this::toogleLoading);
    }

    //endregion

    private void setupView(final Category category) {
        this.category = category;

        sample_titleView.setTitle(category.getName());
        sample_titleView.setBannerColor(category.getBannerColor());

        color_pick.getBackground().setColorFilter(category.getBannerColor(), PorterDuff.Mode.SRC_IN);
        color_pick.setOnClickListener(view -> showColorPick(sample_titleView.getBannerColor()));

        edit_category_name.setHint(category.getName());
        edit_category_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sample_titleView.setTitle(edit_category_name.getText().toString());
            }
        });

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToCategoryImage(categoryID))
                .signature(new MediaStoreSignature("", category.getDateImageUpdate(), 0))
                .into(sample_category_image);
    }

    private void handleResult(ResultState resultState) {
        if (resultState == ResultState.OK)
            getActivity().onBackPressed();
        else
            showSnackbar(getString(R.string.default_error));
    }

    private void toogleLoading(ShowState showState) {
        progress.setVisibility(showState == ShowState.SHOW ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.clickable_item)
    public void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALERY_INPUT);
    }

    @OnClick(R.id.save_button)
    public void updateCategory() {
        category.setName(sample_titleView.getTitle());
        category.setBannerColor(sample_titleView.getBannerColor());

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        viewModel.updateCatagory(category, imagePath);
    }

    public void showColorPick(int initialColor) {

        ColorPickerDialogBuilder
                .with(mContext)
                .setTitle("Selecciona color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(13)
                .initialColor(initialColor)
                .lightnessSliderOnly()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //NOTHING TO DO
                    }
                })
                .setPositiveButton("ACEPTAR", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setColorSelected(selectedColor);
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //NOTHING TO DO
                    }
                })
                .build()
                .show();
    }

    private void setColorSelected(int selectedColor) {
        this.selectedColor = selectedColor;
        sample_titleView.setBannerColor(selectedColor);
        color_pick.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALERY_INPUT) {
                imagePath = data.getData();
                try {
                    Bitmap comment_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    sample_category_image.setImageBitmap(comment_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
