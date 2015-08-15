package chau.streetparking.ui.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import chau.streetparking.MyApplication;
import chau.streetparking.R;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.MapsActivity;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class SignInActivity extends ColoredBarActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();
    private EditText etEmail, etPassword;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etPassword = (EditText) findViewById(R.id.edit_text_password);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOutInBackground();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_CODE_OFFSET:
                    showProgressDialog("Logging in...");
                    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.sign_in_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "SIGN IN";
    }

    @Override
    public void onBackPressed() {
        goBackToStart();
    }

    /**
     * Called when "CONNECT WITH FACEBOOK" button is selected
     */
    public void onFacebookClicked(View v) {
        List<String> permissions = Arrays.asList("public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    Logger.d(TAG, "facebook login done!");

                    if (parseUser.isNew()) {

                    } else {

                    }

                    Intent intent = new Intent(SignInActivity.this, VerifyAccountActivity.class);
                    startActivity(intent);
                } else {
                    // Error
                    Logger.d(TAG, "Error message: " + e == null? "null" : e.getLocalizedMessage());
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
     * Called when "DONE" TextView is clicked
     */
    public void onDoneClicked(View v) {
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if (email == null || email.isEmpty() || pass == null || pass.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
        } else {
            signIn(email, pass);
        }
    }

    /**
     * Called when "FORGOT PASSWORD?" TextView is clicked
     */
    public void onForgotClicked(View v) {

    }

    private void goBackToStart() {
        finish();
    }

    private void signIn(String email, String password) {
        showProgressDialog("Signing in...");

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    String errorMsg = e == null ? "Wrong email or password" : e.getLocalizedMessage();
                    Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignInActivity.this, MapsActivity.class);
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
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
}
