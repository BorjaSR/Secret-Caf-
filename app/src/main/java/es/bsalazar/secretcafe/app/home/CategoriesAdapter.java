package es.bsalazar.secretcafe.app.home;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Category;
import es.bsalazar.secretcafe.utils.CategoryTitleView;

/**
 * Created by borja.salazar on 21/03/2018.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryItemViewHolder> {

    private Context mContext;
    private OnCategoryItemListener listener;
    private List<Category> categories;

    CategoriesAdapter() {
        this.categories = new ArrayList<>();
        this.listener = null;
    }

    @Override
    public CategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_category, parent, false);
        return new CategoryItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CategoryItemViewHolder holder, final int position) {
        final Category category = categories.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.cardView.setTransitionName(mContext.getResources().getString(R.string.category_item_transitionName) + position);

        holder.banner.setTitle(category.getName());
        holder.banner.setBannerColor(category.getBannerColor());

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToCategoryImage(category.getId()))
                .signature(new MediaStoreSignature("", category.getDateImageUpdate(), 0))
                .into(holder.image);

        holder.clickable_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickCategory(position, category);
            }
        });

        if (BuildConfig.Admin)
            holder.clickable_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClickCategory(position, category, holder.cardView);
                    return true;
                }
            });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mContext = recyclerView.getContext();
    }


    void modifyCategory(int index, Category category) {
        if (index < categories.size()) {
            categories.set(index, category);
            notifyItemChanged(index);
        }
    }

    void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    interface OnCategoryItemListener {

        void onClickCategory(int position, Category category);

        void onLongClickCategory(int position, Category category, View sharedView);
    }

    void setListener(OnCategoryItemListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder representation of a Category
     */
    class CategoryItemViewHolder extends RecyclerView.ViewHolder {

        //region Views
        @BindView(R.id.cv_item)
        CardView cardView;
        @BindView(R.id.category_image)
        ImageView image;
        @BindView(R.id.title_view)
        CategoryTitleView banner;
        @BindView(R.id.clickable_item)
        FrameLayout clickable_item;
        //endregion

        CategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
