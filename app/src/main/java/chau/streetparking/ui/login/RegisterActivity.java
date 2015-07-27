package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.exceptions.BackendlessFault;

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
        String email = etEmail.getText().toString();
        String mobile = etMobile.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 5) {
            Toast.makeText(this, "Password must have at least 5 characters", Toast.LENGTH_SHORT).show();
        }

        if (!DeviceUtil.hasConnection(this)) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final IdentityVerifier identityVerifier = new IdentityVerifier(this, new IdentityVerifier.ResultCallback() {
            @Override
            public void handleResult(int result) {
                switch (result) {
                    case IdentityVerifier.RESULT_PASS:
                        Logger.d(TAG, "Pass");
                        break;
                    case IdentityVerifier.RESULT_DUPLICATE_EMAIL:
                        Logger.d(TAG, "Duplicate email");
                        break;
                    case IdentityVerifier.RESULT_DUPLICATE_MOBILE:
                        Logger.d(TAG, "Duplicate mobile");
                        break;
                    case IdentityVerifier.RESULT_DUPLICATE_BOTH:
                        Logger.d(TAG, "Duplicate both");
                        break;
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (fault != null) {
                    Logger.e(TAG, fault.getMessage());
                    Toast.makeText(RegisterActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        identityVerifier.verify(email, mobile);
    }

    private void goBackToStart() {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

    private void getWidgets() {
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etMobile = (EditText) findViewById(R.id.edit_text_mobile);
        etPassword = (EditText) findViewById(R.id.edit_text_password);
    }
}
