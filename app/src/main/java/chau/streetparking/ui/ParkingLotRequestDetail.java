package chau.streetparking.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/27/15.
 */
public class ParkingLotRequestDetail extends ColoredBarActivity implements OnMapReadyCallback {
    private static final String TAG = ParkingLotRequestDetail.class.getSimpleName();
    public static final String EXTRA_PARKING_LOT_ID = "extra_parking_lot_id";

    // Widgets
    private TextView    tvName;
    private ImageView   ivAvatar;
    private TextView    tvAddress;
    private TextView    tvRate;
    private MapView     mapView;
    private TextView    tvInfo;
    private RecyclerView recyclerView;

    // Variables
    private ParkingLot parkingLot;
    private GoogleMap map;
    private volatile boolean mapLoaded = false;

    @Override
    protected int getLayout() {
        return R.layout.parking_lot_request_detail_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "Parking Lot Detail";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        getExtras();
        initMap();
        setupPhotosRecycleView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();

        if (parkingLot != null)
            parkingLot.unpinInBackground();

        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);

        if (!isMapLoaded() && parkingLot != null) {
            showMapPosition();
        }
    }

    public void onBackClicked(View v) {
        finish();
    }

    public void onSelectClicked(View v) {
        if (parkingLot != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_PARKING_LOT_ID, parkingLot.getObjectId());
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    private void initMap() {
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    private void showInfo() {
        if (parkingLot != null) {
            showOwnerInfo();

            String rateType = "";
            switch (parkingLot.getPriceType()) {
                case ParkingLot.PriceType.DAILY:
                    rateType = "/day";
                    break;
                case ParkingLot.PriceType.HOURLY:
                    rateType = "/hour";
                    break;
                case ParkingLot.PriceType.MONTHLY:
                    rateType = "/month";
                    break;
            }

            tvRate.setText("$" + parkingLot.getPrice() + rateType);
            tvAddress.setText(parkingLot.getAddress());
            tvInfo.setText(parkingLot.getInfo());

            if (map != null && !isMapLoaded()) {
                showMapPosition();
            }

            showPhotos();
        }
    }

    private void showMapPosition() {
        if (parkingLot != null) {
            mapLoaded = true;

            ParseGeoPoint geoPoint = parkingLot.getLocation();
            LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
            mapLoaded = true;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            map.addMarker(new MarkerOptions().position(latLng));
        }
    }

    private synchronized boolean isMapLoaded() {
        return mapLoaded;
    }

    private void showOwnerInfo() {
        if (parkingLot != null) {
            final User owner = parkingLot.getOwner();

            final long t = System.currentTimeMillis();
            owner.fetchIfNeededInBackground(new GetCallback<User>() {
                @Override
                public void done(User user, ParseException e) {
                    Logger.d(TAG, "fetch time: " + (System.currentTimeMillis() - t) + "ms");

                    if (user != null) {
                        tvName.setText(user.getFirstName() + " " + user.getLastName());

                        if (owner.getAvatar() != null) {
                            ImageLoader.getInstance().loadImage(owner.getAvatar().getUrl(), new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {}

                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {}

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                    if (bitmap != null)
                                        ivAvatar.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {}
                            });
                        }
                    }
                }
            });
        }
    }

    private void showPhotos() {
        if (parkingLot == null || parkingLot.getPhotos() == null)
            return;

        recyclerView.setAdapter(new PhotoParseFileAdapter(this, parkingLot.getPhotos()));
    }

    private void getExtras() {
        String id = getIntent().getStringExtra(EXTRA_PARKING_LOT_ID);

        try {
            if (id != null) {
                ParseQuery<ParkingLot> query = ParseQuery.getQuery(ParkingLot.class)
                        .setLimit(1)
                        .fromLocalDatastore()
                        .whereEqualTo("objectId", id);

                query.findInBackground(new FindCallback<ParkingLot>() {
                    @Override
                    public void done(List<ParkingLot> list, ParseException e) {
                        if (e != null || list == null || list.isEmpty()) {
                            // error
                            Toast.makeText(ParkingLotRequestDetail.this, "Error getting info", Toast.LENGTH_SHORT).show();
                            if (e != null)
                                Logger.printStackTrace(e);
                        } else {
                            parkingLot = list.get(0);
                            showInfo();
                        }
                    }
                });

            }

        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
    }

    private void setupPhotosRecycleView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void getWidgets() {
        tvName = (TextView) findViewById(R.id.tv_name);
        ivAvatar = (ImageView) findViewById(R.id.avatar);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvRate = (TextView) findViewById(R.id.tv_rate);
        mapView = (MapView) findViewById(R.id.map);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvInfo = (TextView) findViewById(R.id.info);
    }
}
