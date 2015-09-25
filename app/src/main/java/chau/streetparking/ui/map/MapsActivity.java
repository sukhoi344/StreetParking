package chau.streetparking.ui.map;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
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

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.Request;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.DividerItemDecoration;
import chau.streetparking.ui.garage.MyGarageActivity;
import chau.streetparking.ui.ProfileActivity;
import chau.streetparking.ui.SearchLocationActivity;
import chau.streetparking.ui.login.StartActivity;
import chau.streetparking.ui.payment.PaymentActivity;
import chau.streetparking.util.ImageUtil;
import chau.streetparking.util.Logger;
import chau.streetparking.util.MapUtil;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";

    private static final int REQUEST_CODE_SEARCH = 1;
    private static final int REQUEST_CODE_PROFILE = 3;
    private static final int REQUEST_CODE_FIND_SPOTS = 4;

    private static final int ID_MY_RESERVATIONS = 0;
    private static final int ID_PAYMENT = 1;
    private static final int ID_GARAGE_SETTING = 2;
    private static final int ID_PARKING_REQUESTS = 3;
    private static final int ID_BALANCE = 4;

    // Toolbars
//    private Toolbar toolbar;
//    private int     actionBarHeight;

    // Notification setting
//    private View        notificationLayout;
//    private CheckBox    notificationCheckBox;

    // Maps
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap       googleMap; // Might be null if Google Play services APK is not available.
    private Drawer          drawer;
    private MapLayout       mapLayout;
    private MyMapFragment   mapFragment;

    // Global variables
    private VenueMapLoader       venueMapLoader;
    private ParkingSpotMapLoader parkingSpotMapLoader;

    private Geocoder geocoder;
    private TaskGetAddress taskGetAddress;
    private TaskGetRequestList taskGetRequestList;
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

        setupRequestList(mapLayout.getRecyclerViewRequest());
        setUpMapIfNeeded();

        // Setup toolbar
//        actionBarHeight = ImageUtil.getActionBarHeight(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getString(R.string.app_name));

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

    public void onFindClicked(View v) {
    }

    public void onCancelFindSpotsClicked(View v) {
        mapLayout.setMyLocationBtnMargin(dpToPx(10));
        mapLayout.cancelFindParkingSpot();
        disableCircle();
        showNotificationLayout();
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
                parkingSpotMapLoader.load(latLngBounds);
            }
        });

        // Zoom the marker when selected
//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                moveCamera(marker);
//                return true;
//            }
//        });

        setupMapLayout();
        enableCircle();
    }

    private void setupMapLayout() {
        mapLayout.setBtnFindParkingListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentAddress();
                enableCircle();
                hideNotificationLayout();
            }
        });

        mapLayout.setBtnMyRequestListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskGetRequestList != null && taskGetRequestList.isRunning) {
                    taskGetRequestList.cancel(true);
                }

                taskGetRequestList = new TaskGetRequestList();
                taskGetRequestList.execute();

                hideNotificationLayout();
            }
        });

        mapLayout.setSeekBarListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                double radiusInFt = Double.parseDouble(rightPinValue);

                int radiusInMeter = (int) (radiusInFt * 0.3048);
                seekBarRadiusInMeter = radiusInMeter;
                mapFragment.setRadius(MapUtil.convertMetersToPixels(googleMap,
                        googleMap.getCameraPosition().target, radiusInMeter));

                mapLayout.setTextRadius(rightPinValue + " ft");

                if (radiusInFt == rangeBar.getTickEnd()) {
                    rangeBar.setTickEnd(rangeBar.getTickEnd() * 2.0f);
                }

            }
        });

//        mapLayout.setBtnFindListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findParkingSpots();
//            }
//        });

        mapLayout.setLocationLayoutOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SearchLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
            }
        });

        mapLayout.setCancelMyRequestOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationLayout();
            }
        });

        mapLayout.setOnLayoutMoved(new MapLayout.OnLayoutMoved() {
            @Override
            public void onLayoutMoved(double ratio) {
//                int topMargin = -(int) (actionBarHeight * ratio);
//
//                LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) toolbar.getLayoutParams();
//                params.setMargins(params.leftMargin, topMargin, params.rightMargin, params.bottomMargin);
//                toolbar.setLayoutParams(params);
            }
        });

        mapLayout.setMyLocationBtnMargin(dpToPx(55));
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
            mapLayout.setMyLocationBtnMargin(dpToPx(55));
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
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mapFragment != null) {
//                    mapFragment.setCircleEnable(true);
//                    int radiusInMeter = MapUtil.convertMetersToPixels(googleMap, googleMap.getCameraPosition().target,
//                            300 * 0.3048);
//                    mapFragment.setRadius(radiusInMeter);
//                }
//            }
//        }, 1000);

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

    private void setupRequestList(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }

    private void hideNotificationLayout() {
//        notificationLayout.setVisibility(View.INVISIBLE);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);
//        notificationLayout.startAnimation(animation);
    }

    private void showNotificationLayout() {
//        notificationLayout.setVisibility(View.VISIBLE);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
//        notificationLayout.startAnimation(animation);
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
//        notificationLayout = findViewById(R.id.notification_layout);
//        notificationCheckBox = (CheckBox) findViewById(R.id.check_box_notification);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private class TaskGetRequestList extends AsyncTask<Void, Void, List<Request>> {
        private boolean isRunning = false;

        @Override
        protected void onPreExecute() {
            mapLayout.showProgressBarRequest();
        }

        @Override
        protected List<Request> doInBackground(Void... params) {
            isRunning = true;
            return null;
        }

        @Override
        protected void onPostExecute(List<Request> requests) {
            try {
                if (!isCancelled() && requests != null) {
                    mapLayout.hideProgressBarRequest();
                    mapLayout.getRecyclerViewRequest().swapAdapter(
                            new RequestAdapter(MapsActivity.this,mapLayout, requests), true);
                } else {
                    findViewById(R.id.text_view_no_result).setVisibility(View.VISIBLE);
                    mapLayout.hideProgressBarRequest();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
