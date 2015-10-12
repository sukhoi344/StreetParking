package chau.streetparking.ui.map;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 10/10/2015.
 */
public class LocationSuggestAdapter extends RecyclerView.Adapter {
    private List<Address> dataSet;
    private Context context;
    private LocationSuggestView.OnSuggestSelectedListener onSuggestSelectedListener;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title1;
        TextView title2;

        public ViewHolder(View v) {
            super(v);
            title1 = (TextView) v.findViewById(R.id.title1);
            title2 = (TextView) v.findViewById(R.id.title2);
        }
    }

    public LocationSuggestAdapter(
            Context context,
            List<Address> dataSet,
            LocationSuggestView.OnSuggestSelectedListener listener) {
        this.context = context;
        this.dataSet = dataSet;
        this.onSuggestSelectedListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_suggest_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final Address address = dataSet.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            if (address.getMaxAddressLineIndex() >= 0)
                viewHolder.title1.setText(address.getAddressLine(0));

            if (address.getMaxAddressLineIndex() >= 1) {
                viewHolder.title2.setText(address.getAddressLine(1));
                viewHolder.title2.setVisibility(View.VISIBLE);
            } else {
                viewHolder.title2.setText("");
                viewHolder.title2.setVisibility(View.GONE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSuggestSelectedListener != null) {
                        onSuggestSelectedListener.onSelected(address);
                    }
                }
            });
        }
    }

    public void setOnSuggestSelectedListener(LocationSuggestView.OnSuggestSelectedListener listener) {
        this.onSuggestSelectedListener = listener;
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }
}
