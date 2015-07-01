package chau.streetparking.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.SupportMapFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.Request;
import chau.streetparking.ui.curtain.CurtainView;
import chau.streetparking.ui.curtain.ICurtainViewBase;
import chau.streetparking.util.ImageUtil;

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
    private static final int LAYOUT_UPDATE_INTERVAL = 50;

    // The map
    private View map;
    private View mapContainer;
    private int mapHeight;
    private View myLocationBtn;

    // Widgets
    private ViewGroup   locationLayout;
    private ViewGroup   requestAddLayout;
    private CurtainView curtainViewRequest;
    private RangeBar    seekBar;
    private TextView    tvFrom, tvTo;
    private Button      btnSendRequest;
    private TextView    tvLocation;

    // First layout
    private Button          btnRequest, btnOffer;
    private OnClickListener btnRequestListener, btnOfferListener;

    // Offer layout widgets
    private CurtainView                 curtainViewOffer;
    private OnRequestSelectedListener   onRequestSelectedListener;
    private OnLayoutChangeListener      onLayoutChangeListener;

    // Offer layout 1 widgets
    private View            offerLayout1;
    private RecyclerView    recyclerViewRequest;
    private ProgressBar     progressBar;
    private Button          btnCancelOffer1;

    // Offer layout 2 widgets
    private View        offerLayout2;
    private Button      btnBackOffer2;
    private Button      btnNextOffer2;
    private TextView    tvRequestName;
    private ImageView   ivRequestAvatar;
    private TextView    tvRequestLocation;
    private TextView    tvRequestRange;
    private TextView    tvRequestStart;
    private TextView    tvRequestEnd;
    private OnClickListener onClickBackOffer2;
    private OnClickListener onClickNextOffer2;

    private String selectedTime;
    private String currentTag = TAG_FROM;

    public interface OnRequestSelectedListener {
        void onRequestSelected(Request request);
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
    private void showRequest() {
        reset();

        requestAddLayout.setVisibility(View.INVISIBLE);
        curtainViewRequest.setVisibility(View.VISIBLE);

        locationLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
        locationLayout.startAnimation(animation);

        if (curtainViewRequest.getCurtainStatus() == ICurtainViewBase.CurtainStatus.CLOSED) {
            curtainViewRequest.toggleStatus();
        }
    }

    /**
     * Change UI when the user selects "OFFER" button
     */
    private void showOffer() {
        reset();
        curtainViewOffer.addOnLayoutChangeListener(onLayoutChangeListener);

        requestAddLayout.setVisibility(View.INVISIBLE);
        curtainViewOffer.setVisibility(View.VISIBLE);

        if (curtainViewOffer.getCurtainStatus() == ICurtainViewBase.CurtainStatus.CLOSED) {
            curtainViewOffer.toggleStatus();
        }
    }

    /**
     * Change UI when the user selects "CANCEL" button while in Request mode
     */
    public void cancelRequest() {
        if (curtainViewRequest.getCurtainStatus() == ICurtainViewBase.CurtainStatus.OPENED) {
            curtainViewRequest.toggleStatus();
            curtainViewRequest.setAutoScrollingListener(new ICurtainViewBase.AutoScrollingListener() {
                @Override
                public void onScrolling(int currValue, int currVelocity, int startValue, int finalValue) {
                }

                @Override
                public void onScrollFinished() {
                    curtainViewRequest.setVisibility(View.INVISIBLE);
                    requestAddLayout.setVisibility(View.VISIBLE);
                    curtainViewRequest.setAutoScrollingListener(null);
                }
            });
        } else {
            curtainViewRequest.setVisibility(View.INVISIBLE);
            requestAddLayout.setVisibility(View.VISIBLE);
        }

        locationLayout.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up);
        locationLayout.startAnimation(animation);
    }

    /**
     * Change UI when the user selects "CANCEL" button while in Offer mode
     */
    public void cancelOffer() {
        if (curtainViewOffer.getCurtainStatus() == ICurtainViewBase.CurtainStatus.OPENED) {
            curtainViewOffer.toggleStatus();
            curtainViewOffer.setAutoScrollingListener(new ICurtainViewBase.AutoScrollingListener() {
                @Override
                public void onScrolling(int currValue, int currVelocity, int startValue, int finalValue) {
                }

                @Override
                public void onScrollFinished() {
                    curtainViewOffer.removeOnLayoutChangeListener(onLayoutChangeListener);
                    curtainViewOffer.setVisibility(View.INVISIBLE);
                    requestAddLayout.setVisibility(View.VISIBLE);
                    curtainViewOffer.setAutoScrollingListener(null);
                }
            });
        } else {
            curtainViewOffer.setVisibility(View.INVISIBLE);
            requestAddLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Close the curtain request layout, which contains the settings for parking request
     */
    public void closeCurtainRequest() {
        if (curtainViewRequest.getCurtainStatus() != ICurtainViewBase.CurtainStatus.CLOSED) {
            curtainViewRequest.toggleStatus();
        }
    }

    /**
     * Show the selected request in the request list
     * @param request
     */
    public void showSelectedRequest(Request request) {
        offerLayout1.setVisibility(View.INVISIBLE);
        offerLayout2.setVisibility(View.VISIBLE);

        tvRequestName.setText(request.getName());
        if (request.getAddress().getMaxAddressLineIndex() > 0)
            tvRequestLocation.setText(request.getAddress().getAddressLine(0));
        tvRequestRange.setText(request.getRadius() + " ft");
        tvRequestStart.setText(request.getFrom());
        tvRequestEnd.setText(request.getTo());

        onRequestSelectedListener.onRequestSelected(request);
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

    public void setLocationLayoutOnClick(OnClickListener onClickListener) {
        if (locationLayout != null && onClickListener != null) {
            locationLayout.setOnClickListener(onClickListener);
        }
    }

    public void setBtnRequestListener(OnClickListener onClickListener) {
        btnRequestListener = onClickListener;
    }

    public void setBtnOfferListener(OnClickListener onClickListener) {
        btnOfferListener = onClickListener;
    }

    public void setBackOffer2OnClick(OnClickListener onClick) {
        onClickBackOffer2 = onClick;
    }

    public void setNextOffer2OnClick(OnClickListener onClick) {
        onClickNextOffer2 = onClick;
    }

    public void setOnRequestSelectedListener(OnRequestSelectedListener onRequestSelectedListener) {
        this.onRequestSelectedListener = onRequestSelectedListener;
    }

    public RecyclerView getRecyclerViewRequest() {
        return recyclerViewRequest;
    }

    public void showProgressBarRequest() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewRequest.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBarRequest() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerViewRequest.setVisibility(View.VISIBLE);
    }

    public void setMyLocationBtnMargin(int top) {
        if (myLocationBtn != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myLocationBtn.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, top, layoutParams.rightMargin, layoutParams.bottomMargin);
            myLocationBtn.setLayoutParams(layoutParams);
        }
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();

        mapHeight = ImageUtil.getAppScreenHeight(getContext()) -
                ImageUtil.getActionBarHeight(getContext());

        btnOffer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showOffer();
                if (btnOfferListener != null)
                    btnOfferListener.onClick(v);
            }
        });

        btnRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequest();
                if (btnRequestListener != null)
                    btnRequestListener.onClick(v);
            }
        });

        seekBar.setSeekPinByValue(SEEK_BAR_DEFAULT_VALUE);

        tvFrom.setOnClickListener(new TimeTextViewListener(TAG_FROM));
        tvTo.setOnClickListener(new TimeTextViewListener(TAG_TO));

        btnCancelOffer1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOffer();
            }
        });

        btnBackOffer2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openCurtainViewOffer();

                offerLayout2.setVisibility(View.INVISIBLE);
                offerLayout1.setVisibility(View.VISIBLE);

                if (onClickBackOffer2 != null)
                    onClickBackOffer2.onClick(v);
            }
        });

        btnNextOffer2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do sth

                if (onClickNextOffer2 != null)
                    onClickNextOffer2.onClick(v);
            }
        });

        onLayoutChangeListener = getCurtainViewOfferListener();
    }

    private OnLayoutChangeListener getCurtainViewOfferListener() {
        final Wrapper wrapper = new Wrapper();
        wrapper.l = SystemClock.elapsedRealtime();

        return new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                if (top == oldTop)
                    return;

                long t2 = SystemClock.elapsedRealtime();
                if (t2 - wrapper.l < LAYOUT_UPDATE_INTERVAL) {
                    return;
                }
                wrapper.l = t2;

                ViewGroup.LayoutParams params = mapContainer.getLayoutParams();
                params.height = mapHeight - (mapHeight - top);

                if (params.height > mapHeight)
                    params.height = mapHeight;
                mapContainer.setLayoutParams(params);
            }
        };
    }

    private class Wrapper {
        long l;
    }

    private void openCurtainViewOffer() {
        if (curtainViewOffer.getCurtainStatus() != ICurtainViewBase.CurtainStatus.OPENED) {
            curtainViewOffer.toggleStatus();
        }
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
        mapContainer = findViewById(R.id.map_container);
        locationLayout = (ViewGroup) findViewById(R.id.location_layout);
        requestAddLayout = (ViewGroup) findViewById(R.id.request_add_layout);
        curtainViewRequest = (CurtainView) findViewById(R.id.curtain_view);
        seekBar = (RangeBar) findViewById(R.id.seek_bar);
        tvFrom = (TextView) findViewById(R.id.from);
        tvTo = (TextView) findViewById(R.id.to);
        btnSendRequest = (Button) findViewById(R.id.btn_send_request);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        btnRequest = (Button) findViewById(R.id.btn_request);
        btnOffer = (Button) findViewById(R.id.btn_offer);

        curtainViewOffer = (CurtainView) findViewById(R.id.curtain_view_offer);

        offerLayout1 = findViewById(R.id.offer_layout_1);
        recyclerViewRequest = (RecyclerView) findViewById(R.id.recycler_view_request);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_offer_1);
        btnCancelOffer1 = (Button) findViewById(R.id.btn_cancel_offer_1);

        offerLayout2 = findViewById(R.id.offer_layout_2);
        btnBackOffer2 = (Button) findViewById(R.id.btn_offer_2_back);
        btnNextOffer2 = (Button) findViewById(R.id.btn_offer_2_next);
        tvRequestName = (TextView) findViewById(R.id.request_name);
        ivRequestAvatar = (ImageView) findViewById(R.id.request_avatar);
        tvRequestLocation = (TextView) findViewById(R.id.request_location);
        tvRequestRange = (TextView) findViewById(R.id.request_range);
        tvRequestStart = (TextView) findViewById(R.id.request_start);
        tvRequestEnd = (TextView) findViewById(R.id.request_end);

        SupportMapFragment supportMapFragment = (SupportMapFragment) ((FragmentActivity) getContext())
                .getSupportFragmentManager().findFragmentById(R.id.map);
        map = supportMapFragment.getView();

        // This is a hack!!! Beware new Google Play Service update
        myLocationBtn = map.findViewById(2);
    }
}
