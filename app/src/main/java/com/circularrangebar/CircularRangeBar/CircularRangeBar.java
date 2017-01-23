package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.circularrangebar.R;
import com.circularrangebar.Views.CircularSeekBar;

/**
 * Created by irina on 1/23/2017.
 */

public class CircularRangeBar extends View {

    //region Default values
    protected static final float DEFAULT_CIRCLE_X_RADIUS = 30f;
    protected static final float DEFAULT_CIRCLE_Y_RADIUS = 30f;
    protected static final float DEFAULT_POINTER_RADIUS = 30f;
    protected static final float DEFAULT_POINTER_HALO_WIDTH = 6f;
    protected static final float DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f;
    protected static final float DEFAULT_CIRCLE_STROKE_WIDTH = 5f;
    protected static final float DEFAULT_START_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
    protected static final float DEFAULT_END_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
    protected static final int DEFAULT_MAX = 100;
    protected static final int DEFAULT_PROGRESS = 0;
    protected static final int DEFAULT_CIRCLE_COLOR = Color.DKGRAY;
    protected static final int DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255);
    protected static final int DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255);
    protected static final int DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255);
    protected static final int DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255);
    protected static final int DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT;
    protected static final int DEFAULT_POINTER_ALPHA = 135;
    protected static final int DEFAULT_POINTER_ALPHA_ONTOUCH = 100;
    protected static final boolean DEFAULT_USE_CUSTOM_RADII = false;
    protected static final boolean DEFAULT_MAINTAIN_EQUAL_CIRCLE = true;
    protected static final boolean DEFAULT_MOVE_OUTSIDE_CIRCLE = false;
    protected static final boolean DEFAULT_LOCK_ENABLED = true;
    //endregion

    //region Class Attributes
    protected Paint mCirclePaint;
    protected Paint mCircleFillPaint;
    protected Paint mCircleProgressPaint;
    protected Paint mLeftThumbPaint;
    protected Paint mRightThumbPaint;

    protected float mCircleStrokeWidth;
    protected float mCircleXRadius;
    protected float mCircleYRadius;
    protected float mLeftThumbRadius;

    protected float mStartAngle;
    protected float mEndAngle;

    protected RectF mCircleRectF = new RectF();

    protected int mCircleColor = DEFAULT_CIRCLE_COLOR;
    protected int mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
    protected int mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;

    protected float mTotalCircleDegrees;
    protected float mProgressDegrees;

    protected Path mCirclePath;
    protected Path mCircleProgressPath;
    protected Path mRightThumbStartPath;

    protected float mCircleWidth;
    protected float mCircleHeight;

    protected int mMax;
    protected int mProgress;

    protected Thumb mLeftThumb;
    protected Thumb mRightThumb;

    /**
     * If true, then the user can specify the X and Y radii.
     * If false, then the View itself determines the size of te CircularSeekBar.
     */
    protected boolean mUseCustomRadii;

    /**
     * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.
     * The smaller of the two radii will always be used in this case.
     * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.
     */
    protected boolean mMaintainEqualCircle;

    /**
     * Once a user has touched the circle, this determines if moving outside the circle is able
     * to change the position of the pointer (and in turn, the progress).
     */
    protected boolean mCanMoveOutsideCircle;

    /**
     * Represents the clockwise distance from {@code mStartAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    protected float cwDistanceFromStart;

    /**
     * Represents the counter-clockwise distance from {@code mStartAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    protected float ccwDistanceFromStart;

    /**
     * Represents the clockwise distance from {@code mEndAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    protected float cwDistanceFromEnd;

    /**
     * Represents the counter-clockwise distance from {@code mEndAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     * Currently unused, but kept just in case.
     */
    @SuppressWarnings("unused")
    protected float ccwDistanceFromEnd;

    /**
     * The previous touch action value for {@code cwDistanceFromStart}.
     * Used when touching the CircularSeekBar.
     */
    protected float lastCWDistanceFromStart;

    /**
     * Represents the clockwise distance from {@code mPointerPosition} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    protected float cwDistanceFromPointer;

    /**
     * Represents the counter-clockwise distance from {@code mPointerPosition} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    protected float ccwDistanceFromPointer;

    /**
     * True if the user is moving clockwise around the circle, false if moving counter-clockwise.
     * Used when touching the CircularSeekBar.
     */
    protected boolean mIsMovingCW;

    protected OnCircularSeekBarChangeListener mOnCircularSeekBarChangeListener;

