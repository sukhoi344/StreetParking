package chau.streetparking.ui.garage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import chau.streetparking.R;
import chau.streetparking.backend.GarageCreator;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.PhotosAdapter;
import chau.streetparking.ui.SearchLocationActivity;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class AddGarageActivity extends ColoredBarActivity {
    private static final int REQUEST_CODE_PHOTO = 1;
    private static final int REQUEST_CODE_ADDRESS = 2;

    // Widgets
    private EditText etName;
    private TextView tvAddress;
    private EditText etCapacity;
    private EditText etPrice;
    private EditText etInfo;
    private Spinner spinner;
    private RecyclerView recyclerView;

    private ProgressDialog dialog;
    private List<Uri> photoList = new ArrayList<>();
    private Address address;

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
        setupPhotosView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_PHOTO:
                if (data != null) {
                    final Uri uri = data.getData();
                    if (uri != null)
                        photoList.add(uri);
                    recyclerView.swapAdapter(new PhotosAdapter(AddGarageActivity.this, photoList), true);
                }
                break;
            case REQUEST_CODE_ADDRESS:
                if (data != null) {
                    address = data.getParcelableExtra(SearchLocationActivity.EXTRA_ADDRESS);

                    if (address != null && address.getMaxAddressLineIndex() >= 0) {
                        tvAddress.setText(address.getAddressLine(0));
                    }
                }
                break;
        }
    }

    public void onAddressClicked(View v) {
        Intent intent = new Intent(this, SearchLocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }

    public void onSaveClicked(View v) {
        if (checkInput()) {
            showProgressDialog();

            SaveCallback saveCallback = new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dismissProgressDialog();

                    if (e != null) {
                        Logger.printStackTrace(e);
                        Toast.makeText(AddGarageActivity.this, "Error saving garage", Toast.LENGTH_SHORT).show();
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            };

            GarageCreator garageCreator = new GarageCreator.Builder()
                    .setContext(this)
                    .setOwner((User) ParseUser.getCurrentUser())
                    .setName(etName.getText().toString())
                    .setCapacity(Integer.parseInt(etCapacity.getText().toString()))
                    .setLatLng(new LatLng(address.getLatitude(), address.getLongitude()))
                    .setAddress(tvAddress.getText().toString())
                    .setPrice(Double.parseDouble(etPrice.getText().toString()))
                    .setPriceType(getPriceType())
                    .setInfo(etInfo.getText().toString())
                    .setPhotoUriList(photoList)
                    .setSaveCallBack(saveCallback)
                    .build();

            garageCreator.createGarage();
        }
    }

    public void onAddPhotoClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, REQUEST_CODE_PHOTO);
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

    private String getPriceType() {
        int position = spinner.getSelectedItemPosition();

        switch (position) {
            case 0:
                return ParkingLot.PriceType.HOURLY;
            case 1:
                return ParkingLot.PriceType.DAILY;
            case 2:
                return ParkingLot.PriceType.MONTHLY;
        }

        return null;
    }

    private boolean checkInput() {
        String name = etName.getText().toString();
        String address = tvAddress.getText().toString();
        String capacity = etCapacity.getText().toString();
        String price = etPrice.getText().toString();

        if (name.isEmpty() || address.isEmpty() || capacity.isEmpty() || price.isEmpty() || address == null) {
            Toast.makeText(AddGarageActivity.this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Saving...");

        dialog.show();
    }

    private void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void setupPhotosView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void getWidgets() {
        etName = (EditText) findViewById(R.id.edit_text_name);
        tvAddress = (TextView) findViewById(R.id.text_view_address);
        etCapacity = (EditText) findViewById(R.id.edit_text_capacity);
        etPrice = (EditText) findViewById(R.id.edit_text_price);
        spinner = (Spinner) findViewById(R.id.spinner);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        etInfo = (EditText) findViewById(R.id.edit_text_info);
    }
}
