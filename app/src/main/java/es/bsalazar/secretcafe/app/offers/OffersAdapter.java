package es.bsalazar.secretcafe.app.offers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import es.bsalazar.secretcafe.data.entities.Offer;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferItemViewHolder> {

    private OnOfferClickListener onOfferClickListener;
    private List<Offer> offers;
    private Context mContext;

    OffersAdapter(Context context) {
        this.mContext = context;
        this.offers = new ArrayList<>();
    }

    @Override
    public OfferItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_offer, parent, false);
        return new OfferItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OfferItemViewHolder holder, int position) {
        final Offer offer = offers.get(position);

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToOfferImage(offer.getId()))
                .signature(new MediaStoreSignature("", offer.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(holder.image);

        holder.title.setText(offer.getName());
        holder.description.setText(offer.getDescription());
        holder.price.setText(offer.getDescription());

        if (offer.getPrice() > 0) {
            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
            holder.price.setText(formatoImporte.format(offer.getPrice()));
        } else {
            holder.price.setText(mContext.getString(R.string.free_offer));
        }

        holder.products_container.removeAllViews();
        for (String product : offer.getOffers())
            holder.products_container.addView(getProductView(product));


        holder.click.setOnClickListener(view -> {
            if (onOfferClickListener != null)
                onOfferClickListener.onClickOfferListener(offer);
        });

        if (BuildConfig.Admin)
            holder.click.setOnLongClickListener(view -> {
                if (onOfferClickListener != null)
                    onOfferClickListener.onLongClickOfferListener(offer);
                return false;
            });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    Offer getItem(int position) {
        return offers.get(position);
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
        notifyDataSetChanged();
    }

    void addOffer(int index, Offer offer) {
        if (!containEvent(offer)) {
            offers.add(index, offer);
            notifyItemInserted(index);
        }
    }

    void modifyOffer(int index, Offer offer) {
        if (index < offers.size()) {
            offers.set(index, offer);
            notifyItemChanged(index);
        }
    }

    void removeOffer(int index, Offer offer) {
        if (index < offers.size() && containEvent(offer)) {
            offers.remove(index);
            notifyItemRemoved(index);
        }
    }

    void setOnOfferClickListener(OnOfferClickListener onOfferClickListener) {
        this.onOfferClickListener = onOfferClickListener;
    }

    private View getProductView(String productOffer) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup ingredientView = (ViewGroup) inflater.inflate(R.layout.item_product_offer, null);

        ((TextView) ingredientView.findViewById(R.id.product_offer_name)).setText(productOffer);
        ingredientView.setTag(productOffer);

        return ingredientView;
    }

    private boolean containEvent(Offer offer) {
        for (Offer offer1 : offers)
            if (offer.getId().equals(offer1.getId()))
                return true;
        return false;
    }

    /**
     * ViewHolder representation of a Meal
     */
    class OfferItemViewHolder extends RecyclerView.ViewHolder {

        //region Views
        @BindView(R.id.clickable_item)
        FrameLayout click;
        @BindView(R.id.offer_image)
        ImageView image;
        @BindView(R.id.offer_title)
        TextView title;
        @BindView(R.id.offer_description)
        TextView description;
        @BindView(R.id.offer_products_container)
        LinearLayout products_container;
        @BindView(R.id.offer_price)
        TextView price;
        //endregion

        OfferItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnOfferClickListener {
        void onClickOfferListener(Offer offer);

        void onLongClickOfferListener(Offer offer);
    }
}
