package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;

import com.google.gson.Gson;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

import chau.country.picker.CountryPicker;
import chau.country.picker.CountryPickerListener;
import chau.streetparking.R;
import chau.streetparking.backend.JsonHelper;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 7/28/15
 */
public class LinkPaymentActivity extends ColoredBarActivity {
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_MOBILE = "extra_mobile";
    public static final String EXTRA_PASS = "extra_p";
    public static final String EXTRA_FIRST = "extra_first";
    public static final String EXTRA_LAST = "extra_last";
    public static final String EXTRA_AVATAR_SELECTED = "extra_avatar_selected";

    private static final String TAG = LinkPaymentActivity.class.getSimpleName();

    // Widgets
    private CreditCardForm creditCardForm;
    private TextView textViewCountry;
    private EditText editTextZip;

    private String  email,
                    mobile,
                    password,
                    firstName,
                    lastName;
    private boolean avatarSelected;

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        getExtras();
        setCountryPicker();
    }

    @Override
    protected int getLayout() {
        return R.layout.link_payment_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "LINK PAYMENT";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                linkPayment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onPaypalClicked(View v) {

    }

    private void linkPayment() {
        if (checkInput()) {
            try {
                Stripe stripe = new Stripe(getString(R.string.stripe_test_publishable_key));
                stripe.createToken(card, new TokenCallback() {
                    @Override
                    public void onError(Exception e) {
                        Logger.d(TAG, "Error: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(Token token) {
                        Logger.d(TAG, "Got the token, now charging...");
                        charge(token);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LinkPaymentActivity.this, "Error linking payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void charge(Token token) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 100); // 100 cents
        params.put("currency", "usd");
        params.put("card", token.getId());

        ParseCloud.callFunctionInBackground("charge", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (o != null) {
                    Logger.d(TAG, "success");

                    Charge charge = JsonHelper.jsonToCharge(o.toString());

                    if (charge != null) {
                        Logger.d(TAG, "charge: " + charge.toString());
                    } else {
                        Logger.d(TAG, "json error");
                    }

                } else if (e != null) {
                    Logger.d(TAG, "error: " + e.getLocalizedMessage());
                } else {
                    Logger.d(TAG, "unknown error");
                }
            }
        });
    }

    private boolean checkInput() {
        String countryCode = textViewCountry.getText().toString();
        String zipCode = editTextZip.getText().toString();

        if (!creditCardForm.isCreditCardValid() || countryCode.isEmpty() || zipCode.isEmpty()) {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return false;
        }

        CreditCard creditCard = creditCardForm.getCreditCard();

        if (creditCard != null) {
            card = new Card(creditCard.getCardNumber(), creditCard.getExpMonth(), creditCard.getExpYear(),
                    creditCard.getSecurityCode());
        }

        if (creditCard == null || card == null || !card.validateCard()) {
            Toast.makeText(this, "Invalid credit card", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setCountryPicker() {
        final CountryPicker countryPicker = CountryPicker.newInstance();

        textViewCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        countryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code) {
                textViewCountry.setText(code);
                countryPicker.dismiss();
            }
        });

        textViewCountry.setText("US");
    }

    private void getExtras() {
        Intent extras = getIntent();

        if (extras != null) {
            email = extras.getStringExtra(EXTRA_EMAIL);
            mobile = extras.getStringExtra(EXTRA_MOBILE);
            password = extras.getStringExtra(EXTRA_PASS);
            firstName = extras.getStringExtra(EXTRA_FIRST);
            lastName = extras.getStringExtra(EXTRA_LAST);
            avatarSelected = extras.getBooleanExtra(EXTRA_AVATAR_SELECTED, false);
        }

    }

    private void getWidgets() {
        creditCardForm = (CreditCardForm) findViewById(R.id.credit_card_form);
        textViewCountry = (TextView) findViewById(R.id.country);
        editTextZip = (EditText) findViewById(R.id.zip);
    }
}
