package chau.streetparking.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/17/2015.
 */
public class ProfileActivity extends ColoredBarActivity {
    private EditText    etFirstName,
                        etLastName,
                        etMobile,
                        etEmail;
    private Button      btnSave;

    @Override
    protected int getLayout() {
        return R.layout.profile_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "PROFILE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                setEditEnable(true);
                return true;
            case R.id.menu_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save button clicked event callback
     */
    public void onSaveClicked(View v) {
    }

    private void setEditEnable(boolean enable) {
        etEmail.setEnabled(enable);
        etFirstName.setEnabled(enable);
        etLastName.setEnabled(enable);
        etMobile.setEnabled(enable);
        btnSave.setVisibility(enable? View.VISIBLE : View.GONE);
    }

    private void signOut() {
        finish();
    }

    private void getWidgets() {
        etEmail = (EditText) findViewById(R.id.email);
        etFirstName = (EditText) findViewById(R.id.first_name);
        etLastName = (EditText) findViewById(R.id.last_name);
        etMobile = (EditText) findViewById(R.id.mobile);
        btnSave = (Button) findViewById(R.id.save);
    }
}
