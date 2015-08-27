package chau.streetparking.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.util.MapUtil;

/**
 * Created by Chau Thai on 8/25/15.
 */
public class ParkingLotsAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<ParkingLot> dataSet;
    private LatLng location;
    private Map<String, Integer> distanceCache = new HashMap<>();

    private CheckBoxListener checkBoxListener;
    private OnItemClickedListener onItemClickedListener;
    private Set<ParkingLot> selectedSet = new HashSet<>();

    public interface CheckBoxListener {
        void onCheckBoxChanged(Set<ParkingLot> selectedSet);
    }

    public interface OnItemClickedListener {
        void onItemClicked(ParkingLot parkingLot);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        TextView distance;
        CheckBox checkBox;
        View view;

        public ViewHolder(View v) {
            super(v);
            address = (TextView)  v.findViewById(R.id.tv_address);
            distance = (TextView) v.findViewById(R.id.tv_distance);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);
            view = v;
        }
    }

    public ParkingLotsAdapter(
            Activity activity,
            List<ParkingLot> dataSet,
            LatLng location,
            CheckBoxListener checkBoxListener)
    {
        this.activity = activity;
        this.dataSet = dataSet;
        this.location = location;
        this.checkBoxListener = checkBoxListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parking_lot_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final ParkingLot parkingLot = dataSet.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.address.setText(parkingLot.getAddress());

            int distanceInFt;
            if (distanceCache.containsKey(parkingLot.getObjectId())) {
                distanceInFt = distanceCache.get(parkingLot.getObjectId());
            } else {
                LatLng latLng = new LatLng(parkingLot.getLocation().getLatitude(), parkingLot.getLocation().getLongitude());
                int distanceInMeters = (int) MapUtil.getDistance(latLng, location);
                distanceInFt = (int) (distanceInMeters * 0.3048);
                distanceCache.put(parkingLot.getObjectId(), distanceInFt);
            }

            viewHolder.distance.setText(distanceInFt + " ft");
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedSet.add(parkingLot);
                    } else {
                        selectedSet.remove(parkingLot);
                    }

                    if (checkBoxListener != null)
                        checkBoxListener.onCheckBoxChanged(selectedSet);
                }
            });

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickedListener != null) {
                        onItemClickedListener.onItemClicked(parkingLot);
                    }
                }
            });

            if (selectedSet.contains(parkingLot)) {
                viewHolder.checkBox.setChecked(true);
            }

        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }

    public Set<ParkingLot> getSelectedSet() {
        return selectedSet;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void selectParkingLot(String id) {
        Iterator<ParkingLot> iter = selectedSet.iterator();
        boolean hasId = false;


        while (iter.hasNext() && !hasId) {
            ParkingLot parkingLot = iter.next();

            if (parkingLot.getObjectId().equals(id)) {
                hasId = true;
            }
        }

        if (!hasId) {
            ParkingLot parkingLot = null;
            for (ParkingLot p : dataSet) {
                if (p.getObjectId().equals(id)) {
                    parkingLot = p;
                    break;
                }
            }

            if (parkingLot != null) {
                selectedSet.add(parkingLot);
                notifyDataSetChanged();
            }
        }
    }
}
