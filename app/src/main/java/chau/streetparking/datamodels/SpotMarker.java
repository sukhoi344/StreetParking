package chau.streetparking.datamodels;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class SpotMarker implements ClusterItem {
    private final LatLng mPosition;

    public SpotMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
