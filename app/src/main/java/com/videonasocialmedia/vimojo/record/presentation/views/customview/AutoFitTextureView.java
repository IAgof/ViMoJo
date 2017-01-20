package com.videonasocialmedia.vimojo.record.presentation.views.customview;

/**
 * Created by alvaro on 16/01/17.
 */

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView extends TextureView {

  private final String LOG_TAG = getClass().getSimpleName();

  private int mRatioWidth = 0;
  private int mRatioHeight = 0;


  public AutoFitTextureView(Context context) {
    this(context, null);
  }

  public AutoFitTextureView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  /**
   * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
   * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
   * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
   *
   * @param width  Relative horizontal size
   * @param height Relative vertical size
   */
  public void setAspectRatio(int width, int height) {
    if (width < 0 || height < 0) {
      throw new IllegalArgumentException("Size cannot be negative.");
    }
    mRatioWidth = width;
    mRatioHeight = height;
    requestLayout();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    // full screen implementation, does not check aspect ratio
    //setMeasuredDimension(width, height);

    if (0 == mRatioWidth || 0 == mRatioHeight) {
      setMeasuredDimension(width, height);
    } else {
      if (width < height * mRatioWidth / mRatioHeight) {
        setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
      } else {
        setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
      }
    }
  }


  /**
   * Study this method, future use different aspect ratio 1:1, 4:3, 16:9
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    Log.d(LOG_TAG, "[onMeasure] Before transforming: " + width + "x" + height);

    int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
    boolean isInHorizontal = Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation;

    int newWidth;
    int newHeight;

    mAspectRatio = width / height;

    Log.d(LOG_TAG, "[onMeasure] Get measured dimensions: " + getMeasuredWidth() + "x" + getMeasuredHeight());

    if (isInHorizontal) {
      newHeight = getMeasuredHeight();
      if (mAspectRatioOneOne) newWidth = getMeasuredHeight();
      else newWidth = (int) (newHeight * mAspectRatio);
    } else {
      newWidth = getMeasuredWidth();
      if (mAspectRatioOneOne) newHeight = getMeasuredWidth();
      else newHeight = (int) (newWidth * mAspectRatio);
    }

    setMeasuredDimension(newWidth, newHeight);
    Log.d(LOG_TAG, "[onMeasure] After transforming: " + getMeasuredWidth() + "x" + getMeasuredHeight());
    }
    */

}
