package chau.streetparking.ui.picker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 9/3/15.
 */
public class DurationViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    View view;

    public DurationViewHolder(View v) {
        super(v);
        textView =  (TextView) v.findViewById(R.id.text);
        view = v;
    }

}
