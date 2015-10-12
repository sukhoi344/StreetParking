package chau.streetparking.ui.map;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chau.streetparking.R;
import chau.streetparking.util.ImageUtil;
import chau.streetparking.util.Logger;

/**
 * View which displays locations suggestion.
 * Created by Chau Thai on 10/9/2015.
 */
public class LocationSuggestView extends FrameLayout {
    private static final int MAX_RESULTS = 7;

    private static final int HEIGHT_FOR_1_DP = 66;
    private static final int HEIGHT_FOR_2_DP = 115;
    private static final int HEIGHT_FOR_3_DP = 160;
    private static final int HEIGHT_FOR_MORE_THAN_3_DP = 210;

    private RecyclerView recyclerView;
    private TextView tvNoResult;

    private Animation fadeInAnimation;
    private Animation fadeOutAnimation;

    private Geocoder geocoder;
    private TaskGetAddress taskGetAddress;
    private OnSuggestSelectedListener onSuggestSelectedListener;

    private Context context;

    public interface OnSuggestSelectedListener {
        void onSelected(Address address);
    }

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

    public void setOnSuggestSelectedListener(OnSuggestSelectedListener listener) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            ((LocationSuggestAdapter) adapter).setOnSuggestSelectedListener(listener);
        }

        this.onSuggestSelectedListener = listener;
    }

    public void show() {
        if (getVisibility() == INVISIBLE || getVisibility() == GONE) {
            startAnimation(fadeInAnimation);
        }
    }

    public void hide() {
        cancelTask();

        if (getVisibility() == VISIBLE) {
            startAnimation(fadeOutAnimation);
        }
    }

    public void search(String text) {
        cancelTask(); // Cancel current task

        if (text == null || text.isEmpty()) {
            hide();
            recyclerView.swapAdapter(new LocationSuggestAdapter(context,
                    null, onSuggestSelectedListener), true);
        } else {
            taskGetAddress = new TaskGetAddress(text);
            taskGetAddress.execute();
        }
    }

    public void setMargin(int bottom, int left, int right) {
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.setMargins(left, params.topMargin, right, bottom);
        setLayoutParams(params);
    }

    private void cancelTask() {
        if (taskGetAddress != null && taskGetAddress.isWorking) {
            taskGetAddress.cancel(true);
            taskGetAddress = null;
        }
    }

    private void setHeight(List<Address> results) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();

        if (results == null || results.isEmpty()) {
            params.height = ImageUtil.getPixelFromDP(HEIGHT_FOR_1_DP);
        } else {
            int count = results.size();

            if (count == 1)
                params.height = ImageUtil.getPixelFromDP(HEIGHT_FOR_1_DP);
            else if (count == 2)
                params.height = ImageUtil.getPixelFromDP(HEIGHT_FOR_2_DP);
            else if (count == 3)
                params.height = ImageUtil.getPixelFromDP(HEIGHT_FOR_3_DP);
            else
                params.height = ImageUtil.getPixelFromDP(HEIGHT_FOR_MORE_THAN_3_DP);
        }

        params.gravity = Gravity.BOTTOM | Gravity.START;
        setLayoutParams(params);
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.location_suggest_view, this, true);
        setVisibility(View.INVISIBLE);

        getWidgets(view);
        initList();
        loadAnimations();
    }

    private void loadAnimations() {
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
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

        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    private void getWidgets(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_suggest);
        tvNoResult = (TextView) view.findViewById(R.id.text_no_result);
    }

    private class TaskGetAddress extends AsyncTask<Void, Void, List<Address>> {
        private boolean isWorking = false;
        private String text;

        public TaskGetAddress(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            isWorking = true;
        }

        @Override
        protected List<Address> doInBackground(Void... params) {
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(text, MAX_RESULTS);
            } catch (Exception e) {
            } finally {
                isWorking = false;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            try {
                show();

                if (!isCancelled()) {
                    recyclerView.swapAdapter(new LocationSuggestAdapter(
                            getContext(), addresses, onSuggestSelectedListener), true);

                    if (addresses == null || addresses.isEmpty()) {
                        tvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        tvNoResult.setVisibility(View.INVISIBLE);
                    }

                    setHeight(addresses);
                }
            } catch (Exception e) {
                Logger.printStackTrace(e);
            }
        }
    }
}
