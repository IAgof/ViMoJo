package com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.custom.ZoomOutPageTransformer;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment10TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment11TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment1TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment2TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment3TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment4TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment5TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment6TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment7TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment8TutorialRecord;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_record.Fragment9TutorialRecord;


public class TutorialRecordVimojo extends AppIntro {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addSlide(new Fragment1TutorialRecord());
    addSlide(new Fragment2TutorialRecord());
    addSlide(new Fragment3TutorialRecord());
    addSlide(new Fragment4TutorialRecord());
    addSlide(new Fragment5TutorialRecord());
    addSlide(new Fragment6TutorialRecord());
    addSlide(new Fragment7TutorialRecord());
    addSlide(new Fragment8TutorialRecord());
    addSlide(new Fragment9TutorialRecord());
    addSlide(new Fragment10TutorialRecord());
    addSlide(new Fragment11TutorialRecord());

    setCustomTransformer(new ZoomOutPageTransformer());

    setBarColor(Color.parseColor("#00000000"));
    setSeparatorColor(Color.parseColor("#00000000"));
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
