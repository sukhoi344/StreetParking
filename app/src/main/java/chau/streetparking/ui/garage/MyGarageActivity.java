package chau.streetparking.ui.garage;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chau.streetparking.R;
import chau.streetparking.datamodels.Garage;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.DividerItemDecoration;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class MyGarageActivity extends ColoredBarActivity {
    private static final int REQUEST_CODE_ADD_GARAGE = 1;

    // Widgets
    private ProgressBar  progressBar;
    private TextView     tvNoResult;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected int getLayout() {
        return R.layout.my_garage_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "MY GARAGE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        setupList();
        getGarages();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_ADD_GARAGE:
                getGarages();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_garage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Intent intent = new Intent(this, AddGarageActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_GARAGE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupList() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }

    private void getGarages() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        tvNoResult.setVisibility(View.INVISIBLE);

        ParseQuery<ParkingLot> query = getQuery();
        query.findInBackground(new FindCallback<ParkingLot>() {
            @Override
            public void done(List<ParkingLot> list, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                tvNoResult.setVisibility(View.INVISIBLE);

                if (e != null) {
                    Logger.printStackTrace(e);
                    Toast.makeText(MyGarageActivity.this, "Error retrieving garages", Toast.LENGTH_SHORT).show();
                } else if (list == null || list.isEmpty()) {
                    tvNoResult.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setAdapter(new GarageAdapter(MyGarageActivity.this, list));
                }
            }
        });
    }

    private ParseQuery<ParkingLot> getQuery() {
        ParseQuery<ParkingLot> query = ParseQuery.getQuery(ParkingLot.class);
        query.whereEqualTo(ParkingLot.KEY_OWNER, ParseUser.getCurrentUser());
        return query;
    }

    private void getWidgets() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvNoResult = (TextView) findViewById(R.id.text_view_no_result);
    }
}
