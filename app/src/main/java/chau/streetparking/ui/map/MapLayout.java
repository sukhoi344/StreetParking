package chau.streetparking.ui.map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import chau.streetparking.R;
import chau.streetparking.util.DateUtil;
import chau.streetparking.util.ImageUtil;

/**
 * Created by Chau Thai on 6/9/2015.
 */
public class MapLayout extends FrameLayout implements TimePickerDialog.OnTimeSetListener,
                                                    DatePickerDialog.OnDateSetListener {
    private static final String TAG_FROM = "from";
    private static final String TAG_TO = "to";
    public static final int SEEK_BAR_DEFAULT_VALUE_IN_FEET = 300;

    // The map
    private View map;
    private View mapContainer;
    private View myLocationBtn;

    // Widgets
    private ViewGroup   locationLayout;

    // Search Detail panel
    private FloatingActionButton    fab;
    private View                    revealView;
    private EditText                etLocation;
    private Button                  btnCalendarStarting;
    private Button                  btnCalendarEnding;
    private RangeBar                rangeBarStarting;
    private RangeBar                rangeBarEnding;
    private TextView                tvStarting;
    private TextView                tvEnding;
    private Button                  btnSearchDetailCancel;
    private Button                  btnSearchDetailDone;

    // Listener
    private OnClickListener btnDoneOnClick;
    private OnClickListener btnCancelOnClick;

    // Suggest locations View
    private LocationSuggestView viewSuggest;
    private AddressWrapper addressWrapper;

    private TextView    tvLocation;

    private Date   startDate, endDate;
    private TimeWrapper timeWrapper;
    private String currentTag = TAG_FROM;

    public interface OnLayoutMoved {
        /**
         * @param ratio 0 means the layout hasn't been changed, 1 means changed entirely
         */
        void onLayoutMoved(double ratio);
    }

    public MapLayout(Context context) {
        super(context);
        init(context);
    }

    public MapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public MapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void onDateSet(DatePickerDialog picker, int year, int monthOfYear, int dayOfMonth) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.setAccentColor(getContext().getResources().getColor(R.color.primary));

        try {
            Activity activity = (Activity) getContext();
            timePickerDialog.show(activity.getFragmentManager(), picker.getTag());
        } catch (Exception ignore) {}

        timeWrapper = new TimeWrapper(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(RadialPickerLayout picker, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(timeWrapper.year, timeWrapper.month, timeWrapper.day, hourOfDay, minute);

        long minDiff = (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) /
                (1000 * 60);
        if (minDiff < 0) {
            Toast.makeText(getContext(), "Invalid time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentTag == TAG_FROM) {
            startDate = cal.getTime();
            tvStarting.setText(getDateStringScheduled((int) minDiff));
            setEndingDate(rangeBarEnding.getRightIndex());
        } else {
            String timeString = getDateStringScheduled((int) minDiff);
            timeString += " (" + getHoursStringAhead((int) minDiff) + ")";
            tvEnding.setText(timeString);
        }
    }


    /**
     * Set the text for the Parking Location layout
     * @param location
     */
    public void setLocationText(String location) {
        tvLocation.setText(location);
    }

    public void setLocationLayoutOnClick(OnClickListener onClickListener) {
        if (locationLayout != null && onClickListener != null) {
            locationLayout.setOnClickListener(onClickListener);
        }
    }

    public void setOnLayoutMoved(OnLayoutMoved onLayoutMoved) {
//        this.onLayoutMoved = onLayoutMoved;
    }

    public void setMyLocationBtnMargin(int top) {
        if (myLocationBtn != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myLocationBtn.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, top, layoutParams.rightMargin, layoutParams.bottomMargin);
            myLocationBtn.setLayoutParams(layoutParams);
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getSearchText() {
        return etLocation.getText().toString();
    }

    public LatLng getSearchLatLng() {
        if (addressWrapper == null)
            return null;

        String searchText = etLocation.getText().toString();
        if (searchText.equals(addressWrapper.text)) {
            return new LatLng(addressWrapper.address.getLatitude(),
                    addressWrapper.address.getLongitude());
        }

        return null;
    }

    public SearchDetail getSearchDetail() {
        return new SearchDetail(
                startDate,
                endDate,
                getSearchText(),
                getSearchLatLng()
        );
    }

    public void setBtnDoneOnClick(OnClickListener onClick) {
        this.btnDoneOnClick = onClick;
    }

    public void setBtnCancelOnClick(OnClickListener onClick) {
        this.btnCancelOnClick = onClick;
    }

    public View getMapContainer() {
        return mapContainer;
    }

    private void setupSearchDetail() {
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeSearchDetail();
                revealView.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_up);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        fab.hide();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        revealView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                revealView.startAnimation(animation);
            }
        });

        btnSearchDetailCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSuggest.hide();

                if (btnCancelOnClick != null)
                    btnCancelOnClick.onClick(v);

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        revealView.setVisibility(View.INVISIBLE);
                        fab.show();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                revealView.startAnimation(animation);
            }
        });

        btnSearchDetailDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnDoneOnClick != null)
                    btnDoneOnClick.onClick(v);
            }
        });

        btnCalendarStarting.setOnClickListener(new TimeTextViewListener(TAG_FROM));
        btnCalendarEnding.setOnClickListener(new TimeTextViewListener(TAG_TO));

        setTimeSearchDetail();
