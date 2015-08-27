package chau.streetparking.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseFile;

import java.util.List;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 8/27/15.
 */
public class PhotoParseFileAdapter extends RecyclerView.Adapter {
    private List<ParseFile> dataSet;
    private Activity activity;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View view;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.item_photo);
            view = v;
        }
    }

    public PhotoParseFileAdapter(Activity activity, List<ParseFile> dataSet) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final String url = dataSet.get(position).getUrl();
            final ViewHolder viewHolder = (ViewHolder) holder;

            ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }
}
