package com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.custom.ZoomOutPageTransformer;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment10;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment11;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment1;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment2;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment3;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment4;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment5;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment6;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment7;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment8;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.TutorialEditorFragment9;


public class TutorialEditorActivity extends AppIntro {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addSlide(new TutorialEditorFragment1());
    addSlide(new TutorialEditorFragment2());
    addSlide(new TutorialEditorFragment3());
    addSlide(new TutorialEditorFragment4());
    addSlide(new TutorialEditorFragment5());
    addSlide(new TutorialEditorFragment6());
    addSlide(new TutorialEditorFragment7());
    addSlide(new TutorialEditorFragment8());
    addSlide(new TutorialEditorFragment9());
    addSlide(new TutorialEditorFragment10());
    addSlide(new TutorialEditorFragment11());

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
