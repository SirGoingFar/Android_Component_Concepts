package com.eemf.sirgoingfar.androidthreadingpolicy.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.eemf.sirgoingfar.androidthreadingpolicy.R;

import java.util.List;

public class ModuleStatusView extends View {

    private final int SHAPE_CIRCLE = 0;
    private final float DEFAULT_OUTLINE_WIDTH_DP = 2F;
    public final float DEFAULT_SHAPE_SIZE_DP = 40F;
    private final float DEFAULT_SHAPE_SPACING_DP = 6F;
    private final int INVALID_INDEX = -1;

    private Context mContext;

    private float mOutlineWidth;
    private float mShapeSize;
    private float mShapeSpacing;
    private int mShapeType;
    private boolean[] mModuleStatus;
    private Rect[] mRectArray;
    private Paint mStrokePaint;
    private int mStrokeColor;
    private int mFillColor;
    private Paint mFillPaint;
    private int mRadius;
    private int mMaxHorizontalModule;
    private ModuleStatusViewAccessibilityHelper mAccessibilityHelper;

    public ModuleStatusView(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs, defStyle);
    }

    public void setmModuleStatus(boolean[] mModuleStatus) {
        this.mModuleStatus = mModuleStatus;
    }

    public boolean[] getmModuleStatus() {
        return mModuleStatus;
    }

    private void init(AttributeSet attrs, int defStyle) {

        //mock data for testing
        if (isInEditMode()) {
            setupEditModeValue();
        }

        //Enable Accessibility
        mAccessibilityHelper = new ModuleStatusViewAccessibilityHelper(this);
        ViewCompat.setAccessibilityDelegate(this, mAccessibilityHelper);


        // Load attributes
        final TypedArray attributeArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        setShapeProperties(attributeArray);

        attributeArray.recycle();

        mRadius = (int) (mShapeSize - mOutlineWidth) / 2;

        //create paint objects
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mOutlineWidth);
        mStrokePaint.setColor(mStrokeColor);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mFillColor);
    }

    private void setShapeProperties(TypedArray attributeArray) {

        float deviceDensity = mContext.getResources().getDisplayMetrics().density;

        //Set View Outline Width
        float defaultOutlineWidth = deviceDensity * DEFAULT_OUTLINE_WIDTH_DP;
        mOutlineWidth = attributeArray.getDimension(R.styleable.ModuleStatusView_outlineWidth, defaultOutlineWidth);

        //Set View Outline Color
        mStrokeColor = attributeArray.getColor(R.styleable.ModuleStatusView_outlineColor, Color.BLACK);

        //Set Shape Color
        mFillColor = attributeArray.getColor(R.styleable.ModuleStatusView_shapeFill, getResources().getColor(R.color.colorPrimaryDark));

        //Set Shape Size
        float defaultShapeSize = deviceDensity * DEFAULT_SHAPE_SIZE_DP;
        mShapeSize = attributeArray.getDimension(R.styleable.ModuleStatusView_shapeSize, defaultShapeSize);

        //Set Shape Spacing
        float defaultShapeSpacing = deviceDensity * DEFAULT_SHAPE_SPACING_DP;
        mShapeSpacing = attributeArray.getDimension(R.styleable.ModuleStatusView_shapeSpacing, defaultShapeSpacing);

        //Set Shape Type
        mShapeType = attributeArray.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
    }

    private void setupEditModeValue() {

        int EDIT_MODE_ARRAY_LENGTH = 7;

        boolean[] exampleValue = new boolean[EDIT_MODE_ARRAY_LENGTH];

        for (int index = 0; index < (exampleValue.length / 2); index++) {
            exampleValue[index] = true;
        }

        setmModuleStatus(exampleValue);
    }

    private void setupRectFrames(int newWidth) {

        mRectArray = new Rect[mModuleStatus.length];

        int availableWidth = newWidth - (getPaddingLeft() + getPaddingRight());
        int horizontalModuleThatCanFit = (int) (availableWidth / (mShapeSize + mShapeSpacing));
        int maxHorizontalModule = Math.min(horizontalModuleThatCanFit, mModuleStatus.length);

        int left;
        int top;

        for (int moduleIndex = 0; moduleIndex < mModuleStatus.length; moduleIndex++) {

            int row = moduleIndex / maxHorizontalModule;
            int column = moduleIndex % maxHorizontalModule;

            left = getPaddingLeft() + (int) (column * (mShapeSize + mShapeSpacing));
            top = getPaddingTop() + (int) (row * (mShapeSize + mShapeSpacing));
            mRectArray[moduleIndex] = new Rect(left, top, left + (int) mShapeSize, top + (int) mShapeSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupRectFrames(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x;
        int y;

        for (int moduleIndex = 0; moduleIndex < mRectArray.length; moduleIndex++) {

            x = mRectArray[moduleIndex].centerX();
            y = mRectArray[moduleIndex].centerY();

            //draw Color Fill
            if (mModuleStatus[moduleIndex])
                canvas.drawCircle(x, y, mRadius, mFillPaint);

            //draw Stroke
            canvas.drawCircle(x, y, mRadius, mStrokePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth;
        int desiredHeight;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - (getPaddingLeft() + getPaddingRight());
        int horizontalModuleThatCanFit = (int) (availableWidth / (mShapeSize + mShapeSpacing));
        mMaxHorizontalModule = Math.min(horizontalModuleThatCanFit, mModuleStatus.length);

        /*compute the desired width and height*/
        desiredWidth = (mMaxHorizontalModule * ((int) (mShapeSize + mShapeSpacing))) - (int) mShapeSpacing;
        desiredWidth += getPaddingLeft() + getPaddingRight();

        //NB: At least 1 row (reason for subtracting 1 module view)
        int rowNeededToDrawAllModule = (int) ((mModuleStatus.length - 1) / mMaxHorizontalModule) + 1;

        desiredHeight = (int) ((rowNeededToDrawAllModule * (mShapeSize + mShapeSpacing)) - mShapeSpacing);
        desiredHeight += getPaddingTop() + getPaddingBottom();

        /*resolve the desired params to the canvas params*/
        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);


        //notify the System about the measurement
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                int clickedModuleIndex = findModuleClicked(event.getX(), event.getY());
                onModuleClick(clickedModuleIndex);
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    private int findModuleClicked(float x, float y) {

        int moduleIndex = INVALID_INDEX;

        for (int index = 0; index < mRectArray.length; index++) {

            if (mRectArray[index].contains((int) x, (int) y)) {
                moduleIndex = index;
                break;
            }

        }

        return moduleIndex;
    }

    private void onModuleClick(int clickedModuleIndex) {

        if (clickedModuleIndex == INVALID_INDEX)
            return;

        mModuleStatus[clickedModuleIndex] = !mModuleStatus[clickedModuleIndex];

        //re-draw the view
        invalidate();

        //reset Accessibility too
        mAccessibilityHelper.invalidateVirtualView(clickedModuleIndex);
        mAccessibilityHelper.sendEventForVirtualView(clickedModuleIndex, AccessibilityEvent.TYPE_VIEW_CLICKED);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    private class ModuleStatusViewAccessibilityHelper extends ExploreByTouchHelper{
        /**
         * Constructs a new helper that can expose a virtual view hierarchy for the
         * specified host view.
         *
         * @param host view whose virtual view hierarchy is exposed by this helper
         */
        public ModuleStatusViewAccessibilityHelper(View host) {
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {

            int clickedModuleId = findModuleClicked(x, y);

            return clickedModuleId == INVALID_ID ? ExploreByTouchHelper.INVALID_ID : clickedModuleId;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {

            if(mRectArray == null)
                return;

            for(int virtualViewIndex = 0; virtualViewIndex < mRectArray.length; virtualViewIndex++)
                virtualViewIds.add(virtualViewIndex);
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            node.setFocusable(true);
            node.setBoundsInParent(mRectArray[virtualViewId]);
            //in real app, the "Module Title"
            node.setContentDescription("Module " + virtualViewId);

            node.setCheckable(true);
            node.setChecked(mModuleStatus[virtualViewId]);

            //support click action
            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {

            switch (action) {
                case AccessibilityNodeInfoCompat.ACTION_CLICK:
                    onModuleClick(virtualViewId);
                    return true;
            }

            return false;
        }
    }

}
