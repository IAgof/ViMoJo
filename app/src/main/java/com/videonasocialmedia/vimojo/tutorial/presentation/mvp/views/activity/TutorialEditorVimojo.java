package com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.custom.ZoomOutPageTransformer;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment10TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment11TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment1TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment2TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment3TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment4TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment5TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment6TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment7TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment8TutorialEditor;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.fragments.fragment_tutorial_editor.Fragment9TutorialEditor;


public class TutorialEditorVimojo extends AppIntro {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addSlide(new Fragment1TutorialEditor());
    addSlide(new Fragment2TutorialEditor());
    addSlide(new Fragment3TutorialEditor());
    addSlide(new Fragment4TutorialEditor());
    addSlide(new Fragment5TutorialEditor());
    addSlide(new Fragment6TutorialEditor());
    addSlide(new Fragment7TutorialEditor());
    addSlide(new Fragment8TutorialEditor());
    addSlide(new Fragment9TutorialEditor());
    addSlide(new Fragment10TutorialEditor());
    addSlide(new Fragment11TutorialEditor());

    setCustomTransformer(new ZoomOutPageTransformer());

    setBarColor(Color.parseColor("#00000000"));
    setSeparatorColor(Color.parseColor("#00000000"));
    showSkipButton(true);
  }

  @Override
  public void onSkipPressed(Fragment currentFragment) {
    super.onSkipPressed(currentFragment);
    loadMainActivity();
  }

  @Override
  public void onDonePressed(Fragment currentFragment) {
    super.onDonePressed(currentFragment);
    loadMainActivity();
  }


  private void loadMainActivity() {
    Intent intent = new Intent(this, RecordCamera2Activity.class);
    startActivity(intent);
  }

}
