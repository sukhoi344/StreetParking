package chau.streetparking.ui.map;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.*;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.garage.MyGarageActivity;
import chau.streetparking.ui.ProfileActivity;
import chau.streetparking.ui.SearchLocationActivity;
import chau.streetparking.ui.login.StartActivity;
import chau.streetparking.ui.payment.PaymentActivity;
import chau.streetparking.util.Logger;
import chau.streetparking.util.MapUtil;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";

    private static final int REQUEST_CODE_SEARCH = 1;
    private static final int REQUEST_CODE_PROFILE = 3;

    private static final int ID_MY_RESERVATIONS = 0;
    private static final int ID_PAYMENT = 1;
    private static final int ID_GARAGE_SETTING = 2;
    private static final int ID_PARKING_REQUESTS = 3;
    private static final int ID_BALANCE = 4;

    // Maps
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap       googleMap; // Might be null if Google Play services APK is not available.
    private Drawer          drawer;
    private MapLayout       mapLayout;
    private MyMapFragment   mapFragment;

    // Sliding up panel
    private SlidingUpPanelLayout slidingPanelUp;

    // Global variables
    private VenueMapLoader          venueMapLoader;
    private ParkingSpotMapLoader    parkingSpotMapLoader;
    private ParkingDetailDisplayer  parkingDetailDisplayer;

    private Geocoder geocoder;
    private TaskGetAddress taskGetAddress;
    private int seekBarRadiusInMeter = 0;

    // Drawer variables
    private IProfile profile;
    private AccountHeader headerResult;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWidgets();

        geocoder = new Geocoder(this);

        setUpMapIfNeeded();
        setupUser();
        setupDrawer(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_SEARCH) {
            Address address = data.getParcelableExtra(SearchLocationActivity.EXTRA_ADDRESS);
            if (address != null && address.hasLatitude() && address.hasLatitude()) {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                moveCamera(latLng, false);
                updateLocationAddress(latLng);
            }
        }

        if (requestCode == REQUEST_CODE_PROFILE && data != null) {
            int code = data.getIntExtra(ProfileActivity.EXTRA_PROFILE, ProfileActivity.PROFILE_NO_CHANGE);

            switch (code) {
                case ProfileActivity.PROFILE_LOG_OUT:
                    finish();
                    startActivity(new Intent(this, StartActivity.class));
                    break;

                case ProfileActivity.PROFILE_UPDATED:
                    profile.withName(user.getFirstName() + " " + user.getLastName());
                    profile.withEmail(user.getEmail());

                    if (user.getAvatar() != null) {
                        profile.withIcon(user.getAvatar().getUrl());
                    }

                    headerResult.updateProfileByIdentifier(profile);
                    break;

                default:
                    break;
            }
        }
    }

    public void onNavigationClicked(View v) {
        if (drawer != null && !drawer.isDrawerOpen()) {
            drawer.openDrawer();
        }
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
            mapFragment = (MyMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap = mapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        // Build the client to show current location
        buildGoogleApiClient();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        // Initial the loaders
        venueMapLoader = new VenueMapLoader(this, googleMap);
        parkingSpotMapLoader = new ParkingSpotMapLoader(this, googleMap);

        // get seek bar radius
        int radiusInMeter = (int) (MapLayout.SEEK_BAR_DEFAULT_VALUE_IN_FEET * 0.3048);
        seekBarRadiusInMeter = radiusInMeter;

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng latLng = cameraPosition.target;

                // Correct the circle radius to match the map size
                int radiusInMeters = MapUtil.convertMetersToPixels(googleMap, latLng, seekBarRadiusInMeter);
                mapFragment.setRadius(radiusInMeters);

                // Update the location address bar
//                if (mapLayout.getCurrentLayout() == MapLayout.LAYOUT_FIND_PARKING_SPOTS) {
//                    updateLocationAddress(latLng);
//                }

                updateLocationAddress(latLng);

                // Display venues and parking spots on the visible map region
                LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                venueMapLoader.load(latLngBounds);
                parkingSpotMapLoader.load(latLngBounds, mapLayout.getStartDate(), mapLayout.getEndDate());
            }
        });

        // Zoom the marker when selected
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (parkingDetailDisplayer != null) {
                    ParkingLot parkingLot = parkingSpotMapLoader.getParkingLot(marker);
                    parkingDetailDisplayer.display(parkingLot, mapLayout.getStartDate(),
                            mapLayout.getEndDate());
                }

                return false;
            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                parkingDetailDisplayer.close();
            }
        });

        setupPanelUpLayout();
        setupMapLayout();
