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
public class DurationValueAdapter extends RecyclerView.Adapter<DurationViewHolder> {
    private List<Integer> dataSet = new ArrayList<>();
    private Context context;
    private OnSelectedListener onSelectedListener;

    private int selectedIndex = 0;

    public DurationValueAdapter(Context context) {
        this.context = context;

        for (int i = 1; i <= 100; i++) {
            dataSet.add(i);
        }
    }

    public void setValue(int value) {
        if (value <= dataSet.size()) {
            selectedIndex = value - 1;
        }

        notifyDataSetChanged();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    @Override
    public DurationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.duration_picker_value_row, parent, false);
        return new DurationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DurationViewHolder holder, final int position) {
        if (dataSet != null && position < dataSet.size()) {
            holder.textView.setText(dataSet.get(position) + "");

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
                        onSelectedListener.onSelected(selectedIndex + 1);
                    }
                }
            });
        }
    }

    public void addData() {
        int size = dataSet.size();
        for (int i = size + 1; i < size + 100; i++) {
            dataSet.add(i);
        }
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnSelectedListener {
        void onSelected(int value);
    }
}
