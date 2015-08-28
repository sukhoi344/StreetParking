package chau.streetparking.ui;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 7/4/2015.
 */
public class PhotosAdapter extends RecyclerView.Adapter {
    private List<Uri> dataSet;
    private Activity activity;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button btnRemove;
        View view;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.item_photo);
            btnRemove = (Button) v.findViewById(R.id.item_remove);
            view = v;
        }
    }

    public PhotosAdapter(Activity activity, List<Uri> dataSet) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_row_deleteable, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final Uri uri = dataSet.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;

            ImageLoader.getInstance().displayImage(uri.toString(), viewHolder.imageView);

            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSet.remove(uri);
                    notifyDataSetChanged();
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
