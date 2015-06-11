package chau.streetparking.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 6/9/2015.
 */
public class MapLayout extends FrameLayout {
    // Widgets
    private LinearLayout requestAddLayout;
    private LinearLayout scheduleCancelLayout;
    private LinearLayout seekBarLayout;
    private VerticalSeekBar seekBar;
    private TextView rangeIndicator;

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

    public void setSeekBarListener(SeekBar.OnSeekBarChangeListener listener) {
        if (seekBar != null && listener != null) {
            seekBar.setOnSeekBarChangeListener(listener);
        }
    }

    public void showNext() {
        hideRequestAdd();
        showScheduleCancel();
        showSeekBar();
    }

    public void cancel() {
        hideScheduleCancel();
        hideSeekBar();
        showRequestAdd();
    }

    private void showRequestAdd() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        requestAddLayout.startAnimation(animation);
        requestAddLayout.setVisibility(View.VISIBLE);
    }

    private void hideRequestAdd() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        requestAddLayout.startAnimation(animation);
        requestAddLayout.setVisibility(View.GONE);
    }

    private void showScheduleCancel() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        scheduleCancelLayout.startAnimation(animation);
        scheduleCancelLayout.setVisibility(View.VISIBLE);
    }

    private void hideScheduleCancel() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        scheduleCancelLayout.startAnimation(animation);
        scheduleCancelLayout.setVisibility(View.GONE);
    }

    private void showSeekBar() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        seekBarLayout.startAnimation(animation);
        seekBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideSeekBar() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        animation.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        seekBarLayout.startAnimation(animation);
        seekBarLayout.setVisibility(View.GONE);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_map_layout, this, true);
        getWidgets();
    }

    private void getWidgets() {
        requestAddLayout = (LinearLayout) findViewById(R.id.request_add_layout);
        scheduleCancelLayout = (LinearLayout) findViewById(R.id.schedule_cancel_layout);
        seekBarLayout = (LinearLayout) findViewById(R.id.seek_bar_layout);
        seekBar = (VerticalSeekBar) findViewById(R.id.seek_bar);
        rangeIndicator = (TextView) findViewById(R.id.range_indicator);
    }
}
