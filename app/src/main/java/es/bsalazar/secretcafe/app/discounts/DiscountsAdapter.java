package es.bsalazar.secretcafe.app.discounts;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.entities.Drink;
import es.bsalazar.secretcafe.data.entities.Winner;

public class DiscountsAdapter extends RecyclerView.Adapter<DiscountsAdapter.DrinkItemViewHolder> {

    public interface OnDiscountListener {
        void onClickDiscountListener(Winner winner, View... sharedViews);
    }

    private Context mContext;
    private List<Winner> winners;
    private OnDiscountListener onDiscountListener;

    DiscountsAdapter(Context mContext) {
        this.mContext = mContext;
        this.winners = new ArrayList<>();
    }

    @Override
    public DiscountsAdapter.DrinkItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_discount, parent, false);
        return new DiscountsAdapter.DrinkItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DrinkItemViewHolder holder, int position) {
        final Winner winner = winners.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", new Locale("es", "ES"));
        holder.discount_expired.setText(String.format(mContext.getString(R.string.expired_on), sdf.format(winner.getExpiredDate())));


        //STATUS VIEWS ------------------------------------
        holder.label_available.setVisibility(View.GONE);
        holder.label_expired.setVisibility(View.GONE);
        holder.label_spent.setVisibility(View.GONE);

        if(winner.getExpiredDate() < System.currentTimeMillis()){
            holder.label_expired.setVisibility(View.VISIBLE);

        }else {
            switch (winner.getStatus()) {
                case Winner.DISCOUNT_PENDING:
                    holder.label_available.setVisibility(View.VISIBLE);
                    break;
                case Winner.DISCOUNT_SPENT:
                    holder.label_spent.setVisibility(View.VISIBLE);
            }
        }
        //--------------------------------------------------

        holder.cv_item.setOnClickListener(v -> onDiscountListener.onClickDiscountListener(winner, holder.cv_item));
    }

    @Override
    public int getItemCount() {
        return winners.size();
    }

    void setWinners(List<Winner> winners) {
        this.winners = winners;
        notifyDataSetChanged();
    }

    Winner getItem(int index) {
        return winners.get(index);
    }

    void setOnDiscountListener(OnDiscountListener onDiscountListener) {
        this.onDiscountListener = onDiscountListener;
    }

    /**
     * ViewHolder representation of a Category
     */
    class DrinkItemViewHolder extends RecyclerView.ViewHolder {

        //region Views
        @BindView(R.id.cv_item)
        CardView cv_item;
        @BindView(R.id.discount_expired)
        TextView discount_expired;
        @BindView(R.id.label_available)
        LinearLayout label_available;
        @BindView(R.id.label_spent)
        LinearLayout label_spent;
        @BindView(R.id.label_expired)
        LinearLayout label_expired;
        //endregion

        DrinkItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
