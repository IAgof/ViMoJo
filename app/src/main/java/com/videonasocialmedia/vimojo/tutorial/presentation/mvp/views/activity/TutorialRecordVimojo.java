package com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
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
    setDoneText(getResources().getText(R.string.tutorial_record_done_button));
    showSkipButton(true);
  }

  @Override
  public void onSkipPressed(Fragment currentFragment) {
    super.onSkipPressed(currentFragment);
    navigateTo(RecordCamera2Activity.class);
  }

  private void navigateTo(Class cls) {
    Intent intent = new Intent(this, cls);
    startActivity(intent);
  }

  @Override
  public void onDonePressed(Fragment currentFragment) {
    super.onDonePressed(currentFragment);
    navigateTo(TutorialEditorVimojo.class);
  }



  @Override
  public void setProgressButtonEnabled(boolean progressButtonEnabled) {

    this.progressButtonEnabled = progressButtonEnabled;
    if (progressButtonEnabled) {

      if ((!isRtl() && pager.getCurrentItem() == slidesNumber - 1) || (isRtl() && pager.getCurrentItem() == 0)) {
        setButtonState(nextButton, false);
        setButtonState(doneButton, true);
        if (isWizardMode) {
          setButtonState(backButton, showBackButtonWithDone);
        } else {
          setButtonState(skipButton, true);
        }

      } else {
        setButtonState(nextButton, true);
        setButtonState(doneButton, false);
        if (isWizardMode) {
          if (pager.getCurrentItem() == 0) {
            setButtonState(backButton, false);
          } else {
            setButtonState(backButton, isWizardMode);
          }
        } else {
          setButtonState(skipButton, skipButtonEnabled);
        }

      }
    } else {
      setButtonState(nextButton, false);
      setButtonState(doneButton, false);
      setButtonState(backButton, false);
      setButtonState(skipButton, false);
    }
  }
}
