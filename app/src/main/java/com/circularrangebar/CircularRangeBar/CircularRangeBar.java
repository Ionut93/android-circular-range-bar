package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.circularrangebar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by irina on 1/23/2017.
 */

public class CircularRangeBar extends View {

    //region Constants

    /**
     * Used to scale the dp units to pixels
     */
    protected final float DPTOPX_SCALE = getResources().getDisplayMetrics().density;

    /**
     * Minimum touch target size in DP. 48dp is the Android design recommendation
     */
    protected final float MIN_TOUCH_TARGET_DP = 48;
    /**
     * Margin between Text and Handle
     */
    protected final float MIN_MARGIN_TEXT_HANDLE = 12 * DPTOPX_SCALE;
    protected final float MIN_CIRCLE_MARGIN = 18 * DPTOPX_SCALE;
    //endregion

    //region Default values
    protected static final float DEFAULT_CIRCLE_X_RADIUS = 30f;
    protected static final float DEFAULT_CIRCLE_Y_RADIUS = 30f;
    protected static final float DEFAULT_POINTER_RADIUS = 0f;
    protected static final float DEFAULT_CIRCLE_STROKE_WIDTH = 5f;
    protected static final float DEFAULT_START_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
    protected static final float DEFAULT_END_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
    protected static final int DEFAULT_MAX = 100;
    protected static final int DEFAULT_PROGRESS = 0;
    protected static final int DEFAULT_CIRCLE_COLOR = Color.TRANSPARENT;
    protected static final int DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255);
    protected static final int DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT;
    protected static final boolean DEFAULT_USE_CUSTOM_RADII = false;
    protected static final boolean DEFAULT_MOVE_OUTSIDE_CIRCLE = false;
    protected static final float DEFAULT_ON_START_ANGLE = 999999;
    //endregion

    //region Class Attributes
    protected Paint mCirclePaint;
    protected Paint mCircleFillPaint;
    protected Paint mCircleProgressPaint;
    protected Paint mLeftThumbPaint;
    protected Paint mRightThumbPaint;
    protected Paint mInsideWhiteCirclePaint;
    protected Paint mPiesPaint;

    protected float mCircleStrokeWidth;
    protected float mCircleXRadius;
    protected float mCircleYRadius;
    protected float mLeftThumbRadius;
    protected float mLeftThumbAngle;

    protected float mStartAngle;
    protected float mEndAngle;
    protected float onStartTouchAngle = DEFAULT_ON_START_ANGLE;

    protected RectF mCircleRectF = new RectF();
    protected RectF progressRectF = new RectF();
    protected RectF outerCircleRectF = new RectF();
    protected RectF pieCircleRectF = new RectF();

    protected Region pieRegion = new Region();
    protected Region progressRegion = new Region();

    protected int mCircleColor = DEFAULT_CIRCLE_COLOR;
    protected int mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
    protected int mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;

    protected float mTotalCircleDegrees;
    protected float mProgressDegrees;

    protected Path mCirclePath;
    protected Path mCircleProgressPath;
    protected Path mLeftThumbStartPath;
    protected Path mLeftThumbOutsideStartPath;
    protected Path mOutsideCircleProgressPath;

    protected float mCircleWidth;
    protected float mCircleHeight;

    protected int mMax;
    protected int mProgress;
    protected int mProgressPosition;

    protected Thumb mLeftThumb;
    protected Thumb mRightThumb;

    public boolean isTouchEnabled = true;

    /**
     * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.
     * The smaller of the two radii will always be used in this case.
     * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.
     */
    protected boolean mMaintainEqualCircle = true;

    protected boolean isProgressTouched = false;

    protected boolean hideCurrentProgress = true;

    protected OnCircularSeekBarChangeListener mOnCircularSeekBarChangeListener;

    List<AppointmentView> appointments = new ArrayList<>();
    List<AppointmentViewModel> appointmentViewModels = new ArrayList<>();

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
            mLeftThumbRadius = a.getDimension(R.styleable.CircularRangeBar_thumb_radius, DEFAULT_POINTER_RADIUS);
            mLeftThumbAngle = mStartAngle;
            mProgressPosition = mProgress;
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

        mInsideWhiteCirclePaint = new Paint();
        mInsideWhiteCirclePaint.setAntiAlias(true);
        mInsideWhiteCirclePaint.setDither(true);
        mInsideWhiteCirclePaint.setColor(Color.WHITE);
        mInsideWhiteCirclePaint.setStyle(Paint.Style.FILL);

        mCircleProgressPaint = new Paint();
        mCircleProgressPaint.setAntiAlias(true);
        mCircleProgressPaint.setDither(true);
        mCircleProgressPaint.setColor(mCircleProgressColor);
        mCircleProgressPaint.setStrokeWidth(mCircleStrokeWidth);
        mCircleProgressPaint.setStyle(Paint.Style.STROKE);
        mCircleProgressPaint.setStrokeJoin(Paint.Join.ROUND);

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

        mPiesPaint = new Paint();
        mPiesPaint.setAntiAlias(true);
        mPiesPaint.setDither(true);
        mPiesPaint.setColor(Color.GRAY);
        mPiesPaint.setStrokeWidth(mCircleStrokeWidth - 4);
        mPiesPaint.setStyle(Paint.Style.STROKE);
        mPiesPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    protected void initRects() {
        mCircleRectF.set(-mCircleWidth, -mCircleHeight, mCircleWidth, mCircleHeight);
        outerCircleRectF.set(-mCircleWidth - mCircleStrokeWidth - MIN_MARGIN_TEXT_HANDLE * 2, -mCircleHeight - mCircleStrokeWidth - MIN_MARGIN_TEXT_HANDLE,
                mCircleWidth + mCircleStrokeWidth + MIN_MARGIN_TEXT_HANDLE * 2, mCircleHeight + mCircleStrokeWidth + MIN_MARGIN_TEXT_HANDLE * 3);
    }

    protected void initPaths() {
        if (mCirclePath == null)
            mCirclePath = new Path();
        mCirclePath.rewind();
        mCirclePath.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees);

        if (mCircleProgressPath == null)
            mCircleProgressPath = new Path();
        mCircleProgressPath.rewind();
        mCircleProgressPath.addArc(mCircleRectF, mLeftThumbAngle, mProgressDegrees);

        if (mOutsideCircleProgressPath == null)
            mOutsideCircleProgressPath = new Path();
        mOutsideCircleProgressPath.rewind();
        mOutsideCircleProgressPath.addArc(outerCircleRectF, mLeftThumbAngle, mProgressDegrees);

        if (mLeftThumbStartPath == null)
            mLeftThumbStartPath = new Path();
        mLeftThumbStartPath.rewind();
        mLeftThumbStartPath.addArc(mCircleRectF, mLeftThumbAngle, 0.3f);

        if (mLeftThumbOutsideStartPath == null)
            mLeftThumbOutsideStartPath = new Path();
        mLeftThumbOutsideStartPath.rewind();
        mLeftThumbOutsideStartPath.addArc(outerCircleRectF, mLeftThumbAngle, 0.3f);


        if (progressRegion == null)
            progressRegion = new Region();
        progressRegion.setPath(mOutsideCircleProgressPath, new Region((int) outerCircleRectF.left, (int) outerCircleRectF.top, (int) outerCircleRectF.right, (int) outerCircleRectF.bottom));
        progressRectF.setEmpty();
        mCircleProgressPath.computeBounds(progressRectF, false);

        if (pieRegion == null)
            pieRegion = new Region();
        pieRegion.setPath(mCirclePath, new Region(new Region((int) mCircleRectF.left, (int) mCircleRectF.top, (int) mCircleRectF.right, (int) mCircleRectF.bottom)));
        pieCircleRectF.setEmpty();
        mCirclePath.computeBounds(pieCircleRectF, false);
    }

    protected void initializeThumbs() {
        mLeftThumb = new Thumb(getContext(), mLeftThumbRadius, mLeftThumbPaint, outerCircleRectF);
        mRightThumb = new Thumb(getContext(), mLeftThumbRadius, mRightThumbPaint, outerCircleRectF);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(mCirclePath, mCircleFillPaint);
        for (int i = 0; i < 24; i++) {
            canvas.drawArc(mCircleRectF, i * 15 + 1, 14, false, mPiesPaint);
        }

        for (AppointmentView a : appointments)
            a.drawAppointment(canvas, getCircleRectF(), mCircleProgressPaint);
        if (!hideCurrentProgress) {
            canvas.drawPath(mCircleProgressPath, mCircleProgressPaint);
            mLeftThumb.drawThumb(canvas, mLeftThumb.getThumbPosition());
            mRightThumb.drawThumb(canvas, mRightThumb.getThumbPosition());
        }
        canvas.drawCircle(mCircleRectF.centerX(), mCircleRectF.centerY(), mCircleWidth - mCircleStrokeWidth / 2, mInsideWhiteCirclePaint);

    }

    protected void calculateTotalDegrees() {
        mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f; // Length of the entire circle/arc
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 360f;
        }
    }

    protected void calculateProgressDegrees() {
        if ((mProgress == 0 && mRightThumb.mThumbPosition == 0)
                || (mProgress == 360 && mRightThumb.mThumbPosition == 360)) {
            mProgressDegrees = 0;
            return;
        }
        mProgressDegrees = mRightThumb.mThumbPosition - mLeftThumbAngle; // Verified
        mProgressDegrees = (mProgressDegrees < 0 ? 360f + mProgressDegrees : mProgressDegrees); // Verified
    }

    protected void calculateRightThumbPositionAngle() {
        if (mProgress == 0 || mProgress == 360) {
            mRightThumb.setThumbPosition(mLeftThumbAngle);
            return;
        }
        float progressPercent = ((float) mProgress / (float) mMax);
        float rightThumbPositionPointer = (progressPercent * mTotalCircleDegrees) + mLeftThumbAngle;
        rightThumbPositionPointer = rightThumbPositionPointer % 360f;
        mRightThumb.setThumbPosition(rightThumbPositionPointer);
    }

    protected void calculateLeftThumbPositionAngle() {
        mLeftThumb.setThumbPosition(mLeftThumbAngle);
    }

    protected void recalculateAll() {
        calculateTotalDegrees();
        calculateLeftThumbPositionAngle();
        calculateRightThumbPositionAngle();
        calculateProgressDegrees();

        initRects();

        initPaths();
        calculateXYPositionOfThumbsInArc();
        calculateTextXY();
    }

    protected void calculateTextXY() {
        mRightThumb.calculateTextPositionXY(mOutsideCircleProgressPath);
        mLeftThumb.calculateTextPositionXY(mLeftThumbOutsideStartPath);
    }

    protected void calculateXYPositionOfThumbsInArc() {
        if (mProgressDegrees == 0)
            mRightThumb.calculatePointerXYPosition(mLeftThumbStartPath);
        else
            mRightThumb.calculatePointerXYPosition(mCircleProgressPath, mCirclePath);
        mLeftThumb.calculatePointerXYPosition(mLeftThumbStartPath);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
      /*  if (mMaintainEqualCircle) {
            int min = Math.min(width, height);
            setMeasuredDimension(min, min);
        } else {
            setMeasuredDimension(width, height);
        }*/
        setMeasuredDimension(width, height);
        mCircleHeight = (float) height / 2f - mCircleStrokeWidth - mLeftThumbRadius - MIN_CIRCLE_MARGIN;
        mCircleWidth = (float) width / 2f - mCircleStrokeWidth - mLeftThumbRadius - MIN_CIRCLE_MARGIN;

        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
            float min = Math.min(mCircleHeight, mCircleWidth);
            mCircleHeight = min;
            mCircleWidth = min;
        }

        recalculateAll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchEnabled)
            return false;

        // Convert coordinates to our internal coordinate system
        float x = event.getX() - getWidth() / 2;
        float y = event.getY() - getHeight() / 2;

        float touchAngle;
        touchAngle = (float) ((java.lang.Math.atan2(y, x) / Math.PI * 180) % 360); // Verified
        touchAngle = (touchAngle < 0 ? 360 + touchAngle : touchAngle); // Verified

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onActionDown(x, y, touchAngle);
            case MotionEvent.ACTION_MOVE:
                return onActionMove(touchAngle);
            case MotionEvent.ACTION_CANCEL:
                return onActionUp(touchAngle);
            case MotionEvent.ACTION_UP:
                return onActionUp(touchAngle);
        }
        return false;
    }

    private boolean onActionUp(float touchAngle) {
        touchAngle = roundProgress(touchAngle);
        int minutesToDisplay = 0;
        if (touchAngle % 15 != 0)
            minutesToDisplay = 30;
        if (mRightThumb.isThumbPressed()) {
            moveThumbRight(touchAngle);
            mRightThumb.setMinutes(minutesToDisplay);
            recalculateAll();
            invalidate();
        } else if (mLeftThumb.isThumbPressed()) {
            moveThumbLeft(touchAngle);
            mLeftThumb.setMinutes(minutesToDisplay);
            recalculateAll();
            invalidate();
        }
        mLeftThumb.setThumbPressed(false);
        mRightThumb.setThumbPressed(false);
        isProgressTouched = false;
        onStartTouchAngle = DEFAULT_ON_START_ANGLE;
        return true;
    }

    private float roundProgress(float touchAngle) {
        float angle = touchAngle % 15;
        touchAngle = touchAngle - angle;
        if (angle <= 5) {
            // round bottom;
        } else if (angle > 5 && angle <= 11) {
            //round to half
            touchAngle += 7.5;
        } else if (angle > 11) {
            //round top
            touchAngle += 15;
        }
        return touchAngle;
    }

    private boolean onActionDown(float x, float y, float touchAngle) {
        if (inCircle(x, y, mCircleRectF.centerX(), mCircleRectF.centerY(), mCircleHeight - mCircleStrokeWidth / 2)) {
            if (mOnCircularSeekBarChangeListener != null)
                mOnCircularSeekBarChangeListener.onInsideCircleClicked(this);
            return true;
        } else if (!mLeftThumb.isThumbPressed() && !hideCurrentProgress
                && !mRightThumb.isThumbPressed() && mRightThumb.isInTargetZone(x, y)) {
            mRightThumb.setThumbPressed(true);
            return true;
        } else if (!mRightThumb.isThumbPressed() && !hideCurrentProgress
                && mLeftThumb.isInTargetZone(x, y)) {
            mLeftThumb.setThumbPressed(true);
            return true;
        } else if (progressRectF.contains(x, y) && !hideCurrentProgress) {
            isProgressTouched = true;
            return true;
        } else if ((pieCircleRectF.contains((int) x, (int) y) || pieRegion.contains((int) x, (int) y))
                && !inCircle(x, y, mCircleRectF.centerX(), mCircleRectF.centerY(), mCircleHeight - mCircleStrokeWidth / 2)) {
            setThumbsLocationOnSection(touchAngle);
            hideCurrentProgress = false;
            recalculateAll();
            invalidate();
            return true;
        }
        return false;
    }

    private boolean onActionMove(float touchAngle) {
        if (mRightThumb.isThumbPressed()) {
            moveThumbRight(touchAngle);
            recalculateAll();
            invalidate();
            return true;
        } else if (mLeftThumb.isThumbPressed()) {
            moveThumbLeft(touchAngle);
            recalculateAll();
            invalidate();
            return true;
        } else if (isProgressTouched) {
            moveWholeBarWithoutChangingProgressValue(touchAngle);
            recalculateAll();
            invalidate();
            return true;
        }
        return false;

    }

    private void setThumbsLocationOnSection(float touchAngle) {
        float sectionStartTouchAngle = touchAngle - (touchAngle % 15);
        float sectionEndTouchAngle = sectionStartTouchAngle + 15;
        if (sectionEndTouchAngle > 360)
            sectionEndTouchAngle -= 360;
        setLeftThumbAngle(sectionStartTouchAngle);
        moveThumbRight(sectionEndTouchAngle);
    }

    private boolean inCircle(float x, float y, float circleCenterX, float circleCenterY, float circleRadius) {
        double dx = Math.pow(x - circleCenterX, 2);
        double dy = Math.pow(y - circleCenterY, 2);

        if ((dx + dy) < Math.pow(circleRadius, 2)) {
            return true;
        } else {
            return false;
        }
    }


    private void moveThumbLeft(float angle) {
        float progressDif = calculateProgressBetweenTwoAngles(mLeftThumbAngle, angle);
        modifyPrgressByValue(progressDif);
        setLeftThumbAngle(mLeftThumbAngle + progressDif);
    }

    private void roundLeftThumb(float angle) {
        float progressDif = calculateProgressBetweenTwoAngles(mLeftThumbAngle, angle);
        modifyPrgressByValue(progressDif);
        setLeftThumbAngle(angle);
    }

    private void moveWholeBarWithoutChangingProgressValue(float angle) {
        if (onStartTouchAngle == DEFAULT_ON_START_ANGLE) {
            onStartTouchAngle = angle;
        } else {
            float angleToAdd = angle - onStartTouchAngle;
            onStartTouchAngle = angle;
            addAngleToLeftThumb(angleToAdd);
            calculateProgressDegrees();
        }
    }

    private void moveThumbRight(float touchAngle) {
        setProgressBasedOnAngle(touchAngle, mRightThumb);
    }

    protected void setProgressBasedOnAngle(float angle, Thumb thumb) {
        thumb.mThumbPosition = angle;
        calculateProgressDegrees();
        mProgress = Math.round((float) mMax * mProgressDegrees / mTotalCircleDegrees);
    }

    protected float calculateProgressBetweenTwoAngles(float thumbAngle, float newThumbAngle) {
        float phi = (int) (newThumbAngle - thumbAngle) % 360;
        float distance = phi > 360 ? 360 - phi : phi;
        return distance;
    }

    protected void modifyPrgressByValue(float progress) {
        this.mProgress -= progress;
        verifyProgressValue();
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, false);
        }
    }

    private void verifyProgressValue() {
        if (mProgress > 360)
            mProgress -= 360;
        if (mProgress < 0)
            mProgress += 360;
        if (this.mProgress == 360)
            this.mProgress = 0;
    }

    public void addAngleToLeftThumb(float angle) {
        this.mLeftThumbAngle += angle;
        if (mLeftThumbAngle > 360)
            mLeftThumbAngle -= 360;
        if (mLeftThumbAngle < 0)
            mLeftThumbAngle += 360;
    }

    public void addAppointment(int startHour, int startMinute, int endHour, int endMinute) {
        AppointmentViewModel viewModel = new AppointmentViewModel(endHour, endMinute, startHour, startMinute);
        this.appointmentViewModels.add(viewModel);
        this.appointments.add(new AppointmentView(getContext(), viewModel));
        invalidate();
    }

    public void hideCurrentProgress() {
        hideCurrentProgress = true;
        invalidate();
    }

    public void showCurrentProgress() {
        hideCurrentProgress = false;
        invalidate();
    }

    public void addCurrentAppointment() {
        AppointmentViewModel viewModel = new AppointmentViewModel(mRightThumb.getHour(), mRightThumb.getMinutes(), mLeftThumb.getHour(), mLeftThumb.getMinutes());
        this.appointmentViewModels.add(viewModel);
        this.appointments.add(new AppointmentView(getContext(), viewModel));
        invalidate();
    }

    public void cleanAppointments() {
        this.appointments.clear();
        invalidate();
    }

    public void setProgress(int progress) {
        hideCurrentProgress = false;
        this.mProgress = progress;
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

    public void setLeftThumbAngle(float angle) {
        hideCurrentProgress = false;
        this.mLeftThumbAngle = angle;
    }

    public void setRightThumbAngle(float angle) {
        hideCurrentProgress = false;
        this.mRightThumb.setThumbPosition(angle);
    }

    public float getLeftThumbAngle() {
        return mLeftThumbAngle;
    }

    public float getProgressDegrees() {
        return mProgressDegrees;
    }

    public int getProgress() {
        return mProgress;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public RectF getCircleRectF() {
        return mCircleRectF;
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

    public float getmCircleHeight() {
        return mCircleHeight;
    }

    public float getmCircleStrokeWidth() {
        return mCircleStrokeWidth;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();

        bundle.putParcelable("INSTANCE_STATE", super.onSaveInstanceState());
        bundle.putFloat("LEFT_THUMB_ANGLE", mLeftThumbAngle);
        bundle.putInt("PROGRESS", mProgress);
        bundle.putParcelableArrayList("APPOINTMENT_MODELS", (ArrayList) appointmentViewModels);
        bundle.putBoolean("HIDE_PROGRESS", hideCurrentProgress);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mProgress = bundle.getInt("PROGRESS");
            mLeftThumbAngle = bundle.getFloat("LEFT_THUMB_ANGLE");
            appointmentViewModels = bundle.getParcelableArrayList("APPOINTMENT_MODELS");
            hideCurrentProgress = bundle.getBoolean("HIDE_PROGRESS");
            super.onRestoreInstanceState(bundle.getParcelable("INSTANCE_STATE"));
            createAppointmentViews();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void createAppointmentViews() {
        for (AppointmentViewModel a : appointmentViewModels)
            appointments.add(new AppointmentView(getContext(), a));
    }

    /**
     * Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.
     */
    public interface OnCircularSeekBarChangeListener {

        public abstract void onProgressChanged(CircularRangeBar circularSeekBar, int progress, boolean fromUser);

        public abstract void onStopTrackingTouch(CircularRangeBar seekBar);

        public abstract void onStartTrackingTouch(CircularRangeBar seekBar);

        public abstract void onInsideCircleClicked(CircularRangeBar seekBar);

    }

    public void setOnCircularSeekBarChangeListener(OnCircularSeekBarChangeListener onCircularSeekBarChangeListener) {
        this.mOnCircularSeekBarChangeListener = onCircularSeekBarChangeListener;
    }
}

