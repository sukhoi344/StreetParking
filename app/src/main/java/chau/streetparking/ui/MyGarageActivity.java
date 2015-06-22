package chau.streetparking.ui;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chau.streetparking.R;
import chau.streetparking.datamodels.Garage;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class MyGarageActivity extends ColoredBarActivity {
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
                startActivity(intent);
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

        recyclerView.setAdapter(new GarageAdapter(this, createTestList()));
    }

    private List<Garage> createTestList() {
        List<Garage> list = new ArrayList<>();

        Address address1 = new Address(Locale.US);
        address1.setAddressLine(0, "4849 Connecticut ave");

        Garage garage1 = new Garage(
                "Garage 1",
                address1,
                4, 3, 1.99, Garage.PRICE_TYPE_HOURLY
        );

        Garage garage2 = new Garage(
                "Garage 2",
                address1,
                4, 3, 1.99, Garage.PRICE_TYPE_HOURLY
        );

        Garage garage3 = new Garage(
                "Garage 3",
                address1,
                4, 3, 1.99, Garage.PRICE_TYPE_HOURLY
        );

        list.add(garage1);
        list.add(garage2);
        list.add(garage3);

        return list;
    }

    private void getWidgets() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
