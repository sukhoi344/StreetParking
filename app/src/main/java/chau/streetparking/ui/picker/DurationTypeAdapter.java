package chau.streetparking.ui.picker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 9/3/15.
 */
public class DurationTypeAdapter extends RecyclerView.Adapter<DurationViewHolder> {
    private List<String> dataSet = new ArrayList<>();
    private Context context;
    private OnSelectedListener onSelectedListener;

    private int selectedIndex = 0;

    public DurationTypeAdapter(Context context) {
        this.context = context;

        dataSet.add("MINUTE");
        dataSet.add("HOUR");
        dataSet.add("DAY");
        dataSet.add("MONTH");
    }

    public void setDurationType(int type) {
        switch (type) {
            case DurationPickerDialog.DurationType.MINUTE:
                selectedIndex = 0;
                break;
            case DurationPickerDialog.DurationType.HOUR:
                selectedIndex = 1;
                break;
            case DurationPickerDialog.DurationType.DAY:
                selectedIndex = 2;
                break;
            case DurationPickerDialog.DurationType.MONTH:
                selectedIndex = 3;
                break;
        }

        notifyDataSetChanged();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    @Override
    public DurationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.duration_picker_type_row, parent, false);
        return new DurationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DurationViewHolder holder, final int position) {
        if (dataSet != null && position < dataSet.size()) {
            holder.textView.setText(dataSet.get(position));

            if (position != selectedIndex) {
                holder.textView.setBackground(null);
                holder.textView.setTextColor(0xff000000);
            } else {
                holder.textView.setBackgroundResource(R.drawable.duration_selected_circle);
                holder.textView.setTextColor(0xffffffff);
            }

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = position;
                    notifyDataSetChanged();

                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(positionToType(position));
                    }
                }
            });
        }
    }

    private int positionToType(int position) {
        switch (position) {
            case 0:
                return DurationPickerDialog.DurationType.MINUTE;
            case 1:
                return DurationPickerDialog.DurationType.HOUR;
            case 2:
                return DurationPickerDialog.DurationType.DAY;
            case 3:
                return DurationPickerDialog.DurationType.MONTH;
        }

        return DurationPickerDialog.DurationType.HOUR;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnSelectedListener {
        void onSelected(int type);
    }
}
