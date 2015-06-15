package chau.streetparking.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.ui.curtain.SearchLocationActivity;

/**
 * Created by Chau Thai on 6/14/2015.
 */
public class LocationsAdapter extends RecyclerView.Adapter {
    private List<Address> dataSet;
    private Activity activity;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title1;
        TextView title2;
        View locationRow;

        public ViewHolder(View v) {
            super(v);
            title1 = (TextView) v.findViewById(R.id.location_title_1);
            title2 = (TextView) v.findViewById(R.id.location_title_2);
            locationRow = v;
        }
    }

    public LocationsAdapter(Activity activity, List<Address> dataSet) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final Address address = dataSet.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            if (address.getMaxAddressLineIndex() > 0)
                viewHolder.title1.setText(address.getAddressLine(0));

            if (address.getMaxAddressLineIndex() > 1)  {
                viewHolder.title2.setVisibility(View.VISIBLE);
                viewHolder.title2.setText(address.getAddressLine(1));
            } else {
                viewHolder.title2.setText("");
                viewHolder.title2.setVisibility(View.GONE);
            }

            viewHolder.locationRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra(SearchLocationActivity.EXTRA_ADDRESS, address);
                    activity.setResult(Activity.RESULT_OK, data);
                    activity.finish();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }
}
