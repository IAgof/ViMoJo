package com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.custom.ZoomOutPageTransformer;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment10;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment11;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment1;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment2;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment3;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment4;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment5;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment6;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment7;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment8;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.TutorialRecordFragment9;


public class TutorialRecordActivity extends AppIntro {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addSlide(new TutorialRecordFragment1());
    addSlide(new TutorialRecordFragment2());
    addSlide(new TutorialRecordFragment3());
    addSlide(new TutorialRecordFragment4());
    addSlide(new TutorialRecordFragment5());
    addSlide(new TutorialRecordFragment6());
    addSlide(new TutorialRecordFragment7());
    addSlide(new TutorialRecordFragment8());
    addSlide(new TutorialRecordFragment9());
    addSlide(new TutorialRecordFragment10());
    addSlide(new TutorialRecordFragment11());

    setCustomTransformer(new ZoomOutPageTransformer());

    setBarColor(this.getResources().getColor(R.color.colorTransparent));
    setSeparatorColor(this.getResources().getColor(R.color.colorTransparent));
    showSkipButton(true);
  }

  @Override
  public void onSkipPressed(Fragment currentFragment) {
    super.onSkipPressed(currentFragment);
    finish();
  }

  @Override
  public void onDonePressed(Fragment currentFragment) {
    super.onDonePressed(currentFragment);
    finish();
  }

}
