package chau.streetparking.ui.map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.SupportMapFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import chau.streetparking.R;
import chau.streetparking.ui.curtain.CurtainView;
import chau.streetparking.ui.curtain.ICurtainViewBase;
import chau.streetparking.ui.picker.DurationPickerDialog;
import chau.streetparking.util.DateUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/9/2015.
 */
public class MapLayout extends FrameLayout implements TimePickerDialog.OnTimeSetListener,
                                                    DatePickerDialog.OnDateSetListener,
                                                    DurationPickerDialog.OnDurationSetListener {
    private static final String TAG_FROM = "from";
    public static final int SEEK_BAR_DEFAULT_VALUE_IN_FEET = 300;

    private static final int DEFAULT_DURATION_IN_HOUR = 1;

    // The map
    private View map;
    private View mapContainer;
    private View myLocationBtn;

    // Widgets
    private ViewGroup   locationLayout;

    // Find parking spot layout
    private RangeBar    seekBar;
    private TextView    tvFrom, tvDuration;
    private TextView    tvLocation;
    private TextView    tvRadius;
    private OnDurationSetListener onDurationSetListener;
    private OnStartDateSetListener onStartDateSetListener;

    private String selectedTime;
    private Date   requestStartDate;
    private String currentTag = TAG_FROM;

    public interface OnLayoutMoved {
        /**
         * @param ratio 0 means the layout hasn't been changed, 1 means changed entirely
         */
        void onLayoutMoved(double ratio);
    }

    public interface OnDurationSetListener {
        void onDurationSet(String duration);
    }

    public interface OnStartDateSetListener {
        void onStartDateSet(Date startDate);
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

        try {
            Activity activity = (Activity) getContext();
            timePickerDialog.show(activity.getFragmentManager(), picker.getTag());
        } catch (Exception ignore) {}

        monthOfYear++;
        selectedTime = "" + ((monthOfYear < 10)? ("0" + monthOfYear): monthOfYear) + "/"
                + ((dayOfMonth < 10)? "0" + dayOfMonth : dayOfMonth) + "/" + year;
    }

    @Override
    public void onTimeSet(RadialPickerLayout picker, int hourOfDay, int minute) {
        selectedTime += " " + (hourOfDay < 10? "0" + hourOfDay : hourOfDay) + ":"
                + (minute < 10? "0" + minute : minute);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = null;

        try {
            date = dateFormat.parse(selectedTime);
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        if (currentTag == TAG_FROM) {
            tvFrom.setText(selectedTime);
            requestStartDate = date;
        }

        if (onStartDateSetListener != null) {
            onStartDateSetListener.onStartDateSet(requestStartDate);
        }

//        if (!tvDuration.getText().toString().equals("Tap to select")
//                && !tvFrom.getText().toString().equals("Tap to select")) {
//            btnFind.setEnabled(true);
//        }
    }

    @Override
    public void onDurationSet(int duration, int durationType, String text) {
        tvDuration.setText(text);

        if (onDurationSetListener != null) {
            onDurationSetListener.onDurationSet(text);
        }

//        if (!tvDuration.getText().toString().equals("Tap to select")
//                && !tvFrom.getText().toString().equals("Tap to select")) {
//            btnFind.setEnabled(true);
//        }
    }

    public void setOnDurationSetListener(OnDurationSetListener onDurationSetListener) {
        this.onDurationSetListener = onDurationSetListener;
    }

    public void setOnStartDateSetListener(OnStartDateSetListener onStartDateSetListener) {
        this.onStartDateSetListener = onStartDateSetListener;
    }

    /**
     * Set seek bar listener
     */
    public void setSeekBarListener(RangeBar.OnRangeBarChangeListener listener) {
        if (seekBar != null && listener != null) {
            seekBar.setOnRangeBarChangeListener(listener);
        }
    }

    public void setTextRadius(String text) {
        tvRadius.setText(text);
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

    public Date getRequestStartDate() {
        return requestStartDate;
    }

    public String getDuration() {
        return tvDuration.getText().toString();
    }

    public View getMapContainer() {
        return mapContainer;
    }


    private void setCurrentTimeForPickers() {
        tvDuration.setText(DEFAULT_DURATION_IN_HOUR + " HOUR");

        Date date = new Date();
        tvFrom.setText(DateUtil.getStringFromDate(date, DateUtil.DATE_STRING_FORMAT_1));
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();

        seekBar.setSeekPinByValue(SEEK_BAR_DEFAULT_VALUE_IN_FEET);

        tvFrom.setOnClickListener(new TimeTextViewListener(TAG_FROM));
        tvDuration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DurationPickerDialog dialog = DurationPickerDialog.newInstance(2,
                            DurationPickerDialog.DurationType.HOUR);
                    dialog.setDurationSetListener(MapLayout.this);
                    Activity activity = (Activity) getContext();
                    dialog.show(activity.getFragmentManager(), "duration");
                } catch (Exception e) {
                    Logger.printStackTrace(e);
                }
            }
        });

        setCurrentTimeForPickers();
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

        seekBar = (RangeBar) findViewById(R.id.seek_bar);
        tvRadius = (TextView) findViewById(R.id.tv_radius);
        tvFrom = (TextView) findViewById(R.id.from);
        tvDuration = (TextView) findViewById(R.id.duration);
        tvLocation = (TextView) findViewById(R.id.tv_location);

        SupportMapFragment supportMapFragment = (SupportMapFragment) ((FragmentActivity) getContext())
                .getSupportFragmentManager().findFragmentById(R.id.map);
        map = supportMapFragment.getView();

        // This is a hack!!! Beware new Google Play Service update
        myLocationBtn = map.findViewById(2);
    }
}
