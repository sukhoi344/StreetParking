package chau.streetparking.backend;

import android.content.Context;

import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.List;

import chau.streetparking.datamodels.parse.ParkingLot;

/**
 * Created by Chau Thai on 9/19/2015.
 */
public class ParkingSpotFinder {
    private Context context;

    public ParkingSpotFinder(Context context) {
        this.context = context;
    }

    public void find(final LatLngBounds latLngBounds, FindCallback<ParkingLot> findCallback) {
        if (latLngBounds == null || findCallback == null)
            return;

        if (latLngBounds.northeast == null || latLngBounds.southwest == null)
            return;

        ParseQuery<ParkingLot> query = getQuery(latLngBounds);
        query.findInBackground(findCallback);
    }

    public void cancel() {

    }

    private ParseQuery<ParkingLot> getQuery(LatLngBounds latLngBounds) {
        ParseGeoPoint swPoint = new ParseGeoPoint(
                latLngBounds.southwest.latitude,
                latLngBounds.southwest.longitude
        );

        ParseGeoPoint nePoint = new ParseGeoPoint(
                latLngBounds.northeast.latitude,
                latLngBounds.northeast.longitude
        );

        ParseQuery<ParkingLot> query = ParseQuery.getQuery(ParkingLot.class);
        query.whereWithinGeoBox(ParkingLot.KEY_LOCATION, swPoint, nePoint);

        return query;
    }
}
