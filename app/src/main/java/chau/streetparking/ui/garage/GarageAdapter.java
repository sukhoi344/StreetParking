package chau.streetparking.ui.garage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.Garage;
import chau.streetparking.datamodels.parse.ParkingLot;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class GarageAdapter extends RecyclerView.Adapter {
    private List<ParkingLot> dataSet;
    private Context context;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        View view;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            view = v;
        }
    }

    public GarageAdapter(Context context, List<ParkingLot> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.garage_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final ParkingLot parkingLot = dataSet.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.title.setText(parkingLot.getName());

            // TODO: set onclick for garage list item
        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }
}
