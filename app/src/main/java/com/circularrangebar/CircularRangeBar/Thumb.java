package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by irina on 1/23/2017.
 */

public class Thumb extends View {

    private static final float MINIMUM_TARGET_RADIUS_DP = 24;

    protected Paint mThumbPaint;
    protected float mThumbRadius;

    protected boolean mThumbPressed = false;
    protected boolean mThumbIsMoving;

    /**
     * Represents the progress mark on the circle, in geometric degrees.
     * This is not provided by the user; it is calculated;
     */
    protected float mThumbPosition;

    protected float[] mPointerPositionXY = new float[2];


    public Thumb(Context context,
                 float pointerRadius, Paint thumbPaint) {
        super(context);

        mThumbRadius = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, pointerRadius);
        mThumbPaint = thumbPaint;
    }

    public void drawThumb(Canvas canvas) {
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mThumbRadius, mThumbPaint);
    }

    boolean isInTargetZone(float x, float y) {

        if (Math.abs(x - mPointerPositionXY[0]) <= mThumbRadius &&
                Math.abs(y - mPointerPositionXY[1]) <= mThumbRadius) {
            return true;
        }
        return false;
    }

    protected void calculatePointerXYPosition(Path circleProgressPath, Path circlePath) {
        PathMeasure pm = new PathMeasure(circleProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
        if (!returnValue) {
            pm = new PathMeasure(circlePath, false);
            returnValue = pm.getPosTan(0, mPointerPositionXY, null);
        }
    }

    protected void calculatePointerXYPosition(Path circleProgressPath) {
        PathMeasure pm = new PathMeasure(circleProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
    }

    public void setmThumbIsMoving(boolean mThumbIsMoving) {
        this.mThumbIsMoving = mThumbIsMoving;
    }

    public void setmThumbPaint(Paint mThumbPaint) {
        this.mThumbPaint = mThumbPaint;
    }

    public void setmThumbPosition(float mThumbPosition) {
        this.mThumbPosition = mThumbPosition;
    }

    public void setmThumbPressed(boolean mThumbPressed) {
        this.mThumbPressed = mThumbPressed;
    }

    public void setmThumbRadius(float mThumbRadius) {
        this.mThumbRadius = mThumbRadius;
    }

    public boolean isThumbPressed() {
        return mThumbPressed;
    }

    public boolean isThumbIsMoving() {
        return mThumbIsMoving;
    }
}
