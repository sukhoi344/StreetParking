package chau.streetparking.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.stripe.model.Card;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.backend.StripeHelper;
import chau.streetparking.datamodels.parse.Credit;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.DividerItemDecoration;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/19/15.
 */
public class PaymentActivity extends ColoredBarActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private static final int REQUEST_CODE_DETAIL = 1;
    private static final int REQUEST_CODE_ADD = 2;

    private View        contentView;
    private ProgressBar progressBar;
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
        getCards();
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
                Intent intent = new Intent(this, AddPaymentActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_DETAIL:
                if (resultCode == RESULT_OK && data != null) {
                    boolean updated = data.getBooleanExtra(PaymentDetailActivity.EXTRA_UPDATED, false);

                    if (updated) {
                        getCards();
                    }
                }
                break;

            case REQUEST_CODE_ADD:
                if (resultCode == RESULT_OK && data != null) {
                    boolean added = data.getBooleanExtra(AddPaymentActivity.EXTRA_ADDED, false);

                    if (added) {
                        getCards();
                    }
                }
                break;
        }
    }

    private void setupList() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }

    private void getCards() {
        setProgressBarVisible(true);
        User user = (User) ParseUser.getCurrentUser();

        if (user != null) {
            List<Credit> creditList = user.getCredits();

            if (creditList != null && !creditList.isEmpty()) {
                final Credit credit = creditList.get(0);

                final long creditT1 = System.currentTimeMillis();
                credit.fetchIfNeededInBackground(new GetCallback<Credit>() {
                    @Override
                    public void done(Credit parseObject, ParseException e) {
                        long creditTime = System.currentTimeMillis() - creditT1;
                        Logger.d(TAG, "credit fetch time: " + creditTime + "ms");

                        if (e != null) {
                            setProgressBarVisible(false);
                            Toast.makeText(PaymentActivity.this, "Error getting cards", Toast.LENGTH_SHORT).show();
                            Logger.d(TAG, "Error fetching Credit: " + e.getLocalizedMessage());
                        } else {
                            String customerId = credit.getCustomerId();

                            final long getCardT1 = System.currentTimeMillis();
                            StripeHelper.getCardsFromCustomer(PaymentActivity.this, customerId, new StripeHelper.GetCardsCallBack() {
                                @Override
                                public void done(List<Card> cards, String errorMessage) {
                                    setProgressBarVisible(false);

                                    try {
                                        long getCardTime = System.currentTimeMillis() - getCardT1;
                                        Logger.d(TAG, "get cards time: " + getCardTime + "ms");

                                        setProgressBarVisible(false);

                                        if (errorMessage != null) {
                                            Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        } else if (cards != null) {
                                            recyclerView.setAdapter(new PaymentsAdapter(PaymentActivity.this, cards, REQUEST_CODE_DETAIL));
                                        }

                                    } catch (Exception e) {
                                        if (Logger.DEBUG)
                                            e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                setProgressBarVisible(false);
            }
        }
    }

    private void setProgressBarVisible(boolean visible) {
        progressBar.setVisibility(visible? View.VISIBLE : View.INVISIBLE);
        contentView.setVisibility(visible? View.INVISIBLE : View.VISIBLE);
    }

    private void getWidgets() {
        contentView = findViewById(R.id.content_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
