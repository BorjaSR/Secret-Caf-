package es.bsalazar.secretcafe.app.drinks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
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
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Drink;

public class DrinksAdapter extends RecyclerView.Adapter<DrinksAdapter.DrinkItemViewHolder> {

    public interface OnDrinkListener {
        void onClickDrinkListener(View sharedView,View sharedImage, Drink drink);

        void onLongClickDrinkListener(Drink drink);
    }

    private Context mContext;
    private List<Drink> drinks;
    private OnDrinkListener onDrinkListener;

    DrinksAdapter(Context mContext) {
        this.mContext = mContext;
        this.drinks = new ArrayList<>();
    }

    @Override
    public DrinksAdapter.DrinkItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_drink, parent, false);
        return new DrinksAdapter.DrinkItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DrinkItemViewHolder holder, int position) {
        final Drink drink = drinks.get(position);

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToDrinkImage(drink.getId()))
                .signature(new MediaStoreSignature("", drink.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(holder.drink_image);

        holder.drink_name.setText(drink.getName());
        holder.drink_description.setText(drink.getDescription());

        NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
        holder.drink_price.setText(formatoImporte.format(drink.getPrice()));

        holder.clickable_item.setOnClickListener(view -> {
            if (onDrinkListener != null)
                onDrinkListener.onClickDrinkListener(holder.cv_item, holder.drink_image, drink);
        });

        if (BuildConfig.Admin) {
            holder.clickable_item.setOnLongClickListener(view -> {
                if (onDrinkListener != null)
                    onDrinkListener.onLongClickDrinkListener(drink);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
        notifyDataSetChanged();
    }


    void addDrink(int index, Drink drink) {
        if (!containDrink(drink)) {
            drinks.add(index, drink);
            notifyItemInserted(index);
        }
    }

    void modifyDrink(int index, Drink drink) {
        if (index < drinks.size()) {
            drinks.set(index, drink);
            notifyItemChanged(index);
        }
    }

    void removeDrink(int index, Drink drink) {
        if (index < drinks.size() && containDrink(drink)) {
            drinks.remove(index);
            notifyItemRemoved(index);
        }
    }

    private boolean containDrink(Drink drink) {
        for (Drink drink1 : drinks)
            if (drink.getId().equals(drink1.getId()))
                return true;
        return false;
    }

    Drink getItem(int index){
        return drinks.get(index);
    }

    void setOnDrinkListener(OnDrinkListener onDrinkListener) {
        this.onDrinkListener = onDrinkListener;
    }

    /**
     * ViewHolder representation of a Category
     */
    class DrinkItemViewHolder extends RecyclerView.ViewHolder {

        //region Views
        @BindView(R.id.paco)
        CardView cv_item;
        @BindView(R.id.clickable_item)
        FrameLayout clickable_item;
        @BindView(R.id.drink_image)
        ImageView drink_image;
        @BindView(R.id.drink_name)
        TextView drink_name;
        @BindView(R.id.drink_description)
        TextView drink_description;
        @BindView(R.id.drink_price)
        TextView drink_price;
        //endregion

        DrinkItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
