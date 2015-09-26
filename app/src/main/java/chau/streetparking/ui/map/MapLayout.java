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
    public static final int LAYOUT_FIRST_MAIN = 1;
    public static final int LAYOUT_FIND_PARKING_SPOTS = 2;

    private static final String TAG_FROM = "from";
    public static final int SEEK_BAR_DEFAULT_VALUE_IN_FEET = 300;

    private static final int DEFAULT_DURATION_IN_HOUR = 1;

    // The map
    private View map;
    private View mapContainer;
    private View myLocationBtn;
    private int mapHeight;      // Map height without removing the bottom curtain view
    private int actionBarHeight;

    // Widgets
    private ViewGroup   crossLayout;
    private ViewGroup   locationLayout;
    private ViewGroup   firstLayout;

    // First layout (only has 2 buttons: "Find Parking Spot" and "My Requests"
//    private Button          btnFindParkingSpots;
//    private Button          btnMyRequests;
//    private OnClickListener btnFindParkingListener;
//    private OnClickListener btnMyRequestListener;

    // Find parking spot layout
//    private CurtainView curtainViewFindParking;
    private View        findParkingSpotLayout;
    private RangeBar    seekBar;
    private TextView    tvFrom, tvDuration;
//    private Button      btnFind;
    private TextView    tvLocation;
    private TextView    tvRadius;
    private OnDurationSetListener onDurationSetListener;
    private OnStartDateSetListener onStartDateSetListener;

    // My requests layout widgets
//    private CurtainView                 curtainViewMyRequests;
//    private OnLayoutChangeListener      onLayoutChangeListener;
//    private OnLayoutMoved               onLayoutMoved;

    // Offer layout 1 widgets
//    private RecyclerView    recyclerViewRequest;
//    private ProgressBar     progressBar;
//    private Button          btnCancelMyRequest;
//    private OnClickListener onClickCancelMyRequest;

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
     * Change UI when the user selects "REQUEST" button
     */
    private void showFindParkingSpot() {
        reset();

        firstLayout.setVisibility(View.INVISIBLE);
//        curtainViewFindParking.setVisibility(View.VISIBLE);
        crossLayout.setVisibility(View.VISIBLE);

        showLocationLayout();

//        if (curtainViewFindParking.getCurtainStatus() == ICurtainViewBase.CurtainStatus.CLOSED) {
//            curtainViewFindParking.toggleStatus();
//        }
    }

//    /**
//     * Change UI when the user selects "My Requests" button
//     */
//    private void showMyRequests() {
//        reset();
//        curtainViewMyRequests.addOnLayoutChangeListener(onLayoutChangeListener);
//
//        firstLayout.setVisibility(View.INVISIBLE);
//        curtainViewMyRequests.setVisibility(View.VISIBLE);
//
//        if (curtainViewMyRequests.getCurtainStatus() == ICurtainViewBase.CurtainStatus.CLOSED) {
//            curtainViewMyRequests.toggleStatus();
//        }
//    }

    /**
     * Change UI when the user selects "Cancel" in the FindParkingSpot layout
     */
    public void cancelFindParkingSpot() {
//        if (curtainViewFindParking.getCurtainStatus() == ICurtainViewBase.CurtainStatus.OPENED) {
//            curtainViewFindParking.toggleStatus();
//            curtainViewFindParking.setAutoScrollingListener(new ICurtainViewBase.AutoScrollingListener() {
//                @Override
//                public void onScrolling(int currValue, int currVelocity, int startValue, int finalValue) {
//                }
//
//                @Override
//                public void onScrollFinished() {
//                    curtainViewFindParking.setVisibility(View.INVISIBLE);
//                    firstLayout.setVisibility(View.VISIBLE);
//                    curtainViewFindParking.setAutoScrollingListener(null);
//                }
//            });
//        } else {
//            curtainViewFindParking.setVisibility(View.INVISIBLE);
//            firstLayout.setVisibility(View.VISIBLE);
//        }
//
//        crossLayout.setVisibility(View.INVISIBLE);
//
//        hideLocationLayout();
    }

