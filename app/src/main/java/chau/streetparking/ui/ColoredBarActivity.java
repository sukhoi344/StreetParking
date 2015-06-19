package chau.streetparking.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import chau.streetparking.R;

/**
 * Activity with colored status bar and action bar.
 * The layout must contain Toolbar with id="toolbar".
 * Created by Chau Thai on 6/18/2015.
 */
public abstract class ColoredBarActivity extends AppCompatActivity {
    private Toolbar toolbar;

    /**
     * Set the main layout of this activity
     */
    protected abstract int getLayout();

    /**
     * Set the title of the toolbar
     */
    protected abstract String getTitleToolbar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        getWidgets();

        // Setup toolbar and statusBar
        setupToolbar();
        setupStatusBar();
    }

    private void setupStatusBar() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.primary_dark));

        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        findViewById(android.R.id.content).setPadding(0, config.getPixelInsetTop(false), 0, 0);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getTitleToolbar());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void getWidgets() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}