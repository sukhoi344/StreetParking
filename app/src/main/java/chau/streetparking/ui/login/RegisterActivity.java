package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import chau.streetparking.R;
import chau.streetparking.backend.BackendTest;
import chau.streetparking.backend.registration.IdentityVerifier;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.util.DeviceUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/8/2015.
 */
public class RegisterActivity extends ColoredBarActivity {
    private static final String TAG = "RegisterActivity";

    private EditText    etEmail,
                        etMobile,
                        etPassword;
    private TextView    tvError1,
                        tvError2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
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

//        final IdentityVerifier identityVerifier = new IdentityVerifier(this, new IdentityVerifier.ResultCallback() {
//            @Override
//            public void handleResult(int result) {
//                if (result == IdentityVerifier.RESULT_PASS) {
//                    clearError();
//                    goToCreateProfile(email, mobile, password);
//                } else {
//                    showError(result);
//                }
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//                if (fault != null) {
//                    Logger.e(TAG, fault.getMessage());
//                    Toast.makeText(RegisterActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        identityVerifier.verify(email, mobile);

        // TODO: implement the verifier with Parse

        // Temporary bypass the verifier.
        goToCreateProfile(email, mobile, password);
    }

    private void goBackToStart() {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

    private void goToCreateProfile(String email, String mobile, String pass) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra(CreateProfileActivity.EXTRA_EMAIL, email);
        intent.putExtra(CreateProfileActivity.EXTRA_PASSWORD, pass);
        intent.putExtra(CreateProfileActivity.EXTRA_MOBILE, mobile);

        startActivity(intent);
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

    private void getWidgets() {
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etMobile = (EditText) findViewById(R.id.edit_text_mobile);
        etPassword = (EditText) findViewById(R.id.edit_text_password);
        tvError1 = (TextView) findViewById(R.id.text_view_error_1);
        tvError2 = (TextView) findViewById(R.id.text_view_error_2);
    }
}
