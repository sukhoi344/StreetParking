package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.parse.ParseObject;

import chau.streetparking.R;
import chau.streetparking.backend.BackendTest;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
    }

    /**
     * Called when "SIGN IN" button is clicked
     */
    public void onSignInClicked(View v) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    /**
     * Called when "REGISTER" button is clicked
     */
    public void onRegisterClicked(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();

//        BackendTest test = new BackendTest(this);
//        test.testUser();
    }
}