//        revealView.setVisibility(View.VISIBLE);
    }

    private void setStartEndDates() {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        endCal.add(Calendar.HOUR, 3);

        startDate = startCal.getTime();
        endDate = endCal.getTime();
    }

    private void setTimeSearchDetail() {
        rangeBarStarting.setSeekPinByValue(0.0f);
        rangeBarEnding.setSeekPinByValue(6.0f);

        rangeBarStarting.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                int value = rightPinIndex;
                int minutes = (int) (value / 2.0f * 60);

                tvStarting.setText(getDateStringScheduled(minutes));

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, minutes);
                startDate = cal.getTime();

                // Correct the ending date
                setEndingDate(rangeBarEnding.getRightIndex());
            }
        });

        rangeBarEnding.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                setEndingDate(rightPinIndex);
            }
        });

        String endingString = getDateStringScheduled(60 * 3) + " (" + getHoursStringAhead(60 * 3) + ")";

        tvStarting.setText(getDateStringScheduled(0));
        tvEnding.setText(endingString);
    }

    private void setEndingDate(int rightPinIndex) {
        int value = rightPinIndex + 1;
        int minAfterAdded = (int) (value / 2.0f * 60);

        Calendar currentCal = Calendar.getInstance();
        int minStartAdded =  (int) (startDate.getTime() - currentCal.getTimeInMillis()) / (1000 * 60);
        int totalMin = minAfterAdded + minStartAdded;

        String timeString = getDateStringScheduled(totalMin);
        timeString += " (" + getHoursStringAhead(totalMin) + ")";

        tvEnding.setText(timeString);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, totalMin);
        endDate = cal.getTime();
    }

    private String getDateStringScheduled(int minutes) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.add(Calendar.MINUTE, minutes);

        int dayOfYearScheduled = scheduledCalendar.get(Calendar.DAY_OF_YEAR);
        int dayOfYearCurrent = currentCalendar.get(Calendar.DAY_OF_YEAR);

        String timeString = "";

        // Today
        if (dayOfYearCurrent == dayOfYearScheduled) {
            timeString += "Today at ";
        } else if (dayOfYearScheduled - dayOfYearCurrent == 1) {
            timeString += "Tomorrow at ";
        } else {
            timeString += DateUtil.getStringFromDate(scheduledCalendar.getTime(), "MM/dd") + " at ";
        }

        timeString += DateUtil.getStringFromDate(scheduledCalendar.getTime(), "hh:mm aa");
        return timeString;
    }

    private String getHoursStringAhead(int minutes) {
        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.add(Calendar.MINUTE, minutes);

        long minDiff = (scheduledCalendar.getTimeInMillis() - startDate.getTime()) / (1000 * 60);
        double hourDiff = minDiff / 60.0;
        String hourText = String.format("%.1f", hourDiff);

        return hourText + " " + (hourDiff <= 1? "hour" : "hours");
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();

        setStartEndDates();
        setupSearchDetail();
        setupSuggestView();
    }

    private void setupSuggestView() {
        viewSuggest = new LocationSuggestView(getContext());

        addView(viewSuggest);
        viewSuggest.setClickable(true);
        viewSuggest.setVisibility(View.INVISIBLE);

        viewSuggest.setOnSuggestSelectedListener(new LocationSuggestView.OnSuggestSelectedListener() {
            @Override
            public void onSelected(Address address) {
                String location = address.getAddressLine(0);
                if (address.getMaxAddressLineIndex() >= 1)
                    location += ", " + address.getAddressLine(1);

                // Save it
                addressWrapper = new AddressWrapper();
                addressWrapper.text = location;
                addressWrapper.address = address;

                etLocation.setText(location);
                etLocation.setSelection(location.length());
                viewSuggest.hide();
            }
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int[] location = new int[2];
                etLocation.getLocationInWindow(location);
                int bottomMargin = getHeight() - location[1] - ImageUtil.getPixelFromDP(65);

                viewSuggest.setMargin(bottomMargin, location[0], location[0]);
                viewSuggest.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private class TimeTextViewListener implements OnClickListener {
        private final String TAG;

        public TimeTextViewListener(String TAG) {
            this.TAG = TAG;
        }

        @Override
        public void onClick(View v) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    MapLayout.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setAccentColor(getContext().getResources().getColor(R.color.primary));

            try {
                Activity activity = (Activity) getContext();
                datePickerDialog.show(activity.getFragmentManager(), TAG);
                currentTag = TAG;

            } catch (Exception ignore) {

            }
        }
    }

    private void getWidgets() {
        mapContainer = findViewById(R.id.map_container);
        locationLayout = (ViewGroup) findViewById(R.id.location_layout);
        tvLocation = (TextView) findViewById(R.id.tv_location);

        SupportMapFragment supportMapFragment = (SupportMapFragment) ((FragmentActivity) getContext())
                .getSupportFragmentManager().findFragmentById(R.id.map);
        map = supportMapFragment.getView();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        revealView = findViewById(R.id.reveal);
//        revealToolbar = new RevealToolbar((Activity) getContext(), revealView, fab);
        etLocation = (EditText) findViewById(R.id.location_address);
        btnCalendarStarting = (Button) findViewById(R.id.btn_calendar_starting);
        btnCalendarEnding = (Button) findViewById(R.id.btn_calendar_ending);
        rangeBarStarting = (RangeBar) findViewById(R.id.seek_bar_starting);
        rangeBarEnding = (RangeBar) findViewById(R.id.seek_bar_ending);
        tvStarting = (TextView) findViewById(R.id.text_starting);
        tvEnding = (TextView) findViewById(R.id.text_ending);
        btnSearchDetailCancel = (Button) findViewById(R.id.search_detail_cancel);
        btnSearchDetailDone = (Button) findViewById(R.id.search_detail_done);

        // This is a hack!!! Beware new Google Play Service update
        myLocationBtn = map.findViewById(2);
    }

    public static class SearchDetail {
        Date startDate;
        Date endDate;
        String locationText;
        LatLng latLng;

        public SearchDetail(Date startDate, Date endDate, String locationText, LatLng latLng) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.locationText = locationText;
            this.latLng = latLng;
        }
    }

    private static class TimeWrapper {
        int year;
        int month;
        int day;

        public TimeWrapper(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }

    private static class AddressWrapper {
        Address address;
        String text;
    }
}
