package chau.streetparking.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.SpotMarker;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";
    private static final int REQUEST_CODE_SEARCH = 1;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private Drawer drawer;
    private MapLayout mapLayout;

    private Circle circle;  // Radius circle for parking
    private Geocoder geocoder;
    private TaskGetAddress taskGetAddress;

    // For testing purpose
    private List<SpotMarker> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapLayout = (MapLayout) findViewById(R.id.map_layout);
        geocoder = new Geocoder(this);

        setupTestList();
        setUpMapIfNeeded();

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        setupDrawer(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_SEARCH) {
            Address address = data.getParcelableExtra(SearchLocationActivity.EXTRA_ADDRESS);
            if (address != null && address.hasLatitude() && address.hasLatitude()) {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                moveCamera(latLng);
                updateLocationAddress(latLng);
            }
        }
    }

    /** Called when "REQUEST" button is clicked */
    public void onRequestClicked(View v) {
        if (googleMap != null) {
            googleMap.setPadding(0, dpToPx(48), 0, 0);

            LatLng latLng = googleMap.getCameraPosition().target;
            new TaskGetAddress().execute(latLng);
        }

        mapLayout.showNext();
        enableCircle();
    }

    public void onAddClicked(View v) {
    }

    public void onSendRequestClicked(View v) {
    }

    public void onCancelClicked(View v) {
        if (googleMap != null)
            googleMap.setPadding(0, 0, 0, 0);
        mapLayout.cancel();
        disableCircle();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #googleMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        // Build the client to show current location
        buildGoogleApiClient();
        googleMap.setMyLocationEnabled(true);

        mapLayout.setSeekBarListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                if (circle != null) {
                    circle.setRadius(Double.parseDouble(rightPinValue) * 0.3048); // ft to meter
                }
            }
        });

        mapLayout.setLocationLayoutOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SearchLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
            }
        });

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng latLng = cameraPosition.target;

                if (circle != null) {
                    circle.setCenter(latLng);
                }

                mapLayout.closeCurtain();

                if (mapLayout.getCurrentLayout() == MapLayout.LAYOUT_SEND_CANCEL) {
                    updateLocationAddress(latLng);
                }
            }
        });

        setUpClusterer();
    }

    private void updateLocationAddress(LatLng latLng) {
        try {
            if (latLng != null && taskGetAddress != null && taskGetAddress.isLoading) {
                taskGetAddress.cancel(true);
            }

            taskGetAddress = new TaskGetAddress();
            taskGetAddress.execute(latLng);
        } catch (Exception e) {}
    }

    private void setupDrawer(Toolbar toolbar) {
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Chau Thai").withEmail("chthai64@gmail.com")
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
//                .withSelectionListEnabled(false)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Profile")
                                .withIcon(R.drawable.ic_account_circle_black_24dp),
                        new PrimaryDrawerItem().withName("Payment")
                                .withIcon(R.drawable.ic_credit_card_black_24dp),
                        new PrimaryDrawerItem().withName("Setting")
                                .withIcon(R.drawable.ic_settings_black_24dp),
                        new PrimaryDrawerItem().withName("Help")
                                .withIcon(R.drawable.ic_help_outline_black_24dp),
                        new PrimaryDrawerItem().withName("About")
                                .withIcon(R.drawable.ic_info_outline_black_24dp)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long id,
                                               IDrawerItem iDrawerItem) {
                        switch (position) {
                            case 0: {
                                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                return true;
                            }
                            case 1: {
                                Intent intent = new Intent(MapsActivity.this, PaymentActivity.class);
                                startActivity(intent);
                                return true;
                            }

                        }
                        return false;
                    }
                })
                .withSelectedItem(-1)
                .build();
    }

    private synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient()");

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);

                        if (lastLocation != null) {
                            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            showMyLocation(latLng);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                    }
                })
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void showMyLocation(LatLng latLng) {
        moveCamera(latLng);
    }

    private void moveCamera(LatLng latLng) {
        if (latLng != null && googleMap != null) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                 // Sets the orientation of the camera to north
                    .build();                   // Creates a CameraPosition from the builder

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void enableCircle() {
        if (googleMap != null) {
            LatLng currentCamPosition = googleMap.getCameraPosition().target;
                circle = googleMap.addCircle(new CircleOptions()
                        .center(currentCamPosition)
                        .radius(100)
                        .strokeColor(0xffff0000)
                        .fillColor(0x44ff0000));

                circle.setStrokeWidth(3.0f);
        }
    }

    private void disableCircle() {
        if (googleMap != null && circle != null) {
            circle.remove();
            circle = null;
        }
    }

    private void setUpClusterer() {
        // Declare a variable for the cluster manager.
        ClusterManager<SpotMarker> mClusterManager;

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, googleMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
//        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setTitle("15 mins ago");
                marker.showInfoWindow();
                return true;
            }
        });

        // Add cluster items (markers) to the cluster manager.
        for (SpotMarker marker : testList) {
            mClusterManager.addItem(marker);
        }
    }

    private void setupTestList() {
        testList.add(new SpotMarker(38.95853541559733, -77.07178372889757));
        testList.add(new SpotMarker(38.95899035503473, -77.07150846719742));
        testList.add(new SpotMarker(38.957454499065214, -77.07124292850494));
        testList.add(new SpotMarker(38.9609141038949, -77.06989780068398));
        testList.add(new SpotMarker(38.96098162577503, -77.06947736442089));
        testList.add(new SpotMarker(38.960973544008965, -77.06974390894175));
        testList.add(new SpotMarker(38.96109242408757, -77.0695873349905));
        testList.add(new SpotMarker(38.96222516395402, -77.07205329090357));
        testList.add(new SpotMarker(38.96110180934844, -77.0732294395566));
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    private class TaskGetAddress extends AsyncTask<LatLng, Void, String> {
        private boolean isLoading = false;

        @Override
        protected void onPreExecute() {
            isLoading = true;
        }

        @Override
        protected String doInBackground(LatLng... params) {
            try {
                List<Address> matches = geocoder.getFromLocation(params[0].latitude, params[0].longitude, 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));

                if (bestMatch != null) {
                    return bestMatch.getAddressLine(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                isLoading = false;

                if (!isCancelled() && s != null && !s.isEmpty()) {
                    mapLayout.setLocationText(s);
                }
            } catch (Exception e) {}
            finally {
            }
        }
    }
}
