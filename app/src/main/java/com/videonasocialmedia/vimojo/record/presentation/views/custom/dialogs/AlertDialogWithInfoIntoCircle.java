package com.videonasocialmedia.vimojo.record.presentation.views.custom.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alvaro on 6/06/17.
 */

public class AlertDialogWithInfoIntoCircle extends android.support.v7.app.AlertDialog {

  private final AlertDialog alertDialog;
  private View alertDialogView;

  @Bind(R.id.progressBar_level)
  ProgressBar progressBarLevel;
  @Bind(R.id.text_percent_level)
  TextView percentLevel;
  @Bind(R.id.text_message)
  TextView textMessage;

  public AlertDialogWithInfoIntoCircle(Activity activity, String titleText){
    super(activity);
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.VideonaDialog);
    LayoutInflater inflater = this.getLayoutInflater();
    alertDialogView = inflater.inflate(R.layout.alert_dialog_with_info_into_circle, null);
    builder.setView(alertDialogView);
    ButterKnife.bind(this, alertDialogView);
    builder.setCancelable(true);
    builder.setTitle(titleText);
    builder.setNeutralButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dismiss();
      }
    });
    alertDialog = builder.create();
    alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
  }

  public void show(){
    alertDialog.show();
  }

  public void setPercentLevel(int level) {
    percentLevel.setText(level + "%");
    progressBarLevel.setProgress(level);
  }

  public void setTextMessage(String message) {
    textMessage.setVisibility(View.VISIBLE);
    textMessage.setText(message);
  }

  public void setPercentColor(int colorId){
    getDrawableProgressBar().setColor(colorId);
  }

  private GradientDrawable getDrawableProgressBar() {
    LayerDrawable drawableProgressBar = (LayerDrawable) progressBarLevel
        .getProgressDrawable();
    return (GradientDrawable) drawableProgressBar
        .findDrawableByLayerId(R.id.progressbar_alert_dialog_circular_progress);
  }

}
