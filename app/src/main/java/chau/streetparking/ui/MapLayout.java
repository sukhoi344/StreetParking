package chau.streetparking.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.appyvet.rangebar.RangeBar;

import chau.streetparking.R;
import chau.streetparking.ui.curtain.CurtainView;
import chau.streetparking.ui.curtain.ICurtainViewBase;

/**
 * Created by Chau Thai on 6/9/2015.
 */
public class MapLayout extends FrameLayout {
    // Widgets
    private ViewGroup requestAddLayout;
    private CurtainView curtainView;
    private RangeBar seekBar;

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

    public void setSeekBarListener(RangeBar.OnRangeBarChangeListener listener) {
        if (seekBar != null && listener != null) {
            seekBar.setOnRangeBarChangeListener(listener);
        }
    }

    /**
     * Change UI when the user selects "REQUEST" button
     */
    public void showNext() {
        hideRequestAdd();
        showScheduleCancel();
    }

    /**
     * Change UI when the user selects "CANCEL" button
     */
    public void cancel() {
        hideScheduleCancel();
        showRequestAdd();
    }

    private void showRequestAdd() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        requestAddLayout.startAnimation(animation);
        requestAddLayout.setVisibility(View.VISIBLE);
    }

    private void hideRequestAdd() {
//        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
//        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
//        requestAddLayout.startAnimation(animation);
        requestAddLayout.setVisibility(View.GONE);
    }

    private void showScheduleCancel() {
//        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
//        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
//        curtainView.startAnimation(animation);
        curtainView.setVisibility(View.VISIBLE);
//        curtainView.setCurtainStatus(ICurtainViewBase.CurtainStatus.OPENED);

    }

    private void hideScheduleCancel() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        curtainView.startAnimation(animation);
        curtainView.setVisibility(View.GONE);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();
        seekBar.setSeekPinByValue(100);
    }

    private void getWidgets() {
        requestAddLayout = (ViewGroup) findViewById(R.id.request_add_layout);
        curtainView = (CurtainView) findViewById(R.id.curtain_view);
        seekBar = (RangeBar) findViewById(R.id.seek_bar);
    }
}
