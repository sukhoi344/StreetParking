package chau.streetparking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import chau.streetparking.R;
import chau.streetparking.backend.BackendTest;
import chau.streetparking.ui.ColoredBarActivity;

/**
 * Created by Chau Thai on 6/8/2015.
 */
public class RegisterActivity extends ColoredBarActivity {

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
        BackendTest backendTest = new BackendTest(this);
        backendTest.testFind();
    }

    private void goBackToStart() {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }
}