//    /**
//     * Change UI when the user selects "Cancel" button while in MyRequests layout
//     */
//    public void cancelMyRequests() {
//        if (curtainViewMyRequests.getCurtainStatus() == ICurtainViewBase.CurtainStatus.OPENED) {
//            curtainViewMyRequests.toggleStatus();
//            curtainViewMyRequests.setAutoScrollingListener(new ICurtainViewBase.AutoScrollingListener() {
//                @Override
//                public void onScrolling(int currValue, int currVelocity, int startValue, int finalValue) {
//                }
//
//                @Override
//                public void onScrollFinished() {
//                    curtainViewMyRequests.removeOnLayoutChangeListener(onLayoutChangeListener);
//                    curtainViewMyRequests.setVisibility(View.INVISIBLE);
//                    firstLayout.setVisibility(View.VISIBLE);
//                    curtainViewMyRequests.setAutoScrollingListener(null);
//                }
//            });
//        } else {
//            curtainViewMyRequests.setVisibility(View.INVISIBLE);
//            firstLayout.setVisibility(View.VISIBLE);
//        }
//    }

    /**
     * Close the curtain find parking layout
     */
    public void closeCurtainFindParking() {
//        if (curtainViewFindParking.getCurtainStatus() != ICurtainViewBase.CurtainStatus.CLOSED) {
//            curtainViewFindParking.toggleStatus();
//        }
    }

    /**
     * Set the text for the Parking Location layout
     * @param location
     */
    public void setLocationText(String location) {
        tvLocation.setText(location);
    }

    public int getCurrentLayout() {
        return firstLayout.getVisibility() == View.VISIBLE? LAYOUT_FIRST_MAIN : LAYOUT_FIND_PARKING_SPOTS;
    }

    public void setLocationLayoutOnClick(OnClickListener onClickListener) {
        if (locationLayout != null && onClickListener != null) {
            locationLayout.setOnClickListener(onClickListener);
        }
    }

    public void setBtnFindParkingListener(OnClickListener onClickListener) {
//        btnFindParkingListener = onClickListener;
    }

    public void setBtnMyRequestListener(OnClickListener onClickListener) {
//        btnMyRequestListener = onClickListener;
    }

