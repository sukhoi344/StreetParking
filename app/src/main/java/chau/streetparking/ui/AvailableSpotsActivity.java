package chau.streetparking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/25/15.
 */
public class AvailableSpotsActivity extends ColoredBarActivity {
    private static final String TAG = AvailableSpotsActivity.class.getSimpleName();

    public static final String EXTRA_RADIUS = "extra_radius";
    public static final String EXTRA_LATLNG = "extra_location";
    public static final String EXTRA_START_DATE = "extra_start_date";
    public static final String EXTRA_END_DATE = "extra_end_date";

    private ProgressBar     progressBar;
    private RecyclerView    recyclerView;
    private TextView        textViewNoResult;
    private RecyclerView.LayoutManager layoutManager;

    private int radius;
    private LatLng latLng;
    private Date startDate;
    private Date endDate;

    @Override
    protected int getLayout() {
        return R.layout.available_spot_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "Available Parking Lots";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        getExtras();
        getParkingLots();
        setupList();
    }

    public void onBackClicked(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onSendRequestClicked(View v) {

    }

    private void getParkingLots() {
        ParseQuery<ParkingLot> query = getQuery();
        query.findInBackground(new FindCallback<ParkingLot>() {
            @Override
            public void done(List<ParkingLot> list, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);

                if (e != null) {
                    textViewNoResult.setVisibility(View.VISIBLE);
                } else {
                    if (list == null || list.isEmpty())
                        textViewNoResult.setVisibility(View.VISIBLE);
                    else {
                        recyclerView.setAdapter(new ParkingLotsAdapter(AvailableSpotsActivity.this, list, latLng));
                    }
                }
            }
        });
    }

    private ParseQuery<ParkingLot> getQuery() {
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
        ParseQuery<ParkingLot> query = ParseQuery.getQuery("ParkingLot");
        query.whereWithinKilometers(ParkingLot.KEY_LOCATION, parseGeoPoint, radius / 1000.0);
        return query;
    }

    private void setupList() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }

    private void getExtras() {
        Intent extras = getIntent();

        radius = extras.getIntExtra(EXTRA_RADIUS, radius);
        latLng = extras.getParcelableExtra(EXTRA_LATLNG);
        startDate = new Date(extras.getLongExtra(EXTRA_START_DATE, 0));
        endDate = new Date(extras.getLongExtra(EXTRA_END_DATE, 0));
    }

    private void getWidgets() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        textViewNoResult = (TextView) findViewById(R.id.text_view_no_result);
    }
}
