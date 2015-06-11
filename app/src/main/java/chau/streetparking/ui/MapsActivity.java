package chau.streetparking.ui;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

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
import com.google.android.gms.maps.model.MarkerOptions;
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

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Drawer drawer;
    private MapLayout mapLayout;

    // For testing purpose
    private List<SpotMarker> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapLayout = (MapLayout) findViewById(R.id.map_layout);

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

    public void onRequestClicked(View v) {
        mapLayout.showNext();
    }

    public void onAddClicked(View v) {
    }

    public void onScheduleClicked(View v) {

    }

    public void onCancelClicked(View v) {
        mapLayout.cancel();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Build the client to show current location
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
        });

        setUpClusterer();
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
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l,
                                               IDrawerItem iDrawerItem) {
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
        if (latLng != null && mMap != null) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                 // Sets the orientation of the camera to north
                    .build();                   // Creates a CameraPosition from the builder

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            final Circle circle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(100)
                    .strokeColor(0xffff0000)
                    .fillColor(0x44ff0000));

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    circle.setCenter(cameraPosition.target);
                }
            });

            mapLayout.setSeekBarListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    circle.setRadius(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private void setUpClusterer() {
        // Declare a variable for the cluster manager.
        ClusterManager<SpotMarker> mClusterManager;

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
}
