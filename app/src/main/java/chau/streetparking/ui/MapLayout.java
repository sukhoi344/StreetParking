package chau.streetparking.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import chau.streetparking.R;
import chau.streetparking.ui.curtain.CurtainView;
import chau.streetparking.ui.curtain.ICurtainViewBase;

/**
 * Created by Chau Thai on 6/9/2015.
 */
public class MapLayout extends FrameLayout implements TimePickerDialog.OnTimeSetListener,
                                                    DatePickerDialog.OnDateSetListener {
    public static final int LAYOUT_REQUEST_ADD = 1;
    public static final int LAYOUT_SEND_CANCEL = 2;

    private static final String TAG_FROM = "from";
    private static final String TAG_TO = "to";
    private static final int SEEK_BAR_DEFAULT_VALUE = 300;

    // Widgets
    private ViewGroup locationLayout;
    private ViewGroup requestAddLayout;
    private CurtainView curtainView;
    private RangeBar seekBar;
    private TextView tvFrom, tvTo;
    private Button btnSendRequest;
    private TextView tvLocation;

    private String selectedTime;
    private String currentTag = TAG_FROM;

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

        selectedTime = "" + monthOfYear + "/" + (dayOfMonth + 1) + "/" + year;
    }

    @Override
    public void onTimeSet(RadialPickerLayout picker, int hourOfDay, int minute) {
        selectedTime += " " + hourOfDay + ":" + minute;
        if (currentTag == TAG_FROM) {
            tvFrom.setText(selectedTime);
        } else {
            tvTo.setText(selectedTime);
        }

        if (!tvTo.getText().toString().equals("Tap to select")
                && !tvFrom.getText().toString().equals("Tap to select")) {
            btnSendRequest.setEnabled(true);
        }
    }

    /**
     * Set seek bar listener
     */
    public void setSeekBarListener(RangeBar.OnRangeBarChangeListener listener) {
        if (seekBar != null && listener != null) {
            seekBar.setOnRangeBarChangeListener(listener);
        }
    }

    /**
     * Change UI when the user selects "REQUEST" button
     */
    public void showNext() {
        reset();

        requestAddLayout.setVisibility(View.INVISIBLE);
        curtainView.setVisibility(View.VISIBLE);

        locationLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
        locationLayout.startAnimation(animation);

        if (curtainView.getCurtainStatus() == ICurtainViewBase.CurtainStatus.CLOSED) {
            curtainView.toggleStatus();
        }
    }

    /**
     * Change UI when the user selects "CANCEL" button
     */
    public void cancel() {
        if (curtainView.getCurtainStatus() == ICurtainViewBase.CurtainStatus.OPENED) {
            curtainView.toggleStatus();
            curtainView.setAutoScrollingListener(new ICurtainViewBase.AutoScrollingListener() {
                @Override
                public void onScrolling(int currValue, int currVelocity, int startValue, int finalValue) {
                }

                @Override
                public void onScrollFinished() {
                    curtainView.setVisibility(View.INVISIBLE);
                    requestAddLayout.setVisibility(View.VISIBLE);
                    curtainView.setAutoScrollingListener(null);
                }
            });
        } else {
            curtainView.setVisibility(View.INVISIBLE);
            requestAddLayout.setVisibility(View.VISIBLE);
        }

        locationLayout.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up);
        locationLayout.startAnimation(animation);
    }

    /**
     * Close the curtain layout, which contains the settings for parking request
     */
    public void closeCurtain() {
        if (curtainView.getCurtainStatus() != ICurtainViewBase.CurtainStatus.CLOSED) {
            curtainView.toggleStatus();
        }
    }

    /**
     * Set the text for the Parking Location layout
     * @param location
     */
    public void setLocationText(String location) {
        tvLocation.setText(location);
    }

    public int getCurrentLayout() {
        return requestAddLayout.getVisibility() == View.VISIBLE? LAYOUT_REQUEST_ADD : LAYOUT_SEND_CANCEL;
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();
        seekBar.setSeekPinByValue(SEEK_BAR_DEFAULT_VALUE);

        tvFrom.setOnClickListener(new TimeTextViewListener(TAG_FROM));
        tvTo.setOnClickListener(new TimeTextViewListener(TAG_TO));
    }

    private void reset() {
        selectedTime = "";
        seekBar.setSeekPinByValue(SEEK_BAR_DEFAULT_VALUE);
        tvFrom.setText("Tap to select");
        tvTo.setText("Tap to select");
        btnSendRequest.setEnabled(false);
        tvLocation.setText("");
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
        locationLayout = (ViewGroup) findViewById(R.id.location_layout);
        requestAddLayout = (ViewGroup) findViewById(R.id.request_add_layout);
        curtainView = (CurtainView) findViewById(R.id.curtain_view);
        seekBar = (RangeBar) findViewById(R.id.seek_bar);
        tvFrom = (TextView) findViewById(R.id.from);
        tvTo = (TextView) findViewById(R.id.to);
        btnSendRequest = (Button) findViewById(R.id.btn_send_request);
        tvLocation = (TextView) findViewById(R.id.tv_location);
    }
}
