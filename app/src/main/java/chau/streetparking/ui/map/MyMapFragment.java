package chau.streetparking.ui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

import chau.streetparking.R;

/**
 * Created by Chau Thai on 8/25/15.
 */
public class MyMapFragment extends SupportMapFragment {
    private View originalView;
    private FrameLayout frameLayout;
    private CircleView circleView;


    public MyMapFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        originalView = super.onCreateView(inflater, container, savedInstanceState);

        circleView = new CircleView(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        circleView.setLayoutParams(params);
        circleView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        frameLayout = new FrameLayout(getActivity());
        frameLayout.addView(originalView);
        frameLayout.addView(circleView);

        return frameLayout;
    }

    @Nullable
    @Override
    public View getView() {
        return originalView;
    }

    /**
     * @param radius in pixels
     */
    public void setRadius(int radius) {
        if (circleView != null)
            circleView.setRadius(radius);
    }

    public void setCircleEnable(boolean enable) {
        if (circleView != null)
            circleView.setCircleEnable(enable);
    }

    public boolean isCircleEnabled() {
        if (circleView == null)
            return false;

        return circleView.isCircleEnabled();
    }

    public int getRadius() {
        if (circleView == null)
            return 0;

        return circleView.getRadius();
    }


    private class CircleView extends View {
        private Paint paintFill;
        private Paint paintStroke;
        private int radius = 0;
        private boolean showCircle = false;

        public CircleView(Context context) {
            super(context);
            initPaint();
        }

        private void initPaint() {
            paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintFill.setColor(getResources().getColor(R.color.circle_fill_color));
            paintFill.setStyle(Paint.Style.FILL);

            paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintStroke.setColor(getResources().getColor(R.color.circle_stroke_color));
            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setStrokeWidth(3);
        }

        public void setRadius(int radius) {
            this.radius = radius;
            invalidate();
        }

        public int getRadius() {
            return radius;
        }

        public void setCircleEnable(boolean enable) {
            showCircle = enable;
            invalidate();
        }

        public boolean isCircleEnabled() {
            return showCircle;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (showCircle) {
                int x = getWidth();
                int y = getHeight();

                canvas.drawCircle(x / 2, y / 2, radius, paintFill);
                canvas.drawCircle(x / 2, y / 2, radius, paintStroke);
            }
        }
    }
}
