package chau.streetparking.ui.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.stripe.model.Card;

import java.util.HashMap;
import java.util.Map;

import chau.streetparking.R;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.util.Logger;
import chau.streetparking.util.TextUtil;

/**
 * Created by Chau Thai on 6/20/2015.
 */
public class PaymentDetailActivity extends ColoredBarActivity {
    private static final String TAG = PaymentDetailActivity.class.getSimpleName();
    public static final String EXTRA_CARD = "extra_card";
    public static final String EXTRA_UPDATED = "extra_updated";

    // Widgets
    private View        cancelSaveLayout;
    private EditText    editTextNumber,
                        editTextMonth,
                        editTextYear,
                        editTextCVV;
//    private TextView    textViewCountry;
//    private Spinner     spinnerCardType;

    private Card card;
    private boolean updated = false;

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
//        setupSpinner();
//        setCountryPicker();
        setEditEnabled(false);

        getCard();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATED, updated);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onCancelClicked(View v) {
        setEditEnabled(false);
        setWidgetContent();
    }

    public void onSaveClicked(View v) {
        setEditEnabled(false);

        int month = Integer.parseInt(editTextMonth.getText().toString());
        int year = Integer.parseInt(editTextYear.getText().toString());

        new TaskUpdateCard(card, month, year).execute();
    }

    private void setEditEnabled(boolean enabled) {
        cancelSaveLayout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        editTextMonth.setEnabled(enabled);
        editTextYear.setEnabled(enabled);
//        editTextCVV.setEnabled(enabled);
//        editTextZip.setEnabled(enabled);
//        textViewCountry.setClickable(enabled);
//        spinnerCardType.setClickable(enabled);
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
//    }

//    private void setupSpinner() {
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.card_type_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCardType.setAdapter(adapter);
//        spinnerCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//
//                        break;
//                    case 1:
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

    private void setWidgetContent() {
        if (card != null) {
            editTextNumber.setText(TextUtil.getCodedNumber(card.getLast4()));

            String month = card.getExpMonth() + "";
            if (month.length() == 1)
                month = "0" + month;
            editTextMonth.setText(month);

            String year = card.getExpYear() + "";
            if (year.length() == 1)
                year = "0" + year;
            editTextYear.setText(year);

            editTextCVV.setText("•••");
//            textViewCountry.setText(card.getCountry());
//            editTextZip.setText(card.getAddressZip() == null? "" : card.getAddressZip());
        }
    }

    private void getCard() {
        String cardJSON = getIntent().getStringExtra(EXTRA_CARD);

        if (cardJSON != null) {
            card = Card.GSON.fromJson(cardJSON, Card.class);
        }
    }

    private void getWidgets() {
        cancelSaveLayout = findViewById(R.id.cancel_save_layout);
        editTextNumber = (EditText) findViewById(R.id.number);
        editTextMonth = (EditText) findViewById(R.id.month);
        editTextYear = (EditText) findViewById(R.id.year);
        editTextCVV = (EditText) findViewById(R.id.cvv);
//        editTextZip = (EditText) findViewById(R.id.zip);
//        textViewCountry = (TextView) findViewById(R.id.country);
//        spinnerCardType = (Spinner) findViewById(R.id.spinner);
    }

    private class TaskUpdateCard extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;
        private Card card;
        private int month, year;

        public TaskUpdateCard(Card card, int month, int year) {
            this.card = card;
            this.month = month;
            this.year = year;
        }

        @Override
        protected void onPreExecute() {
            if (card == null) {
                cancel(true);
                return;
            }

            dialog = new ProgressDialog(PaymentDetailActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Updating...");

            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (card != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("exp_month", month);
                    map.put("exp_year", year);

                    card.update(map);

                    return true;
                }
            } catch (Exception e) {
                Logger.printStackTrace(e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            try {
                if (dialog != null)
                    dialog.dismiss();

                if (success) {
                    updated = true;
                    Toast.makeText(PaymentDetailActivity.this, "Card has been updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentDetailActivity.this, "Error updating card", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Logger.printStackTrace(e);
            }
        }
    }
}
