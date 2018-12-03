/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.utils.dialog.transcoding;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.videonasocialmedia.vimojo.R;

/**
 * Created by alvaro on 27/11/18.
 */

public class TranscodingDialog extends Dialog {

  private Context contex;
  private String title;
  private String message;
  private AdView adView;
  private View exportDialogView;
  private TextView transcodeTitle;
  private TextView transcodingMessage;
  private OnCancelListener listener;


  public TranscodingDialog(@NonNull Context context, int themeResId, String title,
                           String message, @Nullable OnCancelListener cancelListener) {
    super(context, themeResId);
    this.contex = context;
    this.title = title;
    this.message = message;
    this.listener = cancelListener;
    initComponentsDialog();
  }

  private void initComponentsDialog() {
    LayoutInflater dialogLayout = LayoutInflater.from(contex);
    exportDialogView = dialogLayout.inflate(R.layout.dialog_progress_transcoding, null);

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(this.getWindow().getAttributes());
    lp.width = (int) (contex.getResources().getDisplayMetrics().widthPixels * 0.90);
    lp.height = (int) (contex.getResources().getDisplayMetrics().heightPixels * 0.65);
    this.getWindow().setAttributes(lp);

    transcodeTitle = (TextView) exportDialogView.findViewById(R.id.transcodingDialogTitle);
    transcodeTitle.setText(title);
    transcodingMessage = (TextView) exportDialogView.findViewById(R.id.transcodingDialogMessage);
    transcodingMessage.setText(message);

    Button cancel = (Button) exportDialogView.findViewById(R.id.cancel_btn);
    cancel.setOnClickListener(v -> {
      listener.onCancel(this);
      this.dismiss();
    });
    this.setCancelable(false);
    this.setCanceledOnTouchOutside(false);
  }

  public void updateTranscodingMessage(String message) {
    transcodingMessage.setText(message);
  }

  public void showAds() {
    adView = exportDialogView.findViewById(R.id.adView);
    adView.loadAd(new AdRequest.Builder().build());
  }

  public void hideAds() {
    CardView adsCardView = exportDialogView.findViewById(R.id.adsCardView);
    adsCardView.setVisibility(View.GONE);
  }

  public void showTranscodingDialog() {
    this.setContentView(exportDialogView);
    this.show();
  }

  public void hide() {
    this.dismiss();
  }
}