//endregion

    public CircularRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(attrs);
        initializePaints();
        initializeThumbs();
    }

    private void initializeAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularRangeBar, 0, 0
        );

        try {
            mCircleColor = a.getColor(R.styleable.CircularRangeBar_circle_color, DEFAULT_CIRCLE_COLOR);
            mCircleFillColor = a.getColor(R.styleable.CircularRangeBar_circle_fill_color, DEFAULT_CIRCLE_FILL_COLOR);
            mCircleProgressColor = a.getColor(R.styleable.CircularRangeBar_circle_progress_color, DEFAULT_CIRCLE_PROGRESS_COLOR);
            mCircleStrokeWidth = a.getDimension(R.styleable.CircularRangeBar_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH);
            mCircleXRadius = a.getDimension(R.styleable.CircularRangeBar_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS);
            mCircleYRadius = a.getDimension(R.styleable.CircularRangeBar_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS);
            mStartAngle = a.getFloat(R.styleable.CircularRangeBar_start_angle, DEFAULT_START_ANGLE);
            mEndAngle = a.getFloat(R.styleable.CircularRangeBar_end_angle, DEFAULT_END_ANGLE);
            mMax = a.getInt(R.styleable.CircularRangeBar_max, DEFAULT_MAX);
            mProgress = a.getInt(R.styleable.CircularRangeBar_progress, DEFAULT_PROGRESS);
            mCanMoveOutsideCircle = a.getBoolean(R.styleable.CircularRangeBar_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE);
            mUseCustomRadii = a.getBoolean(R.styleable.CircularRangeBar_use_custom_radii, DEFAULT_USE_CUSTOM_RADII);
            mLeftThumbRadius = a.getDimension(R.styleable.CircularRangeBar_left_thumb_radius, DEFAULT_POINTER_RADIUS);
        } finally {
            a.recycle();
        }
    }

    private void initializePaints() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mCircleFillPaint = new Paint();
        mCircleFillPaint.setAntiAlias(true);
        mCircleFillPaint.setDither(true);
        mCircleFillPaint.setColor(mCircleFillColor);
        mCircleFillPaint.setStyle(Paint.Style.FILL);

        mCircleProgressPaint = new Paint();
        mCircleProgressPaint.setAntiAlias(true);
        mCircleProgressPaint.setDither(true);
        mCircleProgressPaint.setColor(mCircleProgressColor);
        mCircleProgressPaint.setStrokeWidth(mCircleStrokeWidth);
        mCircleProgressPaint.setStyle(Paint.Style.STROKE);
        mCircleProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mCircleProgressPaint.setStrokeCap(Paint.Cap.SQUARE);

        mLeftThumbPaint = new Paint();
        mLeftThumbPaint.setAntiAlias(true);
        mLeftThumbPaint.setDither(true);
        mLeftThumbPaint.setStyle(Paint.Style.FILL);
        mLeftThumbPaint.setColor(Color.RED);
        mLeftThumbPaint.setStrokeWidth(mLeftThumbRadius);

        mRightThumbPaint = new Paint();
        mRightThumbPaint.setAntiAlias(true);
        mRightThumbPaint.setDither(true);
        mRightThumbPaint.setStyle(Paint.Style.FILL);
        mRightThumbPaint.setColor(Color.GREEN);
        mRightThumbPaint.setStrokeWidth(mLeftThumbRadius);
    }

    protected void initRects() {
        mCircleRectF.set(-mCircleWidth, -mCircleHeight, mCircleWidth, mCircleHeight);
    }

    protected void initPaths() {
        mCirclePath = new Path();
        mCirclePath.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees);

        mCircleProgressPath = new Path();
        mCircleProgressPath.addArc(mCircleRectF, mStartAngle, mProgressDegrees);

        mRightThumbStartPath = new Path();
        mRightThumbStartPath.addArc(mCircleRectF, mStartAngle - 1, 1);
    }


    protected void initializeThumbs() {
        mLeftThumb = new Thumb(getContext(), mLeftThumbRadius, mLeftThumbPaint);
        mRightThumb = new Thumb(getContext(), mLeftThumbRadius, mRightThumbPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(mCircleProgressPath, mCircleProgressPaint);
        canvas.drawPath(mCirclePath, mCircleFillPaint);

        mLeftThumb.drawThumb(canvas);
        mRightThumb.drawThumb(canvas);

    }

    protected void calculateTotalDegrees() {
        mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f; // Length of the entire circle/arc
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 360f;
        }
    }

    protected void calculateProgressDegrees() {
        if (mRightThumb.mThumbPosition == 0 || mRightThumb.mThumbPosition == 360) {
            mProgressDegrees = 0;
            return;
        }
        mProgressDegrees = mRightThumb.mThumbPosition - mStartAngle; // Verified
        mProgressDegrees = (mProgressDegrees < 0 ? 360f + mProgressDegrees : mProgressDegrees); // Verified
    }


    protected void calculateRightThumbPositionAngle() {
        if (mProgress == 0 || mProgress == 360) {
            mRightThumb.setmThumbPosition(mProgress);
            return;
        }
        float progressPercent = ((float) mProgress / (float) mMax);
        float rightThumbPositionPointer = (progressPercent * mTotalCircleDegrees) + mStartAngle;
        rightThumbPositionPointer = rightThumbPositionPointer % 360f;
        mRightThumb.setmThumbPosition(rightThumbPositionPointer);
    }

    protected void calculateLeftThumbPositionAngle() {
        mLeftThumb.setmThumbPosition(0);
    }

    protected void recalculateAll() {
        calculateTotalDegrees();
        calculateLeftThumbPositionAngle();
        calculateRightThumbPositionAngle();
        calculateProgressDegrees();

        initRects();

        initPaths();

        mRightThumb.calculatePointerXYPosition(mCircleProgressPath, mCirclePath);
        mLeftThumb.calculatePointerXYPosition(mRightThumbStartPath);
    }

    protected void switchThumbs() {
        final Thumb tempThumb = mLeftThumb;
        mLeftThumb = mRightThumb;
        mRightThumb = tempThumb;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        if (mMaintainEqualCircle) {
            int min = Math.min(width, height);
            setMeasuredDimension(min, min);
        } else {
            setMeasuredDimension(width, height);
        }

        mCircleHeight = (float) height / 2f - mCircleStrokeWidth - DEFAULT_POINTER_RADIUS;
        mCircleWidth = (float) width / 2f - mCircleStrokeWidth - DEFAULT_POINTER_RADIUS;

        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
            float min = Math.min(mCircleHeight, mCircleWidth);
            mCircleHeight = min;
            mCircleWidth = min;
        }


        recalculateAll();
    }

    public void setStartAndEndAngleSamePosition(float angle) {
        this.mStartAngle = angle;
        this.mEndAngle = angle;
    }

    public void setRightThumbAnglePoint(float angle, int progressSubstract) {
        if (angle > 360)
            angle -= 360;
        if (angle < 0)
            angle += 360;
        setStartAndEndAngleSamePosition(angle);
        substractPrgress(progressSubstract);
        recalculateAll();
        invalidate();
    }

    protected void substractPrgress(int progress) {
        this.mProgress -= progress;
        if (mProgress > 360)
            mProgress -= 360;
        if (mProgress < 0)
            mProgress += 360;
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, false);
        }
    }

    public void setStartAngle(float mStartAngle) {
        this.mStartAngle = mStartAngle;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        if (this.mProgress > 360)
            this.mProgress -= 360;
        if (this.mProgress < 0)
            this.mProgress += 360;
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener.onProgressChanged(this, this.mProgress, false);
        }
        recalculateAll();
        invalidate();
    }

    public void setMax(int max) {
        if (!(max <= 0)) { // Check to make sure it's greater than zero
            if (max <= mProgress) {
                mProgress = 0; // If the new max is less than current progress, set progress to zero
                if (mOnCircularSeekBarChangeListener != null) {
                    mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, false);
                }
            }
            mMax = max;

            recalculateAll();
            invalidate();
        }
    }

    public void setEndAngle(float mEndAngle) {
        this.mEndAngle = mEndAngle;
    }

    public int getProgress() {
        return mProgress;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public float getEndAngle() {
        return mEndAngle;
    }

    public Thumb getLeftThumb() {
        return mLeftThumb;
    }

    public Thumb getRightThumb() {
        return mRightThumb;
    }

    /**
     * Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.
     */
    public interface OnCircularSeekBarChangeListener {

        public abstract void onProgressChanged(CircularRangeBar circularSeekBar, int progress, boolean fromUser);

        public abstract void onStopTrackingTouch(CircularRangeBar seekBar);

        public abstract void onStartTrackingTouch(CircularRangeBar seekBar);
    }

    public void setOnCircularSeekBarChangeListener(OnCircularSeekBarChangeListener onCircularSeekBarChangeListener) {
        this.mOnCircularSeekBarChangeListener = onCircularSeekBarChangeListener;
    }
}

