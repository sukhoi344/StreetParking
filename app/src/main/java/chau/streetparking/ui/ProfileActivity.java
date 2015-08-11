package chau.streetparking.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.User;

/**
 * Created by Chau Thai on 6/17/2015.
 */
public class ProfileActivity extends ColoredBarActivity {
    public static final String EXTRA_PROFILE = "extra_profile";
    public static final int PROFILE_NO_CHANGE = 1;
    public static final int PROFILE_LOG_OUT = 2;
    public static final int PROFILE_UPDATED = 3;

    private EditText    etFirstName,
                        etLastName,
                        etMobile,
                        etEmail;
    private Button      btnSave;

    private User        user;
    private ProgressDialog dialog;
    private boolean updated = false;

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
        setupUser();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PROFILE, updated? PROFILE_UPDATED : PROFILE_NO_CHANGE);
        setResult(RESULT_OK, intent);
        finish();
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
        String email = etEmail.getText().toString();
        String first = etFirstName.getText().toString();
        String last = etLastName.getText().toString();
        String mobile = etMobile.getText().toString();

        if (checkInput(email, first, last, mobile)) {
            showProgressDialog("Saving");

            user.setEmail(email);
            user.setFirstName(first);
            user.setLastName(last);
            user.setMobile(mobile);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ProfileActivity.this, "Your profile has been updated", Toast.LENGTH_SHORT).show();
                        setEditEnable(false);
                        updated = true;
                    } else {
                        Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (dialog != null)
                        dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEditEnable(boolean enable) {
        etEmail.setEnabled(enable);
        etFirstName.setEnabled(enable);
        etLastName.setEnabled(enable);
        etMobile.setEnabled(enable);
        btnSave.setVisibility(enable? View.VISIBLE : View.GONE);
    }

    private void setupUser() {
        user = (User) ParseUser.getCurrentUser();

        etEmail.setText(user.getEmail());
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etMobile.setText(user.getMobile());
    }

    private void signOut() {
        showProgressDialog("Logging out...");

        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (dialog != null)
                    dialog.dismiss();

                Intent intent = new Intent();
                intent.putExtra(EXTRA_PROFILE, PROFILE_LOG_OUT);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean checkInput(String email, String first, String last, String mobile) {
        if (email == null || first == null || last == null || mobile == null)
            return false;
        if (email.isEmpty() || first.isEmpty() || last.isEmpty() || mobile.isEmpty())
            return false;

        return true;
    }

    private void getWidgets() {
        etEmail = (EditText) findViewById(R.id.email);
        etFirstName = (EditText) findViewById(R.id.first_name);
        etLastName = (EditText) findViewById(R.id.last_name);
        etMobile = (EditText) findViewById(R.id.mobile);
        btnSave = (Button) findViewById(R.id.save);
    }

    private void showProgressDialog(String message) {
        dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);

        dialog.show();
    }
}
