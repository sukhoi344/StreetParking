package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import chau.streetparking.R;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.ui.MapsActivity;

/**
 * Created by Chau Thai on 6/7/2015.
 */
public class SignInActivity extends ColoredBarActivity {

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
        startActivity(new Intent(this, MapsActivity.class));
        finish();
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
}
