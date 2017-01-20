package com.circularrangebar.Views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.circularrangebar.R;

/**
 * Created by irina on 1/20/2017.
 */

public class SeekBar extends View {

    private int maxValue = 100;
    private int currentValue = 0;
    private float valueToDraw;

    //Styleable attributes
    private int barHeight;
    private int circleRadius;
    private int spaceAfterBar;
    private int circleTextSize;
    private int maxValueTextSize;
    private int labelTextSize;
    private int labelTextColor;
    private int currentValueTextColor;
    private int circleTextColor;
    private int baseColor;
    private int fillColor;
    private String labelText;

    private Paint labelPaint;
    private Paint maxValuePaint;
    private Paint barBasePaint;
    private Paint barFillPaint;
    private Paint circlePaint;
    private Paint currentValuePaint;

    //Animation
    private boolean animated;
    private long animationDuration = 1000;
    ValueAnimator animation = null;

    private Rect maxValueRect;
    private RectF barRect;
    private RectF thumbRect;
    private RectF thumbRightRect;
    private boolean thumbIsPressed = false;
    private boolean thumbRightIsPressed = false;
    private float currentRightValue = -1;
    private float barLength;


    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(context, attrs);
        initializePaintObjects();
        setSaveEnabled(true);
        animated = true;
        thumbRect = new RectF();
        thumbRightRect = new RectF();
    }

    private void initializeAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ValueBar, 0, 0);
        try {
            barHeight = ta.getDimensionPixelSize(R.styleable.ValueBar_barHeight, 0);
            circleRadius = ta.getDimensionPixelSize(R.styleable.ValueBar_circleRadius, 0);
            spaceAfterBar = ta.getDimensionPixelSize(R.styleable.ValueBar_spaceAfterBar, 0);
            circleTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_circleTextSize, 0);
            maxValueTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_maxValueTextSize, 0);
            labelTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_labelTextSize, 0);
            labelTextColor = ta.getColor(R.styleable.ValueBar_labelTextColor, Color.BLACK);
            currentValueTextColor = ta.getColor(R.styleable.ValueBar_maxValueTextColor, Color.BLACK);
            circleTextColor = ta.getColor(R.styleable.ValueBar_circleTextColor, Color.BLACK);
            baseColor = ta.getColor(R.styleable.ValueBar_baseColor, Color.BLACK);
            fillColor = ta.getColor(R.styleable.ValueBar_fillColor, Color.BLACK);
            labelText = ta.getString(R.styleable.ValueBar_labelText);


        } finally {
            ta.recycle();
        }
    }

    private void initializePaintObjects() {
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setColor(labelTextColor);
        labelPaint.setTextAlign(Paint.Align.LEFT);
        labelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        maxValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxValuePaint.setTextSize(maxValueTextSize);
        maxValuePaint.setColor(currentValueTextColor);
        maxValuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        maxValuePaint.setTextAlign(Paint.Align.RIGHT);

        barBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBasePaint.setColor(baseColor);

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setColor(fillColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(fillColor);

        currentValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentValuePaint.setTextSize(circleTextSize);
        currentValuePaint.setColor(circleTextColor);
        currentValuePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int size = getPaddingTop() + getPaddingBottom();
        size += labelPaint.getFontSpacing();
        float maxValuteTextSpacing = maxValuePaint.getFontSpacing();
        size += Math.max(maxValuteTextSpacing, Math.max(barHeight, circleRadius * 2));
        return resolveSizeAndState(size, measureSpec, 0);
    }

    private int measureWidth(int measureSpec) {
        int size = getPaddingLeft() + getPaddingRight();
        Rect bounds = new Rect();
        labelPaint.getTextBounds(labelText, 0, labelText.length(), bounds);
        size += bounds.width();
        bounds = new Rect();
        String maxValueText = String.valueOf(maxValue);
        maxValuePaint.getTextBounds(maxValueText, 0, maxValueText.length(), bounds);
        size += bounds.width();
        return resolveSizeAndState(size, measureSpec, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxValueRect = new Rect();
        String maxValueString = String.valueOf(maxValue);
        maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length(), maxValueRect);
        barLength = getWidth() - getPaddingRight() - getPaddingLeft()
                - circleRadius - maxValueRect.width() - spaceAfterBar;
        if (currentRightValue == -1)
            currentRightValue = barLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //drawLabel(canvas);
        drawBar(canvas);
        drawMaxValue(canvas);
    }

    private void drawBar(Canvas canvas) {


        float barCenter = getBarCenterYPostion();

        float halfBarHeight = barHeight / 2;
        // Position's where to draw
        float top = barCenter - halfBarHeight;
        float bottom = barCenter + halfBarHeight;
        float left = getPaddingLeft();
        float right = getPaddingLeft() + barLength;

        barRect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(barRect, halfBarHeight, halfBarHeight, barBasePaint);
        //above drawn the full empty bar

        float percentFilled = (float) valueToDraw / (float) maxValue;
        float fillLength = barLength * percentFilled;
        float fillPosition = left + fillLength;
        float fullFillPosition = left + currentRightValue;
        RectF fillRect = new RectF(fillPosition, top, fullFillPosition, bottom);
        canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint);

        // above drawn the filled bar
        canvas.drawCircle(fillPosition, barCenter, circleRadius, circlePaint);
        canvas.drawCircle(fullFillPosition, barCenter, circleRadius, circlePaint);


        thumbRect.set(fillPosition, barCenter - circleRadius, fillPosition + 2 * circleRadius, barCenter + circleRadius);
        thumbRightRect.set(fullFillPosition, barCenter - circleRadius, fullFillPosition + 2 * circleRadius, barCenter + circleRadius);
        Rect valueBounds = new Rect();
        String valueString = String.valueOf(Math.round(valueToDraw));
        currentValuePaint.getTextBounds(valueString, 0, valueString.length(), valueBounds);
        float y = getPaddingTop() + valueBounds.height();
        canvas.drawText(valueString, fillPosition, y, currentValuePaint);
    }

    private void drawMaxValue(Canvas canvas) {
        String maxValue = String.valueOf(this.maxValue);
        Rect maxValueRect = new Rect();
        maxValuePaint.getTextBounds(maxValue, 0, maxValue.length(), maxValueRect);
        float xPos = getWidth() - getPaddingRight();
        float yPos = getBarCenterYPostion() + maxValueRect.height() / 2;
        canvas.drawText(maxValue, xPos, yPos, maxValuePaint);
    }

    private float getBarCenterYPostion() {
        float barCenter = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        barCenter += getPaddingTop() + .1f * getHeight();
        return barCenter;
    }

    private void drawLabel(Canvas canvas) {
        float x = getPaddingLeft();
        Rect bounds = new Rect();
        labelPaint.getTextBounds(labelText, 0, labelText.length(), bounds);
        float y = getPaddingTop() + bounds.height();
        canvas.drawText(labelText, x, y, labelPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (thumbRect.contains(x, y))
                    thumbIsPressed = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (thumbIsPressed)
                    thumbIsPressed = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX());
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void onActionMove(float x) {
        if (thumbIsPressed) {
            moveThumb(x);
            invalidate();
        }
    }

    private void moveThumb(float x) {
        float valueToMove = x - thumbRect.centerX();
  /*          if (valueToMove > 0)
                valueToMove += circleRadius;
            else
                valueToMove -= circleRadius;*/
        float barLength = getWidth() - getPaddingRight() - getPaddingLeft()
                - circleRadius - maxValueRect.width() - spaceAfterBar;
        int valueModifier = (int) ((maxValue * valueToMove) / barLength);
        currentValue += valueModifier;
        if (currentValue > maxValue)
            currentValue = maxValue;
        else if (currentValue < 0)
            currentValue = 0;
          /*  thumbRect.right += valueToMove;
            thumbRect.left += valueToMove;*/
        setValue(currentValue);

    }

    public void setValue(int newValue) {
        int previousValue = currentValue;
        if (newValue < 0) {
            currentValue = 0;
        } else if (newValue > maxValue) {
            currentValue = maxValue;
        } else {
            currentValue = newValue;
        }

        if (animation != null)
            animation.cancel();

        if (animated) {
            animation = ValueAnimator.ofFloat(previousValue, currentValue);
            animation.setInterpolator(new DecelerateInterpolator());
           /* int changeInValue = Math.abs(currentValue - previousValue);
            long durationToUse = (long) (animationDuration * ((float) changeInValue / (float) maxValue));
            animation.setDuration(durationToUse);*/
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    valueToDraw = (float) animation.getAnimatedValue();
                    SeekBar.this.invalidate();
                }
            });

            animation.start();
        } else {
            valueToDraw = currentValue;
        }
        invalidate();
    }

    public void setCurrentValue(int newValue) {
        if (newValue < 0) {
            currentValue = 0;
        } else if (newValue > maxValue) {
            currentValue = maxValue;
        } else {
            currentValue = newValue;
        }
        valueToDraw = currentValue;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
        requestLayout();
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentValue = currentValue;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentValue = ss.currentValue;
        valueToDraw = currentValue;
    }

    static class SavedState extends BaseSavedState {

        int currentValue;

        public SavedState(Parcelable superstate) {
            super(superstate);
        }

        private SavedState(Parcel in) {
            super(in);
            currentValue = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentValue);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
