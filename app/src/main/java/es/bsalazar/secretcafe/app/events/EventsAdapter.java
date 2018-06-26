package es.bsalazar.secretcafe.app.events;

import android.content.Context;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.bsalazar.secretcafe.BuildConfig;
import es.bsalazar.secretcafe.R;
import es.bsalazar.secretcafe.data.remote.StorageManager;
import es.bsalazar.secretcafe.data.entities.Event;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventItemViewHolder> {


    private final SimpleDateFormat CURRENT_DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    private final SimpleDateFormat DISPLAY_DATE_FORMAT =
            new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

    private OnEventClickListener onEventClickListener;
    private List<Event> events;
    private Context mContext;

    EventsAdapter(Context context) {
        this.mContext = context;
        this.events = new ArrayList<>();
    }

    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_event, parent, false);
        return new EventItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventItemViewHolder holder, int position) {
        final Event event = events.get(position);

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(StorageManager.getInstance().getReferenceToEventImage(event.getId()))
                .signature(new MediaStoreSignature("", event.getDateImageUpdate(), 0))
                .placeholder(R.drawable.default_image)
                .into(holder.image);

        holder.title.setText(event.getName());
        holder.date.setText(parseDate(event.getDate()));

        StringBuilder timeBuilder = new StringBuilder(event.getStartTime());
        if (event.getEndTime() != null) {
            timeBuilder.append(" - ");
            timeBuilder.append(event.getEndTime());
        }
        holder.time.setText(timeBuilder.toString());

        if (event.getPrice() > 0) {
            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
            holder.price.setText(String.format(mContext.getString(R.string.ticket_event), formatoImporte.format(event.getPrice())));
        } else {
            holder.price.setText(mContext.getString(R.string.free_event));
        }

        holder.description.setText(event.getDescription());

        holder.click.setOnClickListener(view -> {
            if (onEventClickListener != null)
                onEventClickListener.onClickEventListener(event);
        });

        if (BuildConfig.Admin)
            holder.click.setOnLongClickListener(view -> {
                if (onEventClickListener != null)
                    onEventClickListener.onLongClickEventListener(event);
                return false;
            });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    Event getItem(int position) {
        return events.get(position);
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    private String parseDate(String original) {
        String parseDate = "";
        try {
            parseDate = DISPLAY_DATE_FORMAT.format(CURRENT_DATE_FORMAT.parse(original));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseDate;
    }

    void addEvent(int index, Event event) {
        if (!containEvent(event) && index <= events.size()) {
            events.add(index, event);
            notifyItemInserted(index);
        }
    }

    void modifyEvent(int index, Event event) {
        if (index < events.size()) {
            events.set(index, event);
            notifyItemChanged(index);
        }
    }

    void removeEvent(int index, Event event) {
        if (index < events.size() && containEvent(event)) {
            events.remove(index);
            notifyItemRemoved(index);
        }
    }

    void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    private boolean containEvent(Event event) {
        for (Event event1 : events)
            if (event.getId().equals(event1.getId()))
                return true;
        return false;
    }

    /**
     * ViewHolder representation of a Meal
     */
    class EventItemViewHolder extends RecyclerView.ViewHolder {

        //region Views
        @BindView(R.id.clickable_item)
        FrameLayout click;
        @BindView(R.id.event_image)
        ImageView image;
        @BindView(R.id.event_title)
        TextView title;
        @BindView(R.id.event_date)
        TextView date;
        @BindView(R.id.event_time)
        TextView time;
        @BindView(R.id.event_price)
        TextView price;
        @BindView(R.id.event_description)
        TextView description;
        //endregion

        EventItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnEventClickListener {
        void onClickEventListener(Event event);

        void onLongClickEventListener(Event event);
    }
}
