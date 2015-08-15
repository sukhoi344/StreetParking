package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.parse.ParseUser;

import chau.streetparking.R;
import chau.streetparking.backend.BackendTest;
import chau.streetparking.ui.MapsActivity;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class StartActivity extends AppCompatActivity {
    public static final int REQUEST_EXIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (ParseUser.getCurrentUser() != null) {
//            Intent intent = new Intent(this, MapsActivity.class);
//            startActivity(intent);
//            finish();
//        }

        setContentView(R.layout.start_activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_EXIT) {
            finish();
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
}
