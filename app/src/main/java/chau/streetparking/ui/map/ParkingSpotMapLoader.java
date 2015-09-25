package chau.streetparking.ui.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chau.streetparking.R;
import chau.streetparking.backend.ParkingSpotFinder;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.ui.map.MarkerOptionFactory;
import chau.streetparking.util.Logger;
import chau.streetparking.util.MapUtil;

/**
 * Created by Chau Thai on 9/19/2015.
 */
public class ParkingSpotMapLoader {
    private static final int DEFAULT_DISTANCE_TO_HIDE_MARKERS = 1500; // In meter
    private static final int DEFAULT_MAXIMUM_DISTANCE_TO_SHOW = 15000; // In meter

    final private Context context;
    final private GoogleMap map;

    private ParkingSpotFinder finder;
    private int distanceToHide = DEFAULT_DISTANCE_TO_HIDE_MARKERS;
    private float maxDisplayDistance = DEFAULT_MAXIMUM_DISTANCE_TO_SHOW;
    final private BitmapDescriptor smallDot;

    // Cache variables
    private Set<ParkingLot> cache = new HashSet<>();
    private Set<Marker> markerSet = new HashSet<>();
    private Map<Marker, BitmapDescriptor> marketToIcon = new HashMap<>();
    private float previousDistance = 0;


    public ParkingSpotMapLoader(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
        finder = new ParkingSpotFinder(context);
        smallDot = BitmapDescriptorFactory.fromResource(R.drawable.green_dot);
    }

    public void load(final LatLngBounds latLngBounds) {
        final float distance = MapUtil.getDistance(latLngBounds.northeast, latLngBounds.southwest);

        if (distance > maxDisplayDistance)
            return;

        finder.find(latLngBounds, new FindCallback<ParkingLot>() {
            @Override
            public void done(List<ParkingLot> list, ParseException e) {
                if (e != null)
                    Logger.printStackTrace(e);

                if (list != null && !list.isEmpty()) {
                    try {
                        for (ParkingLot parkingLot : list) {
                            if (!cache.contains(parkingLot)) {
                                cache.add(parkingLot);

                                MarkerOptions markerOptions = MarkerOptionFactory
                                        .create(context, parkingLot);
                                Marker marker = map.addMarker(markerOptions);

                                markerSet.add(marker);
                                marketToIcon.put(marker, markerOptions.getIcon());

                                if (distance > distanceToHide) {
                                    marker.setIcon(smallDot);
                                }
                            }
                        }

                        checkDistance(latLngBounds);
                    } catch (Exception e2) {
                        Logger.printStackTrace(e2);
                    }
                }
            }
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
