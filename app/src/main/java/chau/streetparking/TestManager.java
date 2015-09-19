package chau.streetparking;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 9/19/2015.
 */
public class TestManager {
    public static final LatLng HOME_LOCATION = new LatLng(38.958317200057046, -77.07130696624517);

    public static void showImageDialog(Context context, Bitmap bitmap) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);

        final View view = factory.inflate(R.layout.dialog_test, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

        alertadd.setView(view);
        alertadd.show();
    }

    public static void addTestMarker(final Context context, GoogleMap googleMap) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(IconGenerator.STYLE_GREEN);

        final Bitmap bitmap = iconGenerator.makeIcon("$2");

        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(TestManager.HOME_LOCATION);

        googleMap.addMarker(markerOptions);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TestManager.showImageDialog(context, bitmap);
                return false;
            }
        });
    }

    public static void addTestMarkerWithLayout(final Context context, GoogleMap googleMap) {
        IconGenerator iconGenerator = new IconGenerator(context);
        View view = View.inflate(context, R.layout.marker_layout, null);
        iconGenerator.setContentView(view);

        final Bitmap bitmap = iconGenerator.makeIcon();

        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(TestManager.HOME_LOCATION);

        googleMap.addMarker(markerOptions);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TestManager.showImageDialog(context, bitmap);
                return false;
            }
        });
    }

    public static void addTestMarkerWithUrl(final Context context, final GoogleMap googleMap) {
        final String iconUrl = "https://ss3.4sqi.net/img/categories_v2/parks_outdoors/park_bg_88.png";
        final View markerView = View.inflate(context, R.layout.marker_layout, null);
        final ImageView imageView = (ImageView) markerView.findViewById(R.id.imageView);

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();

        ImageLoader.getInstance().displayImage(iconUrl, imageView, imageOptions,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {}

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {}

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap iconBitmap) {
                        imageView.setImageBitmap(iconBitmap);

                        IconGenerator iconGenerator = new IconGenerator(context);
                        iconGenerator.setContentView(markerView);
                        final Bitmap markerBitmap = iconGenerator.makeIcon();

                        MarkerOptions markerOptions = new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                                .position(TestManager.HOME_LOCATION);

                        googleMap.addMarker(markerOptions);
                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                TestManager.showImageDialog(context, markerBitmap);
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {}
                });
    }
}
