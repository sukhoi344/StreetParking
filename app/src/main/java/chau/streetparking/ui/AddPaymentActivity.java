package chau.streetparking.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.devmarvel.creditcardentry.library.CreditCardForm;

import chau.country.picker.CountryPicker;
import chau.country.picker.CountryPickerListener;
import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class AddPaymentActivity extends ColoredBarActivity {

    // Widgets
    private CreditCardForm creditCardForm;
    private TextView textViewCountry;
    private EditText editTextZip;

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
        setCountryPicker();
    }

    public void onAddPaymentClicked(View v) {

    }

    public void onPaypalClicked(View v) {

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

    }

    private void getWidgets() {
        creditCardForm = (CreditCardForm) findViewById(R.id.credit_card_form);
        textViewCountry = (TextView) findViewById(R.id.country);
        editTextZip = (EditText) findViewById(R.id.zip);
    }
}