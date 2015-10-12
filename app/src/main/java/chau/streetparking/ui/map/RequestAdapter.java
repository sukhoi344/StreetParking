package chau.streetparking.ui.map;

import android.app.Activity;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.Request;
import chau.streetparking.ui.map.MapLayout;

/**
 * Created by Chau Thai on 6/27/2015.
 */
public class RequestAdapter extends RecyclerView.Adapter {
    private List<Request> dataSet;
    private Activity activity;
    private MapLayout mapLayout;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    address,
                    start,
                    end;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            address = (TextView) itemView.findViewById(R.id.address);
            start = (TextView) itemView.findViewById(R.id.start);
            end = (TextView) itemView.findViewById(R.id.end);
        }
    }

    public RequestAdapter(Activity activity, MapLayout mapLayout, List<Request> dataSet) {
        this.activity = activity;
        this.mapLayout = mapLayout;
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            final Request request = dataSet.get(position);

            viewHolder.address.setText(request.getLocation().toString());
            viewHolder.start.setText(request.getStartTime().toString());
            viewHolder.end.setText(request.getEndTime().toString());
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mapLayout.showSelectedRequest(request);
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
