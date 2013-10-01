package ru.yandex.yac13.example.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

public class CustomSlidingDrawer extends FrameLayout {

    private VelocityTracker velocityTracker;
    private float bottomOffset;

    private final int BOTTOM_OFFSET_DEFAULT = 200;

    private OverScroller scroller;

    private static final int VELOCITY_UNITS_DIP = 1000;
    private int velocityUnits;
    private int touchDelta;
    private boolean tracking = false;

    private float maxVelocity;
    private float touchSlop;

    public CustomSlidingDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomSlidingDrawer);
        bottomOffset = a.getDimension(R.styleable.CustomSlidingDrawer_bottom_offset, BOTTOM_OFFSET_DEFAULT);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final float density = getResources().getDisplayMetrics().density;
        velocityUnits = (int) (VELOCITY_UNITS_DIP * density);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());

        maxVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        touchSlop = viewConfiguration.getScaledTouchSlop();

        scroller = new OverScroller(getContext(), new SmoothInterpolator());
    }

    private float startX, startY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            scroller.forceFinished(true);
            startX = ev.getX();
            startY = ev.getY();
            velocityTracker = VelocityTracker.obtain();
            velocityTracker.addMovement(ev);
        } else if (action == MotionEvent.ACTION_MOVE) {
            velocityTracker.addMovement(ev);

            int distanceY = (int) Math.abs(y - startY);
            if (distanceY >= touchSlop) {
                startTracking(y);
                return true;
            }
            int distanceX = (int) Math.abs(x - startX);
            if (distanceX >= touchSlop) {
                return false;
            }
        }

        return false;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currY = -scroller.getCurrY();

            scrollTo(0, currY);
            invalidate();
        }
        super.computeScroll();
    }

    private void startTracking(int position) {
        int top = -getScrollY();
        touchDelta = (int) position - top;
        tracking = true;
        scroller.forceFinished(true);
    }

    private void stopTracking() {
        tracking = false;
        velocityTracker.recycle();
        velocityTracker = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        int action = MotionEventCompat.getActionMasked(event);

        int y = (int) event.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            startTracking(y);
        }

        if (tracking) {
            switch (action) {
            case MotionEvent.ACTION_MOVE:
                move(y - touchDelta);
                break;
            case MotionEvent.ACTION_CANCEL:
                stopTracking();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(velocityUnits, maxVelocity);
                float yVelocity = velocityTracker.getYVelocity();

                stopTracking();

                performFling(yVelocity);
                break;
            }
        }

        return tracking;
    }
    
    private int getBottomEdge() {
        return (int) (getHeight() - bottomOffset);
    }

    private int getTopEdge() {
        if (getChildCount() == 0) {
            return getBottomEdge();
        }
        View child = getChildAt(0);

        return getHeight() - child.getHeight();
    }

    private void performFling(float velocity) {
        int min = getTopEdge();
        int max = getBottomEdge();

        int start = -getScrollY();

        scroller.fling(0, start, 0, (int) velocity / 2, 0, 0, min, max);
    }

    private void move(int position) {
        int min = getTopEdge();
        int max = getBottomEdge();

        if (position < min) {
            position = min;
        } else if (position > max) {
            position = max;
        }

        scrollTo(0, -position);
        invalidate();
    }

    public static class SmoothInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return (float) (Math.pow(input - 1, 5) + 1);
        }
    }
    
    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SlidingDrawer can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SlidingDrawer can host only one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SlidingDrawer can host only one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SlidingDrawer can host only one direct child");
        }

        super.addView(child, index, params);
    }    
}
