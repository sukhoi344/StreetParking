package chau.streetparking.ui.map;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import chau.streetparking.R;

/**
 * View which displays locations suggestion.
 * Created by Chau Thai on 10/9/2015.
 */
public class LocationSuggestView extends FrameLayout {
    private RecyclerView recyclerView;

    private Animation fadeInAnimation;
    private Animation fadeOutAnimation;

    public LocationSuggestView(Context context) {
        super(context);
        init(context);
    }

    public LocationSuggestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LocationSuggestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public LocationSuggestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void show() {
        if (getVisibility() == INVISIBLE || getVisibility() == GONE) {
            startAnimation(fadeInAnimation);
        }
    }

    public void hide() {
        if (getVisibility() == VISIBLE) {
            startAnimation(fadeOutAnimation);
        }
    }

    public void setMargin(int bottom, int left, int right) {
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.setMargins(left, params.topMargin, right, bottom);
        setLayoutParams(params);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.location_suggest_view, this, true);
        setVisibility(INVISIBLE);

        initList();
        loadAnimations();
    }

    private void loadAnimations() {
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void initList() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_suggest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
}
