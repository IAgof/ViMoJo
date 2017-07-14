package com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.customview;

/**
 * Created by alvaro on 4/07/17.
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.videonasocialmedia.vimojo.R;


public class GoalProgressBar extends View {

  private String TAG = this.getClass().getCanonicalName();

  private Paint progressPaint;
  private int firstGoal;
  private int secondGoal;
  private int progress;

  private float goalIndicatorHeight;
  private float goalIndicatorThickness;
  private int firstGoalNotReachedColor;
  private int firstGoalReachedColor;
  private int secondGoalReachedColor;
  private int unfilledSectionColor;
  private int barThickness;
  private IndicatorType indicatorType;
  private ValueAnimator barAnimator;

  public enum IndicatorType {
    Line, Circle, Square
  }

  public GoalProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    progressPaint = new Paint();
    progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs,
        R.styleable.GoalProgressBar, 0, 0);
    try {
      setGoalIndicatorHeight(typedArray.getDimensionPixelSize(R.styleable
          .GoalProgressBar_goalIndicatorHeight, 10));
      setGoalIndicatorThickness(typedArray.getDimensionPixelSize(R.styleable
          .GoalProgressBar_goalIndicatorThickness, 5));
      setFirstGoalReachedColor(typedArray.getColor(R.styleable
          .GoalProgressBar_firstGoalReachedColor, Color.BLUE));
      setFirstGoalNotReachedColor(typedArray.getColor(R.styleable
          .GoalProgressBar_firstGoalNotReachedColor, Color.BLACK));
      setSecondGoalReachedColor(typedArray.getColor(R.styleable
          .GoalProgressBar_secondGoalReachedColor, Color.YELLOW));
      setUnfilledSectionColor(typedArray.getColor(R.styleable
          .GoalProgressBar_unfilledSectionColor, Color.RED));
      setBarThickness(typedArray.getDimensionPixelOffset(R.styleable
          .GoalProgressBar_barThickness, 4));

      int index = typedArray.getInt(R.styleable.GoalProgressBar_indicatorType, 0);
      setIndicatorType(IndicatorType.values()[index]);
    } finally {
      typedArray.recycle();
    }
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();

    // save our added state - progress and firstGoal
    bundle.putInt("progress", progress);
    bundle.putInt("firstGoal", firstGoal);
    bundle.putInt("secondGoal", secondGoal);

    // save super state
    bundle.putParcelable("superState", super.onSaveInstanceState());

    return bundle;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;

      // restore our added state - progress and firstGoal
      setProgress(bundle.getInt("progress"));
      setFirstGoal(bundle.getInt("firstGoal"));
      setSecondGoal(bundle.getInt("secondGoal"));

      // restore super state
      state = bundle.getParcelable("superState");
    }

    super.onRestoreInstanceState(state);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    int halfHeight = getHeight() / 2;
    int progressEndX = (int) (getWidth() * progress / 100f);

    // draw the filled portion of the bar
    progressPaint.setStrokeWidth(barThickness);
    // TODO:(alvaro.martinez) 5/07/17 Apply logic first or second goal reached
    int color = firstGoalNotReachedColor;
    // int color = (progress >= firstGoal) ? firstGoalReachedColor : firstGoalNotReachedColor;
    if (progress >= firstGoal) {
      if (progress < secondGoal) {
        color = firstGoalReachedColor;
      } else {
        color = secondGoalReachedColor;
      }
    }

    Log.d(TAG, "onDraw progress " + progress + " color " + color);
    progressPaint.setColor(color);
    canvas.drawLine(0, halfHeight, progressEndX, halfHeight, progressPaint);

    // draw the unfilled portion of the bar
    progressPaint.setColor(unfilledSectionColor);
    canvas.drawLine(progressEndX, halfHeight, getWidth(), halfHeight, progressPaint);

    // draw firstGoal indicator
    int indicatorFirstPosition = (int) (getWidth() * firstGoal / 100f);
    if (progress > secondGoal) {
      progressPaint.setColor(secondGoalReachedColor);
    } else {
      progressPaint.setColor(firstGoalReachedColor);
    }
    progressPaint.setStrokeWidth(goalIndicatorThickness);
    switch (indicatorType) {
      case Line:
        canvas.drawLine(
            indicatorFirstPosition,
            halfHeight - (goalIndicatorHeight / 2),
            indicatorFirstPosition,
            halfHeight + (goalIndicatorHeight / 2),
            progressPaint);
        break;
      case Square:
        canvas.drawRect(
            indicatorFirstPosition - (goalIndicatorHeight / 2),
            0,
            indicatorFirstPosition + (goalIndicatorHeight / 2),
            goalIndicatorHeight,
            progressPaint);
        break;
      case Circle:
        canvas.drawCircle(indicatorFirstPosition, halfHeight, halfHeight, progressPaint);
        break;
    }

    int indicatorSecondPosition = (int) (getWidth() * secondGoal / 100f);
    progressPaint.setColor(secondGoalReachedColor);
    progressPaint.setStrokeWidth(goalIndicatorThickness);
    switch (indicatorType) {
      case Line:
        canvas.drawLine(
            indicatorSecondPosition,
            halfHeight - (goalIndicatorHeight / 2),
            indicatorSecondPosition,
            halfHeight + (goalIndicatorHeight / 2),
            progressPaint);
        break;
      case Square:
        canvas.drawRect(
            indicatorSecondPosition - (goalIndicatorHeight / 2),
            0,
            indicatorSecondPosition + (goalIndicatorHeight / 2),
            goalIndicatorHeight,
            progressPaint);
        break;
      case Circle:
        canvas.drawCircle(indicatorSecondPosition, halfHeight, halfHeight, progressPaint);
        break;
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);

    int specHeight = MeasureSpec.getSize(heightMeasureSpec);
    int height;
    switch (MeasureSpec.getMode(heightMeasureSpec)) {

      // be exactly the given specHeight
      case MeasureSpec.EXACTLY:
        height = specHeight;
        break;

      // be at most the given specHeight
      case MeasureSpec.AT_MOST:
        height = (int) Math.min(goalIndicatorHeight, specHeight);
        break;

      // be whatever size you want
      case MeasureSpec.UNSPECIFIED:
      default:
        height = specHeight;
        break;
    }

    // must call this, otherwise the app will crash
    setMeasuredDimension(width, height);
  }

  public void setProgress(int progress) {
    setProgress(progress, true);
  }

  public void setProgress(final int progress, boolean animate) {
    if (animate) {
      barAnimator = ValueAnimator.ofFloat(0, 1);

      barAnimator.setDuration(200);

      // reset progress without animating
      setProgress(0, false);

      barAnimator.setInterpolator(new DecelerateInterpolator());

      barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          float interpolation = (float) animation.getAnimatedValue();
          setProgress((int) (interpolation * progress), false);
        }
      });

      if (!barAnimator.isStarted()) {
        barAnimator.start();
      }
    } else {
      this.progress = progress;
      postInvalidate();
    }
  }

  public void setFirstGoal(int firstGoal) {
    this.firstGoal = firstGoal;
    postInvalidate();
  }

  public void setSecondGoal(int secondGoal) {
    this.secondGoal = secondGoal;
    postInvalidate();
  }

  public void setGoalIndicatorHeight(float goalIndicatorHeight) {
    this.goalIndicatorHeight = goalIndicatorHeight;
    postInvalidate();
  }

  public void setGoalIndicatorThickness(float goalIndicatorThickness) {
    this.goalIndicatorThickness = goalIndicatorThickness;
    postInvalidate();
  }

  public void setFirstGoalReachedColor(int firstGoalReachedColor) {
    this.firstGoalReachedColor = firstGoalReachedColor;
    postInvalidate();
  }

  public void setSecondGoalReachedColor(int secondGoalReachedColor) {
    this.secondGoalReachedColor = secondGoalReachedColor;
    postInvalidate();
  }

  public void setFirstGoalNotReachedColor(int firstGoalNotReachedColor) {
    this.firstGoalNotReachedColor = firstGoalNotReachedColor;
    postInvalidate();
  }

  public void setUnfilledSectionColor(int unfilledSectionColor) {
    this.unfilledSectionColor = unfilledSectionColor;
    postInvalidate();
  }

  public void setBarThickness(int barThickness) {
    this.barThickness = barThickness;
    postInvalidate();
  }

  public void setIndicatorType(IndicatorType indicatorType) {
    this.indicatorType = indicatorType;
    postInvalidate();
  }
}

