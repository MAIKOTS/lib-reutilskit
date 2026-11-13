package reutilskit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

public class ScrollView2D extends ViewGroup {
    private OverScroller scroller;
    private GestureDetector gestureDetector;
    private int lastX, lastY, touchSlop;
    private boolean isBeingDragged = false;
    private int maxScrollX, maxScrollY;

    public ScrollView2D(Context context) { this(context, null); }
    public ScrollView2D(Context context, AttributeSet attrs) { super(context, attrs); init(context); }

    private void init(Context ctx) {
        scroller = new OverScroller(ctx);
        touchSlop = ViewConfiguration.get(ctx).getScaledTouchSlop();
        gestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onDown(MotionEvent e) { return true; }
            @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (maxScrollX > 0 || maxScrollY > 0) {
                    scroller.fling(getScrollX(), getScrollY(), (int) -velocityX, (int) -velocityY, 0, maxScrollX, 0, maxScrollY);
                    postInvalidateOnAnimation();
                }
                return true;
            }
        });
    }

    @Override protected void onMeasure(int widthSpec, int heightSpec) {
        int width = MeasureSpec.getSize(widthSpec);
        int height = MeasureSpec.getSize(heightSpec);
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }
        setMeasuredDimension(width, height);
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();
            maxScrollX = Math.max(0, childW - getWidth());
            maxScrollY = Math.max(0, childH - getHeight());
            child.layout(0, 0, childW, childH);
        }
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) ev.getX(); lastY = (int) ev.getY();
                isBeingDragged = !scroller.isFinished();
                if (isBeingDragged) { scroller.abortAnimation(); postInvalidateOnAnimation(); }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (isBeingDragged) return true;
                int x = (int) ev.getX(); int y = (int) ev.getY();
                if (Math.abs(x - lastX) > touchSlop || Math.abs(y - lastY) > touchSlop) {
                    isBeingDragged = true; lastX = x; lastY = y; return true;
                }
                return false;
        }
        return false;
    }

    @Override public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: lastX = (int) ev.getX(); lastY = (int) ev.getY(); return true;
            case MotionEvent.ACTION_MOVE:
                if (!isBeingDragged) return true;
                int x = (int) ev.getX(); int y = (int) ev.getY();
                scrollTo(clamp(getScrollX() - (x - lastX), 0, maxScrollX), clamp(getScrollY() - (y - lastY), 0, maxScrollY));
                lastX = x; lastY = y; return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: isBeingDragged = false; return true;
        }
        return false;
    }

    private int clamp(int valor, int min, int max) { return Math.max(min, Math.min(valor, max)); }

    @Override public void computeScroll() {
        if (scroller.computeScrollOffset()) { scrollTo(scroller.getCurrX(), scroller.getCurrY()); postInvalidateOnAnimation(); }
    }
}