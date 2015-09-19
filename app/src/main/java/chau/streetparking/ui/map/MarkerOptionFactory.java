package chau.streetparking.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import chau.streetparking.R;
import chau.streetparking.TestManager;
import chau.streetparking.datamodels.foursquare.Location;
import chau.streetparking.datamodels.foursquare.Venue;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 9/19/2015.
 */
public class MarkerOptionFactory {

    public static MarkerOptions createMarkerOption(Context context, Venue venue, Bitmap venueIcon) {
        try {
            if (venue != null && venueIcon != null) {
                Location location = venue.getLocation();
                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

                View view = View.inflate(context, R.layout.marker_layout, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                imageView.setImageBitmap(venueIcon);

                IconGenerator iconGenerator = new IconGenerator(context);
                iconGenerator.setContentView(view);
                Bitmap bitmap = iconGenerator.makeIcon();

                MarkerOptions options = new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(venue.getName());

                return options;
            }
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return null;
    }

}
