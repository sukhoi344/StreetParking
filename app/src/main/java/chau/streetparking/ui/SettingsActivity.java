package chau.streetparking.ui;


import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/27/2015.
 */
public class SettingsActivity extends ColoredBarActivity {
    @Override
    protected int getLayout() {
        return R.layout.settings_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "SETTINGS";
    }
}
