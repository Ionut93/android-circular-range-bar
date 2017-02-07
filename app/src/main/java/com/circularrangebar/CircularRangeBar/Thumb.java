package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;

import com.circularrangebar.R;

/**
 * Created by irina on 1/23/2017.
 */

public class Thumb extends View {

    private static final float MINIMUM_TARGET_RADIUS_DP = 24;
    private static final int MINUTES = 60;
    private static final int DEFAULT_THUMB_BITMAP = R.drawable.handle;

    protected Paint mThumbPaint;
    protected Path progressPath;
    protected RectF textRectF;
    protected Rect textRect = new Rect();
    protected float mThumbRadius;

    protected boolean mThumbPressed = false;
    protected boolean mThumbIsMoving;

    /**
     * Represents the progress mark on the circle, in geometric degrees.
     * This is not provided by the user; it is calculated;
     */
    protected float mThumbPosition;
    protected float[] mPointerPositionXY = new float[2];
    protected float[] mTextPositionXY = new float[2];
    protected String hourToDisplay;
    protected int mImageSize;
    protected int hour;
    protected int minutes;

    private final Bitmap mImage;
    private final Matrix matrix;


    public Thumb(Context context, float pointerRadius, Paint thumbPaint, RectF textRectF) {
        super(context);

        mThumbRadius = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, pointerRadius);
        mThumbPaint = thumbPaint;
        mThumbPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                16, context.getResources().getDisplayMetrics()));
        mThumbPaint.setColor(Color.BLACK);
        mImage = BitmapFactory.decodeResource(context.getResources(), DEFAULT_THUMB_BITMAP);
        matrix = new Matrix();
        mImageSize = mImage.getWidth() > mImage.getHeight() ? mImage.getWidth() : mImage.getHeight();
        this.textRectF = textRectF;
    }

    public void drawThumb(Canvas canvas, float angle) {

        matrix.reset();
        float x = mPointerPositionXY[0] - mImage.getWidth() / 2;
        float y = mPointerPositionXY[1] - mImage.getHeight() / 2;
        matrix.preRotate(angle - 270,
                mImage.getWidth() / 2,
                mImage.getHeight() / 2);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(mImage, matrix, null);
        calculateCurrentHour();
        mThumbPaint.getTextBounds(hourToDisplay, 0, hourToDisplay.length(), textRect);
        float xText = mTextPositionXY[0] - textRect.width() / 2;
        float yText = mTextPositionXY[1] - textRect.height() / 2;
        matrix.reset();
        matrix.preRotate(angle - 270, textRect.width() / 2, textRect.height() / 2);
        matrix.postTranslate(xText, yText);
        canvas.drawText(String.valueOf(hourToDisplay), xText, yText, mThumbPaint);
    }

    protected void calculateCurrentHour() {
        float thumbPosition = mThumbPosition < 360 ? mThumbPosition + 360 : mThumbPosition;
        float min = (mThumbPosition % 15) * MINUTES / 15;
        int currentHour = (int) ((((thumbPosition - CircularRangeBar.DEFAULT_START_ANGLE) % 360) / 15) + 6) % 24;
        constructHourString((int) min, currentHour);
    }

    private void constructHourString(int minutes, int currentHour) {
        this.minutes = minutes;
        this.hour = currentHour;
        hourToDisplay = "";
        hourToDisplay += currentHour + ":";
        if (minutes < 10)
            hourToDisplay += "0";
        hourToDisplay += String.valueOf(minutes);
    }

    boolean isInTargetZone(float x, float y) {

        if (Math.abs(x - mPointerPositionXY[0]) <= mThumbRadius &&
                Math.abs(y - mPointerPositionXY[1]) <= mThumbRadius) {
            return true;
        }
        return false;
    }

    protected int getHour() {
        return hour;
    }

    protected int getMinutes() {
        return minutes;
    }

    protected void calculatePointerXYPosition(Path circleProgressPath, Path circlePath) {
        PathMeasure pm = new PathMeasure(circleProgressPath, false);
        progressPath = circleProgressPath;
        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
        if (!returnValue) {
            pm = new PathMeasure(circlePath, false);
            returnValue = pm.getPosTan(0, mPointerPositionXY, null);
        }
    }

    protected void calculateTextPositionXY(Path circleOutsideProgressPath) {
        PathMeasure pm = new PathMeasure(circleOutsideProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), mTextPositionXY, null);
    }

    protected void calculatePointerXYPosition(Path circleProgressPath) {
        progressPath = circleProgressPath;
        PathMeasure pm = new PathMeasure(circleProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
    }

    public void setThumbIsMoving(boolean mThumbIsMoving) {
        this.mThumbIsMoving = mThumbIsMoving;
    }

    public void setThumbPaint(Paint mThumbPaint) {
        this.mThumbPaint = mThumbPaint;
    }

    public void setThumbPosition(float mThumbPosition) {
        this.mThumbPosition = mThumbPosition;
    }

    public void setThumbPressed(boolean mThumbPressed) {
        this.mThumbPressed = mThumbPressed;
    }

    public void setThumbRadius(float mThumbRadius) {
        this.mThumbRadius = mThumbRadius;
    }

    public float getThumbPosition() {
        return mThumbPosition;
    }

    public boolean isThumbPressed() {
        return mThumbPressed;
    }

    public boolean isThumbIsMoving() {
        return mThumbIsMoving;
    }
}
