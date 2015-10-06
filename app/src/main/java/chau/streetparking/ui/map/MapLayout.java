package chau.streetparking.ui.map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.SupportMapFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import adik.fabtransitions.RevealToolbar;
import chau.streetparking.R;
import chau.streetparking.ui.picker.DurationPickerDialog;
import chau.streetparking.util.DateUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/9/2015.
 */
public class MapLayout extends FrameLayout implements TimePickerDialog.OnTimeSetListener,
                                                    DatePickerDialog.OnDateSetListener {
    private static final String TAG_FROM = "from";
    private static final String TAG_TO = "to";
    public static final int SEEK_BAR_DEFAULT_VALUE_IN_FEET = 300;

    private static final int DEFAULT_DURATION_IN_HOUR = 1;

    // The map
    private View map;
    private View mapContainer;
    private View myLocationBtn;

    // Widgets
    private ViewGroup   locationLayout;

    // Search Detail panel
    private FloatingActionButton    fab;
    private View                    revealView;
    private RevealToolbar           revealToolbar;
    private Button                  btnCalendarStarting;
    private Button                  btnCalendarEnding;
    private RangeBar                rangeBarStarting;
    private RangeBar                rangeBarEnding;
    private TextView                tvStarting;
    private TextView                tvEnding;
    private Button                  btnSearchDetailCancel;
    private Button                  btnSearchDetailDone;

    // Find parking spot layout
//    private RangeBar    seekBar;
//    private TextView    tvFrom, tvDuration;
    private TextView    tvLocation;
//    private TextView    tvRadius;
//    private OnStartDateSetListener onStartDateSetListener;

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
        } else {
            String timeString = getDateStringScheduled((int) minDiff);
            timeString += " (" + getHoursStringAhead((int) minDiff) + ")";
            tvEnding.setText(timeString);
        }
    }

//    public void setOnDurationSetListener(OnDurationSetListener onDurationSetListener) {
//        this.onDurationSetListener = onDurationSetListener;
//    }
//
//    public void setOnStartDateSetListener(OnStartDateSetListener onStartDateSetListener) {
//        this.onStartDateSetListener = onStartDateSetListener;
//    }

//    /**
//     * Set seek bar listener
//     */
//    public void setSeekBarListener(RangeBar.OnRangeBarChangeListener listener) {
//        if (seekBar != null && listener != null) {
//            seekBar.setOnRangeBarChangeListener(listener);
//        }
//    }

//    public void setTextRadius(String text) {
//        tvRadius.setText(text);
//    }

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

//    public Date getRequestStartDate() {
//        return requestStartDate;
//    }

//    public String getDuration() {
//        return tvDuration.getText().toString();
//    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public View getMapContainer() {
        return mapContainer;
    }


//    private void setCurrentTimeForPickers() {
//        tvDuration.setText(DEFAULT_DURATION_IN_HOUR + " HOUR");
//
//        Date date = new Date();
//        tvFrom.setText(DateUtil.getStringFromDate(date, DateUtil.DATE_STRING_FORMAT_1));
//    }

    private void setupSearchDetail() {
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                revealToolbar.Reveal(revealView);
                setTimeSearchDetail();
            }
        });

        btnSearchDetailCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                revealToolbar.HideReveal(revealView);
            }
        });

        btnSearchDetailDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                revealToolbar.HideReveal(revealView);
            }
        });

        btnCalendarStarting.setOnClickListener(new TimeTextViewListener(TAG_FROM));
        btnCalendarEnding.setOnClickListener(new TimeTextViewListener(TAG_TO));

        setTimeSearchDetail();
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
                int value = Integer.parseInt(rightPinValue);
                int minutes = (int) (value / 2.0f * 60);

                tvStarting.setText(getDateStringScheduled(minutes));
            }
        });

        rangeBarEnding.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                int value = Integer.parseInt(rightPinValue);
                int minutes = (int) (value / 2.0f * 60);

                String timeString = getDateStringScheduled(minutes);
                timeString += " (" + getHoursStringAhead(minutes) + ")";

                tvEnding.setText(timeString);
            }
        });

        String endingString = getDateStringScheduled(60 * 3) + " (" + getHoursStringAhead(60 * 3) + ")";

        tvStarting.setText(getDateStringScheduled(0));
        tvEnding.setText(endingString);
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
        Calendar currentCalendar = Calendar.getInstance();
        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.add(Calendar.MINUTE, minutes);

        long minDiff = (scheduledCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis()) /
                (1000 * 60);
        double hourDiff = minDiff / 60.0;
        String hourText = String.format("%.1f", hourDiff);

        return hourText + " " + (hourDiff <= 1? "hour" : "hours");
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();

        setupSearchDetail();
        setStartEndDates();

//        seekBar.setSeekPinByValue(SEEK_BAR_DEFAULT_VALUE_IN_FEET);

//        tvFrom.setOnClickListener(new TimeTextViewListener(TAG_FROM));
//        tvDuration.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    DurationPickerDialog dialog = DurationPickerDialog.newInstance(2,
//                            DurationPickerDialog.DurationType.HOUR);
//                    dialog.setDurationSetListener(MapLayout.this);
//                    Activity activity = (Activity) getContext();
//                    dialog.show(activity.getFragmentManager(), "duration");
//                } catch (Exception e) {
//                    Logger.printStackTrace(e);
//                }
//            }
//        });
//
//        setCurrentTimeForPickers();
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

//        seekBar = (RangeBar) findViewById(R.id.seek_bar);
//        tvRadius = (TextView) findViewById(R.id.tv_radius);
//        tvFrom = (TextView) findViewById(R.id.from);
//        tvDuration = (TextView) findViewById(R.id.duration);
        tvLocation = (TextView) findViewById(R.id.tv_location);

        SupportMapFragment supportMapFragment = (SupportMapFragment) ((FragmentActivity) getContext())
                .getSupportFragmentManager().findFragmentById(R.id.map);
        map = supportMapFragment.getView();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        revealView = findViewById(R.id.reveal);
        revealToolbar = new RevealToolbar((Activity) getContext(), revealView, fab);
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
}
