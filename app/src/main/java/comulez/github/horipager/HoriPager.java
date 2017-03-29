package comulez.github.horipager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by eado on 2017/3/28.
 */

public class HoriPager extends ViewGroup {

    private Scroller scroller;
    private int childrenCount;
    private int lastInterX;
    private int lastInterY;
    private VelocityTracker velocityTracker;
    private int lastX;
    private int lastY;
    private int mChildWidth;
    private int mChildIndex;

    public HoriPager(Context context) {
        this(context, null);
    }

    public HoriPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HoriPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = lastInterX - x;
                int deltaY = lastInterY - y;
                if (Math.abs(deltaX) > Math.abs(deltaY)) intercept = true;
                else intercept = false;
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        lastInterX = x;
        lastInterY = y;
        lastX=x;//这里要存下；
        lastY=y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished())
                    scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = lastX - x;
                int deltaY = lastY - y;
                scrollBy(deltaX, 0);
//                Log.e("lcy", "lastX=" + lastX + ",x=" + x + "deltaX=" + deltaX);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > 500) {
                    mChildIndex = xVelocity > 0 ? mChildIndex - 1 : mChildIndex + 1;
                } else {
                    mChildIndex = (int) Math.rint(1.0f * scrollX / mChildWidth);
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, childrenCount - 1));
                int dx = mChildIndex * mChildWidth - scrollX;
//                Log.e("lcy", "mChildIndex=" + mChildIndex);
                smoothScrollBy(dx, 0);
                velocityTracker.clear();
                break;
            default:
                break;
        }
        lastX = x;
        lastY = y;
        return true;
    }

    private void smoothScrollBy(int dx, int dy) {
        scroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        velocityTracker.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = 0;
        int measuredHeight = 0;
        int childCount = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpaceMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpaceMode = MeasureSpec.getMode(heightMeasureSpec);
        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if (widthSpaceMode == MeasureSpec.AT_MOST && heightSpaceMode == MeasureSpec.AT_MOST) {//宽高wrap_content；
            View child = getChildAt(0);
            measuredWidth = child.getMeasuredWidth() * childCount;
            measuredHeight = child.getMeasuredHeight();
            setMeasuredDimension(measuredWidth, measuredHeight);
        } else if (heightSpaceMode == MeasureSpec.AT_MOST) {//宽精确，高wrap_content；
            View child = getChildAt(0);
            measuredHeight = child.getMeasuredHeight();
            setMeasuredDimension(widthSpaceSize, measuredHeight);
        } else if (widthSpaceMode == MeasureSpec.AT_MOST) {//宽wrap_content；高精确；
            View child = getChildAt(0);
            measuredWidth = child.getMeasuredWidth() * childCount;
            setMeasuredDimension(measuredWidth, heightSpaceSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int count = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
//        Log.e("lcy", "PaddingLeft=" + paddingLeft + "PaddingTop=" + paddingTop + "PaddingRight=" + paddingRight + "PaddingBottom=" + paddingBottom);
        childrenCount = count;
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                int childWidth = childView.getMeasuredWidth();
                mChildWidth = childWidth;
                childView.layout(paddingLeft + childLeft, 0 + paddingTop, paddingLeft + childLeft + childWidth + paddingRight, childView.getMeasuredHeight() + paddingBottom);
                childLeft += childWidth;
            }
        }
    }
}
