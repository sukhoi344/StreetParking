package chau.streetparking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/8/2015.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
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

    }

    private void goBackToStart() {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }
}
