package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by irina on 1/27/2017.
 */

public class AppointmentView extends View {

    // region DEFAULTS
    private final String DEFAULT_COLOR = "#FF0000FF";
    private final int DEFAULT_ONE_INTERVAL_ANGLE_VALUE = 15;
    private final int DEFAULT_CIRCLE_START_ANGLE = 270;
    private final int DEFAULT_NUMBER_OF_VIEWS = 24;
    private final int DEFAULT_MINUTES = 60;
    /*
    Since Clock Top value is 6 we have to subtract
    this value so that we can find the correct angle
    since hour 6 should be index 0 -> TOP
     */
    private final int DEFAULT_HOUR_MODIFIER = 6;
    private final int DEFAULT_STROKE_WIDTH = 26;
    private final int DEFAULT_MAX_PROGRESS = 360;
    //endregion

    protected float leftStartAngle;
    protected float rightEndPosition;
    protected float progress;
    protected float progressDegree;


    protected Path progressPath;
    protected AppointmentViewModel viewModel;


    public AppointmentView(Context context, AppointmentViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;

        calculateStartAngle();
        calculateEndAngle();
        calculateProgressDegree();
        initializePath();
    }


    public AppointmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void initializePath() {
        progressPath = new Path();
    }

    protected void drawAppointment(Canvas canvas, RectF circleRectF, Paint paint) {
        progressPath.addArc(circleRectF, leftStartAngle, progressDegree);
        canvas.drawPath(progressPath, paint);
    }

    private void calculateStartAngle() {
        int startIndex = (viewModel.getStartHour() - DEFAULT_HOUR_MODIFIER + DEFAULT_NUMBER_OF_VIEWS) % DEFAULT_NUMBER_OF_VIEWS;

        float minutesAngleToAdd = (viewModel.getStartMinute() * 100) / DEFAULT_MINUTES; // --> percent of minutes where 1h = 60 min;
        minutesAngleToAdd = (DEFAULT_ONE_INTERVAL_ANGLE_VALUE * minutesAngleToAdd) / 100;


        leftStartAngle = (startIndex * DEFAULT_ONE_INTERVAL_ANGLE_VALUE + DEFAULT_CIRCLE_START_ANGLE);
        leftStartAngle += minutesAngleToAdd;
        leftStartAngle %= 360;
    }

    private void calculateEndAngle() {
        int startIndex = (viewModel.getStartHour() - DEFAULT_HOUR_MODIFIER + DEFAULT_NUMBER_OF_VIEWS) % DEFAULT_NUMBER_OF_VIEWS;
        float startHourWithoutMinutesAngle = (startIndex * DEFAULT_ONE_INTERVAL_ANGLE_VALUE + DEFAULT_CIRCLE_START_ANGLE);
        int indexDiff = viewModel.getEndHour() - viewModel.getStartHour();
        progress = indexDiff * DEFAULT_ONE_INTERVAL_ANGLE_VALUE;
        float progressPercent = progress / DEFAULT_MAX_PROGRESS;
        rightEndPosition = (progressPercent * DEFAULT_MAX_PROGRESS) + startHourWithoutMinutesAngle;

        float minutesAngleToAdd = (viewModel.getEndMinute() * 100) / DEFAULT_MINUTES; // --> percent of minutes where 1h = 60 min;
        minutesAngleToAdd = (DEFAULT_ONE_INTERVAL_ANGLE_VALUE * minutesAngleToAdd) / 100;
        rightEndPosition += minutesAngleToAdd;

        rightEndPosition = rightEndPosition % 360;
    }

    private void calculateProgressDegree() {
        progressDegree = rightEndPosition - leftStartAngle;
        progressDegree = (progressDegree < 0 ? 360f + progressDegree : progressDegree);
    }

}
