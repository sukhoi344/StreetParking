package chau.streetparking.ui.curtain;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chau.streetparking.R;
import chau.streetparking.ui.LocationsAdapter;

/**
 * Created by Chau Thai on 6/14/2015.
 */
public class SearchLocationActivity extends AppCompatActivity {
    public static final String EXTRA_ADDRESS = "extra_address";
    private static final int MAX_RESULTS = 10;

    // Widgets
    private View iconSearch;
    private View progressBar;
    private EditText editText;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Geocoder geocoder;
    private TaskFindAddress taskFindAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location_activity);
        getWidgets();
        setupList();
        setupEditText();
    }

    private void setupEditText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (taskFindAddress != null && taskFindAddress.isWorking) {
                    taskFindAddress.cancel(true);
                }

                if (s.length() == 0) {
                    recyclerView.swapAdapter(new LocationsAdapter(SearchLocationActivity.this,
                            new ArrayList<Address>()), true);
                    iconSearch.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    taskFindAddress = new TaskFindAddress(s.toString());
                    taskFindAddress.execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void setupList() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private List<String> createTestList() {
        List<String> list = new ArrayList<>();
        list.add("Canada");
        list.add("4849 Connecticut ave");
        list.add("Vietnam");
        return list;
    }

    private void getWidgets() {
        editText = (EditText) findViewById(R.id.edit_text);
        iconSearch = findViewById(R.id.icon_search);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private class TaskFindAddress extends AsyncTask<Void, Void, List<Address>> {
        private boolean isWorking = false;
        private String text;

        public TaskFindAddress(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            isWorking = true;
            iconSearch.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Address> doInBackground(Void... params) {
            List<Address> list = new ArrayList<>();

            try {
                list = geocoder.getFromLocationName(text, MAX_RESULTS);

            } catch (Exception e) {
            } finally {
                isWorking = false;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            try {
                iconSearch.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                if (!isCancelled()) {
                    recyclerView.swapAdapter(new LocationsAdapter(SearchLocationActivity.this,
                            addresses), true);
                }

            } catch (Exception e) {

            }
        }
    }
}
