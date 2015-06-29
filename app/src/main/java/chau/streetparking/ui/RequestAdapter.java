package chau.streetparking.ui;

import android.app.Activity;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.Request;

/**
 * Created by Chau Thai on 6/27/2015.
 */
public class RequestAdapter extends RecyclerView.Adapter {
    private List<Request> dataSet;
    private Activity activity;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    address,
                    radius,
                    start,
                    end;
        View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            address = (TextView) itemView.findViewById(R.id.address);
            radius = (TextView) itemView.findViewById(R.id.radius);
            start = (TextView) itemView.findViewById(R.id.start);
            end = (TextView) itemView.findViewById(R.id.end);
        }
    }

    public RequestAdapter(Activity activity, List<Request> dataSet) {
        this.activity = activity;
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
            final Address address = request.getAddress();

            if (address.getMaxAddressLineIndex() > 0) {
                viewHolder.address.setText(address.getAddressLine(0));
            }

            viewHolder.radius.setText(request.getRadius() + " ft");
            viewHolder.start.setText(request.getFrom());
            viewHolder.end.setText(request.getTo());
        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }
}
