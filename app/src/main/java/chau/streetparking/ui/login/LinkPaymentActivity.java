package chau.streetparking.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;

import com.stripe.android.model.Card;

import chau.country.picker.CountryPicker;
import chau.country.picker.CountryPickerListener;
import chau.streetparking.R;
import chau.streetparking.backend.registration.AccountCreator;
import chau.streetparking.backend.registration.ResultCallBack;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.map.MapsActivity;
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
    public static final String EXTRA_IS_FACEBOOK = "extra_facebook";

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
    private boolean isFacebook;

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
            AccountCreator accountCreator = new AccountCreator(
                    this, card, email, mobile, password, firstName, lastName, avatarSelected, isFacebook,
                    new ResultCallBack() {
                        @Override
                        public void success(String userId) {
                            Logger.d(TAG, "saved, userId: " + userId);
                            goToMainActivity();
                        }

                        @Override
                        public void failure(String errorMessage) {
                            Logger.d(TAG, "Error: " + errorMessage);
                            showError(errorMessage);
                        }
                    }
            );

            accountCreator.saveAccount();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
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
            isFacebook = extras.getBooleanExtra(EXTRA_IS_FACEBOOK, false);
        }

    }

    private void getWidgets() {
        creditCardForm = (CreditCardForm) findViewById(R.id.credit_card_form);
        textViewCountry = (TextView) findViewById(R.id.country);
        editTextZip = (EditText) findViewById(R.id.zip);
    }

    private void showError(String errorMessage) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setMessage(errorMessage)
                .setPositiveButton("Ok", null)
                .show();

    }
}