//        enableCircle();

        parkingDetailDisplayer = new ParkingDetailDisplayer(this, googleMap, slidingPanelUp);
    }

    private void setupMapLayout() {
        mapLayout.setLocationLayoutOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SearchLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
            }
        });

        mapLayout.setMyLocationBtnMargin(getResources()
                .getDimensionPixelSize(R.dimen.google_map_top_margin));

        mapLayout.setBtnDoneOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapLayout.SearchDetail searchDetail = mapLayout.getSearchDetail();
                LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                parkingDetailDisplayer.setDates(searchDetail.startDate, searchDetail.endDate);
                parkingSpotMapLoader.setDates(latLngBounds, searchDetail.startDate, searchDetail.endDate);

            }
        });
    }

    private void setupPanelUpLayout() {
        View view = findViewById(R.id.view_transparent);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mapLayout.getMapContainer().dispatchTouchEvent(event);
                return true;
            }
        });

        slidingPanelUp.setDragView(R.id.parking_detail_header);

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

    private void updateCurrentAddress() {
        if (googleMap != null) {
            mapLayout.setMyLocationBtnMargin(getResources()
                    .getDimensionPixelSize(R.dimen.google_map_top_margin));
            LatLng latLng = googleMap.getCameraPosition().target;
            new TaskGetAddress().execute(latLng);
        }
    }

    private void setupDrawer(Toolbar toolbar) {
        String name = user.getFirstName() + " " + user.getLastName();
        String email = user.getEmail();

        // Create profile
        profile = new ProfileDrawerItem().withName(name).withEmail(email).withIdentifier(0);
        if (user.getAvatar() != null)
            profile.withIcon(user.getAvatar().getUrl());

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header)
                .addProfiles(profile)
                .withSelectionListEnabled(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_PROFILE);
                        return true;
                    }
                })
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile iProfile) {
                        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_PROFILE);
                        return true;
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
//                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)

                .addDrawerItems(
                        new PrimaryDrawerItem().withName("My Reservations")
                                .withIcon(R.drawable.ic_action_done)
                                .withIdentifier(ID_MY_RESERVATIONS)
                                .withSelectable(false),
                        new PrimaryDrawerItem().withName("Payment")
                                .withIcon(R.drawable.ic_action_credit_card)
                                .withIdentifier(ID_PAYMENT)
                                .withSelectable(false),
                        new SectionDrawerItem().withName("Your Own Garages"),
                        new PrimaryDrawerItem().withName("Garages Setting")
                                .withIcon(R.drawable.ic_action_settings)
                                .withIdentifier(ID_GARAGE_SETTING)
                                .withSelectable(false),
                        new PrimaryDrawerItem().withName("Parking Requests")
                                .withIcon(R.drawable.ic_action_info)
                                .withIdentifier(ID_PARKING_REQUESTS)
                                .withSelectable(false),
                        new PrimaryDrawerItem().withName("Income")
                                .withIcon(R.drawable.ic_action_account_balance)
                                .withIdentifier(ID_BALANCE)
                                .withSelectable(false)

                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        int id = iDrawerItem.getIdentifier();
                        switch (id) {
                            case ID_PAYMENT: {
                                Intent intent = new Intent(MapsActivity.this, PaymentActivity.class);
                                startActivity(intent);
                                return true;
                            }
                            case ID_GARAGE_SETTING: {
                                Intent intent = new Intent(MapsActivity.this, MyGarageActivity.class);
                                startActivity(intent);
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(-1)
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
                            initSearchDetail(latLng);
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

    private void initSearchDetail(final LatLng latLng) {
        if (latLng != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    try {
                        List<Address> matches = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        final Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                        if (bestMatch != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mapLayout.setSearchAddress(bestMatch);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Logger.printStackTrace(e);
                    }
                }
            }).start();
        }
    }

    private void showMyLocation(LatLng latLng) {
        moveCamera(latLng, false);
    }

//    private void findParkingSpots() {
//        Intent intent = new Intent(this, AvailableSpotsActivity.class);
//        intent.putExtra(AvailableSpotsActivity.EXTRA_RADIUS, seekBarRadiusInMeter);
//        intent.putExtra(AvailableSpotsActivity.EXTRA_LATLNG, googleMap.getCameraPosition().target);
//
//        if (mapLayout.getRequestStartDate() != null)
//            intent.putExtra(AvailableSpotsActivity.EXTRA_START_DATE, mapLayout.getRequestStartDate().getTime());
//
//        startActivityForResult(intent, REQUEST_CODE_FIND_SPOTS);
//    }

    private void moveCamera(LatLng latLng, boolean animate) {
        if (latLng != null && googleMap != null) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                 // Sets the orientation of the camera to north
                    .build();                   // Creates a CameraPosition from the builder

            if (!animate)
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            else
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void moveCamera(Marker marker) {
        if (marker != null) {
            marker.showInfoWindow();
            moveCamera(marker.getPosition(), true);
        }
    }

    private void enableCircle() {
        if (mapFragment != null) {
            mapFragment.setCircleEnable(true);
            int radiusInMeter = MapUtil.convertMetersToPixels(googleMap,
                    googleMap.getCameraPosition().target,
                    MapLayout.SEEK_BAR_DEFAULT_VALUE_IN_FEET * 0.3048);
            mapFragment.setRadius(radiusInMeter);
        }
    }

    private void disableCircle() {
        if (mapFragment != null)
            mapFragment.setCircleEnable(false);
    }

    private void setupUser() {
        user = (User) ParseUser.getCurrentUser();

    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    private void getWidgets() {
        mapLayout = (MapLayout) findViewById(R.id.map_layout);
        slidingPanelUp = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
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
                Logger.printStackTrace(e);
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
