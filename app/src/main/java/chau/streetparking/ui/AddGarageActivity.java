package chau.streetparking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class AddGarageActivity extends ColoredBarActivity {
    private Spinner spinner;

    @Override
    protected int getLayout() {
        return R.layout.add_garage_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "ADD GARAGE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        setupSpinner();
    }

    public void onAddressClicked(View v) {
        Intent intent = new Intent(this, SearchLocationActivity.class);
        startActivity(intent);
    }

    private void setupSpinner() {
        if (spinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.price_type_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    private void getWidgets() {
        spinner = (Spinner) findViewById(R.id.spinner);
    }
}
