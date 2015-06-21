package chau.streetparking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wallet.WalletConstants;

import java.util.ArrayList;
import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.CardItem;
import chau.streetparking.datamodels.CardTypes;

/**
 * Created by Chau Thai on 6/19/15.
 */
public class PaymentActivity extends ColoredBarActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected int getLayout() {
        return R.layout.payment_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "Payment";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        setupList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
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

        recyclerView.setAdapter(new PaymentsAdapter(this, createTestSList()));
    }

    private List<CardItem> createTestSList() {
        List<CardItem> list = new ArrayList<>();

        CardItem item1 = new CardItem(1, WalletConstants.CardNetwork.VISA,
                CardTypes.PERSONAL, "2442 4441 2234 1124", 5,12,355,"US", "20015");
        CardItem item2 = new CardItem(2, WalletConstants.CardNetwork.MASTERCARD,
                CardTypes.BUSINESS, "2314 5535 1232, 5435", 5,12,123,"US", "20015");
        CardItem item3 = new CardItem(3, WalletConstants.CardNetwork.DISCOVER,
                CardTypes.PERSONAL, "2323 1312 1242 1341", 5,12,456,"US", "20015");

        list.add(item1);
        list.add(item2);
        list.add(item3);

        return list;
    }

    private void getWidgets() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
