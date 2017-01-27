package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.circularrangebar.R;

/**
 * Created by irina on 1/27/2017.
 */

public class AppointmentView extends View {

    private final String DEFAULT_COLOR = "#FF0000FF";
    private final int DEFAULT_ONE_INTERVAL_ANGLE_VALUE = 15;
    private final int DEFAULT_CIRCLE_START_ANGLE = 270;
    private final int DEFAULT_NUMBER_OF_VIEWS = 24;

    /*
    Since Clock Top value is 6 we have to subtract
    this value so that we can find the correct angle
    since hour 6 should be index 0 -> TOP
     */
    private final int DEFAULT_HOUR_MODIFIER = -6;
    private final int DEFAULT_STROKE_WIDTH = 26;
    private final int DEFAULT_MAX_PROGRESS = 360;

    protected float leftStartAngle;
    protected float rightEndPosition;
    protected float progress;
    protected float progressDegree;

    protected int startHour;
    protected int endHour;
    protected int startMinute;
    protected int endMinute;

    protected Path progressPath;
    protected Paint progressPaint;
    protected RectF circleRectF;

    protected int strokeWidth;

    public AppointmentView(Context context, int startHour, int startMinute,
                           int endHour, int endMinute, RectF circleRectF) {
        super(context);
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.circleRectF = circleRectF;

        initializeStrokeWidth();
        initializePaint();
        calculateStartAngle();
        calculateEndAngle();
        calculateProgressDegree();
        initializePath();
    }

    public AppointmentView(Context context, float leftStartAngle, float progressDegree, RectF circleRectF) {
        super(context);
        this.leftStartAngle = leftStartAngle;
        this.progressDegree = progressDegree;
        this.circleRectF = circleRectF;
        initializeStrokeWidth();
        initializePaint();
        initializePath();
    }

    public AppointmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initializeStrokeWidth() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        strokeWidth = (int) (DEFAULT_STROKE_WIDTH * displayMetrics.density + 0 / 5f);
    }

    private void initializePaint() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setColor(Color.parseColor(DEFAULT_COLOR));
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
    }

    private void initializePath() {
        progressPath = new Path();
        progressPath.addArc(circleRectF, leftStartAngle, progressDegree);
    }

    protected void drawAppointment(Canvas canvas) {
        canvas.drawPath(progressPath, progressPaint);
    }

    private void calculateStartAngle() {
        int startIndex = (startHour - DEFAULT_HOUR_MODIFIER + DEFAULT_NUMBER_OF_VIEWS) % DEFAULT_NUMBER_OF_VIEWS;
        leftStartAngle = startIndex * DEFAULT_ONE_INTERVAL_ANGLE_VALUE + DEFAULT_CIRCLE_START_ANGLE;
    }

    private void calculateEndAngle() {
        int indexDiff = endHour - startHour;
        progress = indexDiff * DEFAULT_ONE_INTERVAL_ANGLE_VALUE;
        float progressPercent = progress / DEFAULT_MAX_PROGRESS;
        rightEndPosition = (progressPercent * DEFAULT_MAX_PROGRESS) + leftStartAngle;
        rightEndPosition = rightEndPosition % 360;
    }

    private void calculateProgressDegree() {
        progressDegree = rightEndPosition - leftStartAngle;
        progressDegree = (progressDegree < 0 ? 360f + progressDegree : progressDegree);
    }
}
