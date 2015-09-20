package chau.streetparking.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chau.streetparking.R;
import chau.streetparking.backend.foursquare.VenueFinder;
import chau.streetparking.datamodels.foursquare.Venue;
import chau.streetparking.util.Logger;
import chau.streetparking.util.MapUtil;

/**
 * Load and display venues as markers on the map
 * Created by Chau Thai on 9/19/2015.
 */
public class VenueMapLoader {
    private static final int DEFAULT_DISTANCE_TO_HIDE_MARKERS = 1500; // In meter
    private static final int DEFAULT_MAXIMUM_DISTANCE_TO_SHOW = 15000; // In meter
    final private Context context;
    final private GoogleMap map;

    final private VenueFinder venueFinder;
    private int distanceToHide = DEFAULT_DISTANCE_TO_HIDE_MARKERS;
    private float maxDisplayDistance = DEFAULT_MAXIMUM_DISTANCE_TO_SHOW;
    final private BitmapDescriptor smallDot;

    // Cache variables
    private Set<Venue> cache = new HashSet<>();
    private Set<Marker> markerSet = new HashSet<>();
    private Map<Marker, BitmapDescriptor> marketToIcon = new HashMap<>();
    private float previousDistance = 0;


    public VenueMapLoader(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
        venueFinder = new VenueFinder(context);
        smallDot = BitmapDescriptorFactory.fromResource(R.drawable.gray_dot);
    }

    public void load(final LatLngBounds latLngBounds) {
        final float distance = MapUtil.getDistance(latLngBounds.northeast, latLngBounds.southwest);

        if (distance > maxDisplayDistance)
            return;

        venueFinder.find(latLngBounds, new VenueFinder.OnSearchDoneListener() {
            @Override
            public void onSearchDone(int code, String requestId, List<Venue> venues) {
                try {
                    for (Venue venue : venues) {
                        if (!cache.contains(venue)) {
                            cache.add(venue);
                            loadVenue(venue, latLngBounds);
                        }
                    }

                    checkDistance(latLngBounds);

                } catch (Exception e) {
                    Logger.printStackTrace(e);
                }
            }

            @Override
            public void onSearchError(int code, String errorType, String errorDetail) {}
        });
    }

    /**
     * Set the distance across the Google Map on the visible region to
     * hide markers on the map.
     * @param distanceToHide distance in meters.
     */
    public void setDistanceToHide(int distanceToHide) {
        this.distanceToHide = distanceToHide;
    }

    private void loadVenue(final Venue venue, final LatLngBounds latLngBounds) {
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
                            MarkerOptions markerOptions = MarkerOptionFactory.create(
                                    context, venue, bitmap
                            );

                            Marker marker = map.addMarker(markerOptions);
                            markerSet.add(marker);
                            marketToIcon.put(marker, markerOptions.getIcon());

                            if (MapUtil.getDistance(latLngBounds.northeast, latLngBounds.southwest) > distanceToHide) {
                                marker.setIcon(smallDot);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {}
                    });
        }

    }

    // TODO: implement cancel()
    public void cancel() {

    }

    private void checkDistance(LatLngBounds latLngBounds) {
        float distance = MapUtil.getDistance(latLngBounds.northeast, latLngBounds.southwest);
        if (distance > distanceToHide && previousDistance <= distanceToHide) {
            for (Marker marker : markerSet) {
                marker.setIcon(smallDot);
            }
        }

        else if (distance <= distanceToHide && previousDistance > distanceToHide) {
            for (Marker marker : markerSet) {
                marker.setIcon(marketToIcon.get(marker));
            }
        }

        previousDistance = distance;
    }
}
