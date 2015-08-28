package chau.streetparking.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 8/28/15.
 */
public class DialogPhotoFragment extends DialogFragment {
    private ProgressBar progressBar;
    private ImageView imageView;
    private DisplayImageOptions displayImageOptions;

    private String url;

    public static DialogPhotoFragment newInstance(String url) {
        DialogPhotoFragment dialog = new DialogPhotoFragment();

        Bundle args = new Bundle();
        args.putString("url", url);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getArguments().getString("url");
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_dialog, container, false);

        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        imageView = (ImageView) v.findViewById(R.id.iv_photo);

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().setCancelable(true);

        return v;
    }

    @Override
    public void onDestroy() {
        ImageLoader.getInstance().cancelDisplayTask(imageView);

        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();

        if (url != null) {
            ImageLoader.getInstance().loadImage(url, displayImageOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error loading photo", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);

                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }
    }
}
