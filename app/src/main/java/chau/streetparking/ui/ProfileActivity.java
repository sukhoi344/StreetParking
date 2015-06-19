package chau.streetparking.ui;

import android.os.Bundle;
import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/17/2015.
 */
public class ProfileActivity extends ColoredBarActivity {

    @Override
    protected int getLayout() {
        return R.layout.profile_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "PROFILE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