//    public void setBtnFindListener(OnClickListener onClickListener) {
//        if (btnFind != null)
//            btnFind.setOnClickListener(onClickListener);
//    }

    public void setCancelMyRequestOnClick(OnClickListener onClick) {
//        onClickCancelMyRequest = onClick;
    }

    public void setOnLayoutMoved(OnLayoutMoved onLayoutMoved) {
//        this.onLayoutMoved = onLayoutMoved;
    }

    public RecyclerView getRecyclerViewRequest() {
//        return recyclerViewRequest;
        return null;
    }

    public void showProgressBarRequest() {
//        progressBar.setVisibility(View.VISIBLE);
//        recyclerViewRequest.setVisibility(View.INVISIBLE);
//        findViewById(R.id.text_view_no_result).setVisibility(View.INVISIBLE);
    }

    public void hideProgressBarRequest() {
//        progressBar.setVisibility(View.INVISIBLE);
//        recyclerViewRequest.setVisibility(View.VISIBLE);
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

    private void hideLocationLayout() {
        locationLayout.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up);
        locationLayout.startAnimation(animation);
    }

    private void showLocationLayout() {
        locationLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
        locationLayout.startAnimation(animation);
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

        // Set height and margin of the map container
//        mapHeight = ImageUtil.getActionBarHeight(getContext()) - findSpotLayoutHeight;
//        actionBarHeight = ImageUtil.getActionBarHeight(getContext());
//        mapHeight = ImageUtil.getAppScreenHeight(getContext()) - actionBarHeight;  // this is tricky (hack)

//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mapContainer.getLayoutParams();
//        params.height = mapHeight - context.getResources().getDimensionPixelSize(R.dimen.curtain_view_fixed);
//        params.setMargins(0, actionBarHeight, 0, 0);
//        params.height = findSpotLayoutHeight;
//        mapContainer.setLayoutParams(params);

//        btnMyRequests.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showMyRequests();
//                if (btnMyRequestListener != null)
//                    btnMyRequestListener.onClick(v);
//            }
//        });

//        btnFindParkingSpots.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFindParkingSpot();
//                if (btnFindParkingListener != null)
//                    btnFindParkingListener.onClick(v);
//            }
//        });

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

//        btnCancelMyRequest.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancelMyRequests();
//                if (onClickCancelMyRequest != null)
//                    onClickCancelMyRequest.onClick(v);
//            }
//        });

//        onLayoutChangeListener = getCurtainViewOfferListener();

        // Show the current time and default parking duration on
        // "Stat Time" and "Duration"
        setCurrentTimeForPickers();
    }

    private OnLayoutChangeListener getCurtainViewOfferListener() {
        final int curtainViewHeight = getResources().getDimensionPixelOffset(R.dimen.curtain_view_offer_height);
        final int curtainViewFixedHeight = getResources()
                .getDimensionPixelOffset(R.dimen.find_parking_spot_layout_height);
        final int withoutFixedHeight = curtainViewHeight - curtainViewFixedHeight;

        return new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                if (top == oldTop)
                    return;

                int diff = mapHeight - top;
                int topMargin =  -(int) (diff * 0.5) + actionBarHeight;

                ViewGroup.MarginLayoutParams params = (MarginLayoutParams) mapContainer.getLayoutParams();
                params.setMargins(params.leftMargin, topMargin, params.rightMargin, params.topMargin);
                mapContainer.setLayoutParams(params);

//                if (onLayoutMoved != null) {
//                    double ratio = (double) diff / withoutFixedHeight;
//                    onLayoutMoved.onLayoutMoved(ratio);
//                }
            }
        };
    }

    private void openCurtainMyRequests() {
//        if (curtainViewMyRequests.getCurtainStatus() != ICurtainViewBase.CurtainStatus.OPENED) {
//            curtainViewMyRequests.toggleStatus();
//        }
    }

    private void reset() {
        selectedTime = "";
        seekBar.setSeekPinByValue(SEEK_BAR_DEFAULT_VALUE_IN_FEET);
        tvFrom.setText("Tap to select");
        tvDuration.setText("Tap to select");
//        btnFind.setEnabled(false);
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
        mapContainer = findViewById(R.id.map_container);
        crossLayout = (ViewGroup) findViewById(R.id.cross_view);
        locationLayout = (ViewGroup) findViewById(R.id.location_layout);
        firstLayout = (ViewGroup) findViewById(R.id.first_layout);

//        curtainViewFindParking = (CurtainView) findViewById(R.id.curtain_view);
        findParkingSpotLayout = findViewById(R.id.find_parking_spot_layout);
        seekBar = (RangeBar) findViewById(R.id.seek_bar);
        tvRadius = (TextView) findViewById(R.id.tv_radius);
        tvFrom = (TextView) findViewById(R.id.from);
        tvDuration = (TextView) findViewById(R.id.duration);
//        btnFind = (Button) findViewById(R.id.btn_find);
        tvLocation = (TextView) findViewById(R.id.tv_location);

//        btnFindParkingSpots = (Button) findViewById(R.id.btn_find_parking_spots);
//        btnMyRequests = (Button) findViewById(R.id.btn_my_requests);
//
//        curtainViewMyRequests = (CurtainView) findViewById(R.id.curtain_view_my_requests);
//        recyclerViewRequest = (RecyclerView) findViewById(R.id.recycler_view_request);
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar_offer_1);
//        btnCancelMyRequest = (Button) findViewById(R.id.btn_cancel_my_requests);

        SupportMapFragment supportMapFragment = (SupportMapFragment) ((FragmentActivity) getContext())
                .getSupportFragmentManager().findFragmentById(R.id.map);
        map = supportMapFragment.getView();

        // This is a hack!!! Beware new Google Play Service update
        myLocationBtn = map.findViewById(2);
    }
}
