package chau.streetparking.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import chau.streetparking.MyApplication;
import chau.streetparking.R;
import chau.streetparking.backend.registration.IdentityVerifier;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.map.MapsActivity;
import chau.streetparking.util.DeviceUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/8/2015.
 */
public class RegisterActivity extends ColoredBarActivity {
    public static final int REQUEST_EXIT = 1;
    private static final String TAG = "RegisterActivity";

    private EditText    etEmail,
                        etMobile,
                        etPassword;
    private TextView    tvError1,
                        tvError2;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EXIT:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case MyApplication.REQUEST_CODE_OFFSET:
                    showProgressDialog("Logging in...");
                    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.register_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "CREATE AN ACCOUNT";
    }

    @Override
    public void onBackPressed() {
        goBackToStart();
    }

    public void onFacebookClicked(View v) {
        List<String> permissions = Arrays.asList("public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    Logger.d(TAG, "facebook login done!");

                    if (parseUser.isNew() || parseUser.getEmail() == null) {
                        Intent intent = new Intent(RegisterActivity.this, VerifyAccountActivity.class);
                        startActivityForResult(intent, REQUEST_EXIT);
                    } else {
                        goToMap();
                    }
                } else {
                    // Error
                    Logger.d(TAG, "Error message: " + e == null ? "null" : e.getLocalizedMessage());
                    if (parseUser == null)
                        Logger.d(TAG, "parseUser is null");
                }

                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    /**
     * Called when "CANCEL" TextView is clicked
     */
    public void onCancelClicked(View v) {
        goBackToStart();
    }

    /**
     * Called when "NEXT" TextView is clicked
     */
    public void onNextClicked(View v) {
        // TODO: format phone number
        final String email = etEmail.getText().toString();
        final String mobile = etMobile.getText().toString();
        final String password = etPassword.getText().toString();

        if (email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 5) {
            Toast.makeText(this, "Password must have at least 5 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!DeviceUtil.hasConnection(this)) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final IdentityVerifier identityVerifier = new IdentityVerifier(this, new IdentityVerifier.ResultCallback() {
            @Override
            public void handleResult(int result) {
                if (result == IdentityVerifier.RESULT_PASS) {
                    clearError();
                    goToCreateProfile(email, mobile, password);
                } else {
                    showError(result);
                }
            }

            @Override
            public void handleFault(ParseException fault) {
                if (fault != null) {
                    Logger.e(TAG, fault.getMessage());
                    Toast.makeText(RegisterActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        identityVerifier.verify(email, mobile);
    }

    private void goBackToStart() {
        finish();
    }

    private void goToCreateProfile(String email, String mobile, String pass) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra(CreateProfileActivity.EXTRA_EMAIL, email);
        intent.putExtra(CreateProfileActivity.EXTRA_PASSWORD, pass);
        intent.putExtra(CreateProfileActivity.EXTRA_MOBILE, mobile);

        startActivityForResult(intent, REQUEST_EXIT);
    }

    private void goToMap() {
        Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

    private void showError(int errorCode) {
        switch (errorCode) {
            case IdentityVerifier.RESULT_DUPLICATE_EMAIL:
                tvError1.setText(getString(R.string.error_duplicate_email));
                tvError2.setText("");
                break;
            case IdentityVerifier.RESULT_DUPLICATE_MOBILE:
                tvError1.setText(getString(R.string.error_duplicate_mobile));
                tvError2.setText("");
                break;
            case IdentityVerifier.RESULT_DUPLICATE_BOTH:
                tvError1.setText(getString(R.string.error_duplicate_email));
                tvError2.setText(getString(R.string.error_duplicate_mobile));
                break;
        }
    }

    private void clearError() {
        tvError1.setText("");
        tvError2.setText("");
    }

    private void showProgressDialog(String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);

        dialog.show();
    }

    private void getWidgets() {
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etMobile = (EditText) findViewById(R.id.edit_text_mobile);
        etPassword = (EditText) findViewById(R.id.edit_text_password);
        tvError1 = (TextView) findViewById(R.id.text_view_error_1);
        tvError2 = (TextView) findViewById(R.id.text_view_error_2);
    }
}
