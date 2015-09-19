package chau.streetparking.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chau.streetparking.backend.foursquare.VenueFinder;
import chau.streetparking.datamodels.foursquare.Venue;
import chau.streetparking.util.Logger;

/**
 * Load and display venues as markers on the map
 * Created by Chau Thai on 9/19/2015.
 */
public class VenueMapLoader {
    final private Context context;
    final private GoogleMap map;
    final private VenueFinder venueFinder;
    final private Set<Venue> cache = new HashSet<>();

    public VenueMapLoader(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
        venueFinder = new VenueFinder(context);
    }

    public void load(LatLngBounds latLngBounds) {
        venueFinder.find(latLngBounds, new VenueFinder.OnSearchDoneListener() {
            @Override
            public void onSearchDone(int code, String requestId, List<Venue> venues) {
                try {
                    for (Venue venue : venues) {
                        if (!cache.contains(venue)) {
                            cache.add(venue);
                            loadVenue(venue);
                        }
                    }

                } catch (Exception e) {
                    Logger.printStackTrace(e);
                }
            }

            @Override
            public void onSearchError(int code, String errorType, String errorDetail) {}
        });
    }

    private void loadVenue(final Venue venue) {
        if (venue != null) {
            String url = venue.getCategories()[0].getIconUrl88(true);

            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .build();

            ImageLoader.getInstance().loadImage(url, imageOptions,
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {}

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {}

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            MarkerOptions markerOptions = MarkerOptionFactory.createMarkerOption(
                                    context, venue, bitmap
                            );

                            map.addMarker(markerOptions);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {}
                    });
        }

    }

    // TODO: implement cancel()
    public void cancel() {

    }
}
