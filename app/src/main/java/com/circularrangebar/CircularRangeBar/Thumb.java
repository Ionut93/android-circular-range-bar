package com.circularrangebar.CircularRangeBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.circularrangebar.R;

/**
 * Created by irina on 1/23/2017.
 */

public class Thumb extends View {

    private static final float MINIMUM_TARGET_RADIUS_DP = 24;
    protected static final int DEFAULT_THUMB_BITMAP = R.drawable.thumb;

    protected Paint mThumbPaint;
    private Rect textRect = new Rect();
    protected float mThumbRadius;

    protected boolean mThumbPressed = false;
    protected boolean mThumbIsMoving;

    /**
     * Represents the progress mark on the circle, in geometric degrees.
     * This is not provided by the user; it is calculated;
     */
    protected float mThumbPosition;
    protected float[] mPointerPositionXY = new float[2];
    protected int currentHour;

    protected int mImageSize;

    private final Bitmap mImage;
    private final Matrix matrix;


    public Thumb(Context context, float pointerRadius, Paint thumbPaint) {
        super(context);

        mThumbRadius = (int) Math.max(MINIMUM_TARGET_RADIUS_DP, pointerRadius);
        mThumbPaint = thumbPaint;
        mThumbPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, context.getResources().getDisplayMetrics()));
        mImage = BitmapFactory.decodeResource(context.getResources(), DEFAULT_THUMB_BITMAP);
        matrix = new Matrix();
        mImageSize = mImage.getWidth() > mImage.getHeight() ? mImage.getWidth() : mImage.getHeight();
    }

    public void drawThumb(Canvas canvas, float angle) {

        matrix.reset();
        float x = mPointerPositionXY[0] - mImage.getWidth() / 2;
        float y = mPointerPositionXY[1] -  mImage.getHeight() / 2;
        matrix.preRotate(angle - 270,
                mImage.getWidth() / 2,
                mImage.getHeight() / 2);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(mImage, matrix, null);
        calculateCurrentHour();
        mThumbPaint.getTextBounds(String.valueOf(currentHour), 0, String.valueOf(currentHour).length(), textRect);
        float xText = mPointerPositionXY[0] - textRect.centerX();
        float yText = mPointerPositionXY[1] - textRect.centerY();
        matrix.reset();
        matrix.preRotate(angle - 270, textRect.width() / 2, textRect.height() / 2);
        matrix.postTranslate(xText, yText);
        canvas.drawText(String.valueOf(currentHour), xText, yText, mThumbPaint);
    }

    protected void calculateCurrentHour() {
        currentHour = (int) Math.ceil(mThumbPosition - CircularRangeBar.DEFAULT_START_ANGLE) / 15 + 6;
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

    public float getmThumbPosition() {
        return mThumbPosition;
    }

    public boolean isThumbPressed() {
        return mThumbPressed;
    }

    public boolean isThumbIsMoving() {
        return mThumbIsMoving;
    }
}
