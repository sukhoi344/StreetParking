package chau.streetparking.ui.map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.SupportMapFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

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

    // The map
    private View map;
    private View mapContainer;
    private View myLocationBtn;
    private int mapHeight;      // Map height without removing the bottom curtain view
    private int actionBarHeight;

    // Widgets
    private ViewGroup   setOfferLocationLayout;
    private ViewGroup   crossLayout;
    private ViewGroup   locationLayout;
    private ViewGroup   requestAddLayout;

    // Request layout
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
    private OnLayoutMoved               onLayoutMoved;

    // Offer layout 1 widgets
    private View            offerLayout1;
    private RecyclerView    recyclerViewRequest;
    private ProgressBar     progressBar;
    private Button          btnCancelOffer1;
    private OnClickListener onClickCancelOffer1;

    // Offer layout 2 widgets
    private View        offerLayout2;
    private Button      btnBackOffer2;
//    private Button      btnNextOffer2;
    private TextView    tvRequestName;
    private ImageView   ivRequestAvatar;
    private TextView    tvRequestLocation;
    private TextView    tvRequestRange;
    private TextView    tvRequestStart;
    private TextView    tvRequestEnd;
    private OnClickListener onClickBackOffer2;
    private OnClickListener onClickNextOffer2;

    // Offer layout 3 widgets
    private ViewGroup       offerLayout3;
    private Button          btnBackOffer3;
    private Button          btnOffer3;
    private Button          btnAddPhotos;
    private EditText        etPriceOffer3;
    private Spinner         spinnerOffer3;
    private RecyclerView    recyclerViewPhotos;
    private OnClickListener onClickSetOfferLocation;
    private OnClickListener onClickBackOffer3;
    private OnClickListener onClickAddPhotos;

    private String selectedTime;
    private String currentTag = TAG_FROM;

    public interface OnRequestSelectedListener {
        void onRequestSelected(Request request);
    }

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
        crossLayout.setVisibility(View.VISIBLE);

        showLocationLayout();

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

        crossLayout.setVisibility(View.INVISIBLE);

        hideLocationLayout();
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
        showLocationLayout();

        offerLayout1.setVisibility(View.INVISIBLE);
        offerLayout2.setVisibility(View.VISIBLE);

        setOfferLocationLayout.setVisibility(View.VISIBLE);
        crossLayout.setVisibility(View.VISIBLE);

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

    public void setCancelOffer1OnClick(OnClickListener onClick) {
        onClickCancelOffer1 = onClick;
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

    public void setOnLayoutMoved(OnLayoutMoved onLayoutMoved) {
        this.onLayoutMoved = onLayoutMoved;
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

    public void setSetOfferLocationListener(OnClickListener onClick) {
        onClickSetOfferLocation = onClick;
    }

    public void setBackOffer3Listener(OnClickListener onClick) {
        onClickBackOffer3 = onClick;
    }

    public void setAddPhotosListener(OnClickListener onClick) {
        onClickAddPhotos = onClick;
    }

    public RecyclerView getRecyclerViewPhotos() {
        return recyclerViewPhotos;
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

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();

        // Set height and margin of the map container
        actionBarHeight = ImageUtil.getActionBarHeight(getContext());
        mapHeight = ImageUtil.getAppScreenHeight(getContext()) - actionBarHeight;  // this is tricky (hack)

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mapContainer.getLayoutParams();
        params.height = mapHeight - context.getResources().getDimensionPixelSize(R.dimen.curtain_view_fixed);
        params.setMargins(0, actionBarHeight, 0, 0);
        mapContainer.setLayoutParams(params);

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
                if (onClickCancelOffer1 != null)
                    onClickCancelOffer1.onClick(v);
            }
        });

        btnBackOffer2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLocationLayout();
                openCurtainViewOffer();

                offerLayout2.setVisibility(View.INVISIBLE);
                offerLayout1.setVisibility(View.VISIBLE);
                setOfferLocationLayout.setVisibility(View.INVISIBLE);
                crossLayout.setVisibility(View.INVISIBLE);

                if (onClickBackOffer2 != null)
                    onClickBackOffer2.onClick(v);
            }
        });

        setOfferLocationLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offerLayout3.setVisibility(View.VISIBLE);
                offerLayout2.setVisibility(View.INVISIBLE);
                setOfferLocationLayout.setVisibility(View.INVISIBLE);
                locationLayout.setEnabled(false);

                if (onClickSetOfferLocation != null)
                    onClickSetOfferLocation.onClick(v);
            }
        });

        btnBackOffer3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offerLayout2.setVisibility(View.VISIBLE);
                offerLayout3.setVisibility(View.INVISIBLE);
                setOfferLocationLayout.setVisibility(View.VISIBLE);
                locationLayout.setEnabled(true);

                if (onClickBackOffer3 != null)
                    onClickBackOffer3.onClick(v);
            }
        });

        etPriceOffer3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    btnOffer3.setEnabled(false);
                } else {
                    btnOffer3.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setupSpinnerPrice();

        btnAddPhotos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickAddPhotos != null)
                    onClickAddPhotos.onClick(v);
            }
        });

        onLayoutChangeListener = getCurtainViewOfferListener();
    }

    private void setupSpinnerPrice() {
        if (spinnerOffer3 != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.price_type_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerOffer3.setAdapter(adapter);
            spinnerOffer3.setOnItemSelectedListener(null);
        }
    }

    private OnLayoutChangeListener getCurtainViewOfferListener() {
        final Wrapper wrapper = new Wrapper();
        wrapper.l = SystemClock.elapsedRealtime();
        final int curtainViewHeight = getResources().getDimensionPixelOffset(R.dimen.curtain_view_offer_height);
        final int curtainViewFixedHeight = getResources()
                .getDimensionPixelOffset(R.dimen.curtain_view_fixed);
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

                if (onLayoutMoved != null) {
                    double ratio = (double) diff / withoutFixedHeight;
                    onLayoutMoved.onLayoutMoved(ratio);
                }
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
        setOfferLocationLayout = (ViewGroup) findViewById(R.id.set_offer_location_layout);
        crossLayout = (ViewGroup) findViewById(R.id.cross_view);
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
//        btnNextOffer2 = (Button) findViewById(R.id.btn_offer_2_next);
        tvRequestName = (TextView) findViewById(R.id.request_name);
        ivRequestAvatar = (ImageView) findViewById(R.id.request_avatar);
        tvRequestLocation = (TextView) findViewById(R.id.request_location);
        tvRequestRange = (TextView) findViewById(R.id.request_range);
        tvRequestStart = (TextView) findViewById(R.id.request_start);
        tvRequestEnd = (TextView) findViewById(R.id.request_end);

        offerLayout3 = (ViewGroup) findViewById(R.id.offer_layout_3);
        btnBackOffer3  = (Button) findViewById(R.id.btn_offer_3_back);
        btnOffer3 = (Button) findViewById(R.id.btn_offer_3_offer);
        etPriceOffer3 = (EditText) findViewById(R.id.edit_text_price_offer_3);
        spinnerOffer3 = (Spinner) findViewById(R.id.spinner_offer_3);
        btnAddPhotos = (Button) findViewById(R.id.btn_add_photos);
        recyclerViewPhotos = (RecyclerView) findViewById(R.id.recycler_view_photos);

        SupportMapFragment supportMapFragment = (SupportMapFragment) ((FragmentActivity) getContext())
                .getSupportFragmentManager().findFragmentById(R.id.map);
        map = supportMapFragment.getView();

        // This is a hack!!! Beware new Google Play Service update
        myLocationBtn = map.findViewById(2);
    }
}
