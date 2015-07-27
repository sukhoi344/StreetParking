package chau.streetparking.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import chau.country.picker.CountryPicker;
import chau.country.picker.CountryPickerListener;
import chau.streetparking.R;
import chau.streetparking.datamodels.CardItem;
import chau.streetparking.datamodels.CardTypes;
import chau.streetparking.util.TextUtil;

/**
 * Created by Chau Thai on 6/20/2015.
 */
public class PaymentDetailActivity extends ColoredBarActivity {
    public static final String EXTRA_CARD = "extra_card";

    // Widgets
    private View cancelSaveLayout;
    private EditText    editTextNumber,
                        editTextMonth,
                        editTextYear,
                        editTextCVV,
                        editTextZip;
    private TextView    textViewCountry;
    private Spinner     spinnerCardType;

    private CardItem cardItem;

    @Override
    protected int getLayout() {
        return R.layout.payment_detail_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "PAYMENT";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        setupSpinner();
        setCountryPicker();
        setEditEnabled(false);

        cardItem = getIntent().getParcelableExtra(EXTRA_CARD);
        setWidgetContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                setEditEnabled(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCancelClicked(View v) {
        setEditEnabled(false);
        setWidgetContent();
    }

    public void onSaveClicked(View v) {
        setEditEnabled(false);
    }

    private void setEditEnabled(boolean enabled) {
        cancelSaveLayout.setVisibility(enabled? View.VISIBLE : View.INVISIBLE);
        editTextMonth.setEnabled(enabled);
        editTextYear.setEnabled(enabled);
        editTextCVV.setEnabled(enabled);
        editTextZip.setEnabled(enabled);
        textViewCountry.setClickable(enabled);
        spinnerCardType.setClickable(enabled);
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

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.card_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCardType.setAdapter(adapter);
        spinnerCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setWidgetContent() {
        if (cardItem != null) {
            editTextNumber.setText(TextUtil.getCodedNumber(cardItem.getNumber()));

            String month = cardItem.getExpMonth() + "";
            if (month.length() == 1)
                month = "0" + month;
            editTextMonth.setText(month);

            String year = cardItem.getExpYear() + "";
            if (year.length() == 1)
                year = "0" + year;
            editTextYear.setText(year);

            editTextCVV.setText(cardItem.getCvv() + "");
            textViewCountry.setText(cardItem.getCountry());
            editTextZip.setText(cardItem.getZipCode());

            switch (cardItem.getType()) {
                case CardTypes.PERSONAL:
                    spinnerCardType.setSelection(0);
                    break;
                case CardTypes.BUSINESS:
                    spinnerCardType.setSelection(1);
                    break;
            }
        }
    }

    private void getWidgets() {
        cancelSaveLayout = findViewById(R.id.cancel_save_layout);
        editTextNumber = (EditText) findViewById(R.id.number);
        editTextMonth = (EditText) findViewById(R.id.month);
        editTextYear = (EditText) findViewById(R.id.year);
        editTextCVV = (EditText) findViewById(R.id.cvv);
        editTextZip = (EditText) findViewById(R.id.zip);
        textViewCountry = (TextView) findViewById(R.id.country);
        spinnerCardType = (Spinner) findViewById(R.id.spinner);
    }
}