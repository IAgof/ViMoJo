package com.videonasocialmedia.vimojo.utils;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v13.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jliarte on 7/11/17.
 */

@Keep
public class MoveUpBehaviour extends CoordinatorLayout.Behavior<View> {
  public MoveUpBehaviour() {
    super();
  }

  public MoveUpBehaviour(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
    return dependency instanceof Snackbar.SnackbarLayout;
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
    float translationY = Math.min(0, ViewCompat.getTranslationY(dependency)
            - dependency.getHeight());

    //Dismiss last SnackBar immediately to prevent from conflict when showing SnackBars immediately
    // after eachother
    ViewCompat.animate(child).cancel();

    //Move entire child layout up that causes objects on top disappear
    ViewCompat.setTranslationY(child, translationY);

    // Set top padding to child layout to reappear missing objects
    // If you had set padding to child in xml, then you have to set them here
    // by <child.getPaddingLeft(), ...>
    child.setPadding(0, -Math.round(translationY), 0, 0);

    return true;
  }

  @Override
  public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
    //Reset paddings and translationY to its default
    child.setPadding(0, 0, 0, 0);
    ViewCompat.animate(child).translationY(0).start();
  }
}
