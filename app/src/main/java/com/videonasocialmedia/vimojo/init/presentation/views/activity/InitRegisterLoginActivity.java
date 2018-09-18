/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.init.presentation.views.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.init.presentation.mvp.presenters.InitRegisterLoginPresenter;
import com.videonasocialmedia.vimojo.init.presentation.mvp.views.InitRegisterLoginView;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 24/8/18.
 */

public class InitRegisterLoginActivity extends VimojoActivity implements InitRegisterLoginView {

  @Inject InitRegisterLoginPresenter presenter;

  @BindView(R.id.linear_layout_buttons_register_login)
  LinearLayout linearLayoutButtonsRegisterLogin;
  @BindView(R.id.video_view_init_looper)
  VideoView videoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);

    getActivityPresentersComponent().inject(this);

    //remove title, mode fullscreen
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_init_register_login);
    ButterKnife.bind(this);

  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
    System.exit(0);
  }

  @Override
  public void onResume() {
    super.onResume();
    presenter.init();
  }

  @OnClick (R.id.button_init_login)
  public void onClickLogin() {
    presenter.performLogin(this);
  }

  @OnClick (R.id.button_init_register)
  public void onClickRegister() {
    presenter.performRegister(this);
  }

  @Override
  public void showErrorAuth0(int errorAuth0) {
    Snackbar.make(linearLayoutButtonsRegisterLogin, errorAuth0 ,Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void navigateToRecordCamera2() {
    finish();
    Intent intent = new Intent(VimojoApplication.getAppContext(), RecordCamera2Activity.class);
    startActivity(intent);
  }

  @Override
  public void setVideoOnLoop(Uri videoUri) {
    videoView.setVideoURI(videoUri);
    videoView.start();
    videoView.setOnCompletionListener(mp -> videoView.start());
  }

  @Override
  public void pauseVideo() {
    videoView.pause();
  }
}
