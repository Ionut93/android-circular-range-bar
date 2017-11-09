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
    protected Path mProgressPath;
    protected RectF mmTextRectF;
    protected Rect mTextRect = new Rect();
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
    protected String mHourToDisplay;
    protected int mImageSize;
    protected int mHour;
    protected int mMinutes;

    private final Bitmap mImage;
    private final Matrix mMatrix;


    public Thumb(Context context, float pointerRadius, Paint thumbPaint, RectF mmTextRectF) {
        super(context);

        mThumbRadius = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, pointerRadius);
        mThumbPaint = thumbPaint;
        mThumbPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                16, context.getResources().getDisplayMetrics()));
        mThumbPaint.setColor(Color.BLACK);
        mImage = BitmapFactory.decodeResource(context.getResources(), DEFAULT_THUMB_BITMAP);
        mMatrix = new Matrix();
        if (mImage != null) {
            mImageSize = mImage.getWidth() > mImage.getHeight() ? mImage.getWidth() : mImage.getHeight();
        }
        this.mmTextRectF = mmTextRectF;
    }

    public void drawThumb(Canvas canvas, float angle) {

        mMatrix.reset();
        float x = mPointerPositionXY[0] - mImage.getWidth() / 2;
        float y = mPointerPositionXY[1] - mImage.getHeight() / 2;
        mMatrix.preRotate(angle - 270,
                mImage.getWidth() / 2,
                mImage.getHeight() / 2);
        mMatrix.postTranslate(x, y);
        canvas.drawBitmap(mImage, mMatrix, null);
        calculateCurrentmHour();
        mThumbPaint.getTextBounds(mHourToDisplay, 0, mHourToDisplay.length(), mTextRect);
        float xText = mTextPositionXY[0] - mTextRect.width() / 2;
        float yText = mTextPositionXY[1] - mTextRect.height() / 2;
        mMatrix.reset();
        mMatrix.preRotate(angle - 270, mTextRect.width() / 2, mTextRect.height() / 2);
        mMatrix.postTranslate(xText, yText);
        canvas.drawText(String.valueOf(mHourToDisplay), xText, yText, mThumbPaint);
    }

    protected void calculateCurrentmHour() {
        float thumbPosition = mThumbPosition < 360 ? mThumbPosition + 360 : mThumbPosition;
        float min = (mThumbPosition % 15) * MINUTES / 15;
        int currentmHour = (int) ((((thumbPosition - CircularRangeBar.DEFAULT_START_ANGLE) % 360) / 15) + 6) % 24;
        constructmHourString((int) min, currentmHour);
    }

    private void constructmHourString(int mMinutes, int currentmHour) {
        this.mMinutes = mMinutes;
        this.mHour = currentmHour;
        mHourToDisplay = "";
        mHourToDisplay += currentmHour + ":";
        if (mMinutes < 10)
            mHourToDisplay += "0";
        mHourToDisplay += String.valueOf(mMinutes);
    }

    boolean isInTargetZone(float x, float y) {

        if (Math.abs(x - mPointerPositionXY[0]) <= mThumbRadius &&
                Math.abs(y - mPointerPositionXY[1]) <= mThumbRadius) {
            return true;
        }
        return false;
    }

    protected int getmHour() {
        return mHour;
    }

    protected int getmMinutes() {
        return mMinutes;
    }

    protected void calculatePointerXYPosition(Path circlemProgressPath, Path circlePath) {
        PathMeasure pm = new PathMeasure(circlemProgressPath, false);
        mProgressPath = circlemProgressPath;
        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
        if (!returnValue) {
            pm = new PathMeasure(circlePath, false);
            returnValue = pm.getPosTan(0, mPointerPositionXY, null);
        }
    }

    protected void calculateTextPositionXY(Path circleOutsidemProgressPath) {
        PathMeasure pm = new PathMeasure(circleOutsidemProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), mTextPositionXY, null);
    }

    protected void calculatePointerXYPosition(Path circlemProgressPath) {
        mProgressPath = circlemProgressPath;
        PathMeasure pm = new PathMeasure(circlemProgressPath, false);
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
