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
import com.parse.ParseUser;

import chau.streetparking.R;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.MapsActivity;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class SignInActivity extends ColoredBarActivity {
    private EditText etEmail, etPassword;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etPassword = (EditText) findViewById(R.id.edit_text_password);
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
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

    private void signIn(String email, String password) {
        showProgressDialog();

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

    private void showProgressDialog() {
            dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Signing in...");

            dialog.show();
    }
}
