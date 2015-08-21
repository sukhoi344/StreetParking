package chau.streetparking.ui.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.parse.ParseUser;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;
import com.stripe.model.Card;
import com.stripe.model.Customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chau.country.picker.CountryPicker;
import chau.country.picker.CountryPickerListener;
import chau.streetparking.R;
import chau.streetparking.datamodels.parse.Credit;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class AddPaymentActivity extends ColoredBarActivity {
    public static final String EXTRA_ADDED = "extra_added";

    // Widgets
    private CreditCardForm creditCardForm;
//    private TextView textViewCountry;
//    private EditText editTextZip;

    @Override
    protected int getLayout() {
        return R.layout.add_payment_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "ADD PAYMENT";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
//        setCountryPicker();
    }

    public void onAddPaymentClicked(View v) {
        if (checkInput()) {
            new TaskCreateCard().execute();

        }
    }

//    public void onPaypalClicked(View v) {
//
//    }

    private boolean checkInput() {
//        String countryCode = textViewCountry.getText().toString();
//        String zipCode = editTextZip.getText().toString();

        if (!creditCardForm.isCreditCardValid()) {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

//    private void setCountryPicker() {
//        final CountryPicker countryPicker = CountryPicker.newInstance();
//
//        textViewCountry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                countryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
//            }
//        });
//
//        countryPicker.setListener(new CountryPickerListener() {
//            @Override
//            public void onSelectCountry(String name, String code) {
//                textViewCountry.setText(code);
//                countryPicker.dismiss();
//            }
//        });
//
//        textViewCountry.setText("US");
//    }

    private void getWidgets() {
        creditCardForm = (CreditCardForm) findViewById(R.id.credit_card_form);
//        textViewCountry = (TextView) findViewById(R.id.country);
//        editTextZip = (EditText) findViewById(R.id.zip);
    }

    private class TaskCreateCard extends AsyncTask<Void, Void, Customer> {
        private ProgressDialog dialog;
        private CreditCard creditCard;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AddPaymentActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Saving...");
            dialog.show();

            creditCard = creditCardForm.getCreditCard();
        }

        @Override
        protected Customer doInBackground(Void... params) {
            try {
                User user = (User) ParseUser.getCurrentUser();
                List<Credit> credits = user.getCredits();

                Credit credit = credits.get(0);
                credit = credit.fetchIfNeeded();

                if (credit == null)
                    return null;

                return Customer.retrieve(credit.getCustomerId());

            } catch (Exception e) {
                Logger.printStackTrace(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Customer customer) {
            try {
                if (customer != null) {
                    com.stripe.android.model.Card card = new com.stripe.android.model.Card(
                            creditCard.getCardNumber(),
                            creditCard.getExpMonth(),
                            creditCard.getExpYear(),
                            creditCard.getSecurityCode()
                    );

                    Stripe stripe = new Stripe(getResources().getString(R.string.stripe_test_publishable_key));
                    stripe.createToken(card, new TokenCallback() {
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(AddPaymentActivity.this, "Error adding card", Toast.LENGTH_SHORT).show();
                            if (dialog != null)
                                dialog.dismiss();
                        }

                        @Override
                        public void onSuccess(final Token token) {
                            saveCardToCustomer(token, customer);
                        }
                    });


                } else {
                    Toast.makeText(AddPaymentActivity.this, "Error adding card", Toast.LENGTH_SHORT).show();
                    if (dialog != null)
                        dialog.dismiss();
                }
            } catch (Exception e) {
                Logger.printStackTrace(e);
            }
        }

        private void saveCardToCustomer(final Token token, final Customer customer) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        customer.createCard(token.getId());
                        onSuccess();
                    } catch (Exception e) {
                        handleError(e);
                    }
                }

                private void handleError(Exception e) {
                    Logger.printStackTrace(e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddPaymentActivity.this, "Error adding card", Toast.LENGTH_SHORT).show();
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    });
                }

                private void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null)
                                dialog.dismiss();
                            Toast.makeText(AddPaymentActivity.this, "Card has been added", Toast.LENGTH_SHORT).show();

                            Intent data = new Intent();
                            data.putExtra(EXTRA_ADDED, true);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    });
                }
            }).start();
        }
    }
}
