package es.bsalazar.secretcafe.app.meals;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.StorageManager;
import es.bsalazar.secretcafe.data.entities.Meal;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealItemViewHolder> {

    private OnMealClickListener onMealClickListener;
    private List<Meal> meals;
    private Context mContext;

    MealsAdapter(Context context) {
        this.mContext = context;
        this.meals = new ArrayList<>();
    }

    @Override
    public MealItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_meal, parent, false);
        return new MealItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MealItemViewHolder holder, int position) {
        final Meal meal = meals.get(position);

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToMealImage(meal.getId()))
                .signature(new MediaStoreSignature("", meal.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(holder.meal_image);

        holder.meal_name.setText(meal.getName());
        NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
        holder.meal_price.setText(formatoImporte.format(meal.getPrice()));
        holder.meal_description.setText(meal.getDescription());

        holder.clickable_item.setOnClickListener(view -> onMealClickListener.onClickMealListener(meal));

        if (BuildConfig.Admin)
            holder.clickable_item.setOnLongClickListener(view -> {
                onMealClickListener.onLongClickMealListener(meal);
                return false;
            });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    Meal getItem(int position){
        return this.meals.get(position);
    }

    void setOnMealClickListener(OnMealClickListener onMealClickListener) {
        this.onMealClickListener = onMealClickListener;
    }


    void addMeal(int index, Meal meal) {
        if (!containMeal(meal)) {
            meals.add(index, meal);
            notifyItemInserted(index);
        }
    }

    void modifyMeal(int index, Meal meal) {
        if (index < meals.size()) {
            meals.set(index, meal);
            notifyItemChanged(index);
        }
    }

    void removeMeal(int index, Meal meal) {
        if (index < meals.size() && containMeal(meal)) {
            meals.remove(index);
            notifyItemRemoved(index);
        }
    }

    private boolean containMeal(Meal meal) {
        for (Meal meal1 : meals)
            if (meal.getId().equals(meal1.getId()))
                return true;
        return false;
    }

    void updateList(List<Meal> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(this.meals, newList));

        this.meals = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * ViewHolder representation of a Meal
     */
    class MealItemViewHolder extends RecyclerView.ViewHolder {

        //region Views
        @BindView(R.id.clickable_item)
        FrameLayout clickable_item;
        @BindView(R.id.meal_image)
        ImageView meal_image;
        @BindView(R.id.meal_name)
        TextView meal_name;
        @BindView(R.id.meal_description)
        TextView meal_description;
        @BindView(R.id.meal_price)
        TextView meal_price;
        //endregion

        MealItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnMealClickListener {
        void onClickMealListener(Meal meal);

        void onLongClickMealListener(Meal meal);
    }

    public class MyDiffCallback extends DiffUtil.Callback{

        List<Meal> oldMeals;
        List<Meal> newMeals;

        MyDiffCallback(List<Meal> newMeals, List<Meal> oldMeals) {
            this.newMeals = newMeals;
            this.oldMeals = oldMeals;
        }

        @Override
        public int getOldListSize() {
            return oldMeals.size();
        }

        @Override
        public int getNewListSize() {
            return newMeals.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMeals.get(oldItemPosition).getId().equals(newMeals.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Meal oldMeal = oldMeals.get(oldItemPosition);
            Meal newMeal = newMeals.get(newItemPosition);

            return !(oldMeal.getDateImageUpdate() != newMeal.getDateImageUpdate() ||
                    !oldMeal.getDescription().equals(newMeal.getDescription()) ||
                    !oldMeal.getName().equals(newMeal.getName()) ||
                    oldMeal.getPrice() != newMeal.getPrice());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            //you can return particular field for changed item.
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}
