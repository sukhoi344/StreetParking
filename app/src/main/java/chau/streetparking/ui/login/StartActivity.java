package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.parse.ParseUser;

import chau.streetparking.R;
import chau.streetparking.backend.BackendTest;
import chau.streetparking.ui.MapsActivity;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class StartActivity extends AppCompatActivity {
    public static final int REQUEST_EXIT = 1;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkPlayServices()) {
            if (ParseUser.getCurrentUser() != null) {
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(StartActivity.this, "Google Play Services must be installed.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case REQUEST_EXIT:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }

    /**
     * Called when "SIGN IN" button is clicked
     */
    public void onSignInClicked(View v) {
        startActivityForResult(new Intent(this, SignInActivity.class), REQUEST_EXIT);
    }

    /**
     * Called when "REGISTER" button is clicked
     */
    public void onRegisterClicked(View v) {
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_EXIT);

//        BackendTest test = new BackendTest(this);
//        test.testPointerRetrive();
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }
}
