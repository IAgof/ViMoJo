package com.videonasocialmedia.vimojo.split.presentation.views.activity;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_HIGH;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_LOW;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_MEDIUM;
import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class VideoSplitActivity extends VimojoActivity implements SplitView,
    SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener,
    VideonaPlayer.VideonaPlayerListener {

    @Inject SplitPreviewPresenter presenter;

    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView(R.id.text_time_split)
    TextView timeTag;
    @BindView(R.id.seekBar_split)
    SeekBar splitSeekBar;
    @BindView(R.id.coordinator_layout_video_split)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.radio_group_split_advance)
    RadioGroup radioGroupSplit;
    @BindView(R.id.radio_button_split_advance_low)
    RadioButton buttonSelectAdvanceLow;
    @BindView(R.id.radio_button_split_advance_medium)
    RadioButton buttonSelectAdvanceMedium;
    @BindView(R.id.radio_button_split_advance_high)
    RadioButton buttonSelectAdvanceHigh;
    @BindView(R.id.player_advance_backward_split)
    ImageButton playerAdvanceBackwardSplit;
    @BindView(R.id.player_advance_forward_split)
    ImageButton playerAdvanceForwardSplit;

    int videoIndexOnTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_split);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        splitSeekBar.setProgress(0);
        splitSeekBar.setOnSeekBarChangeListener(this);
        timeTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(0));

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        radioGroupSplit.setOnCheckedChangeListener(this);
        buttonSelectAdvanceMedium.setChecked(true);
        setupActivityViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updatePresenter(videoIndexOnTrack);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pausePresenter();
    }

  private void setupActivityViews() {
    presenter.setupActivityViews();
  }

    private void tintSplitButtons(int button_color) {
        tintButton(playerAdvanceBackwardSplit, button_color);
        tintButton(playerAdvanceForwardSplit, button_color);
    }

    private void tintDurationTag(int color) {
        timeTag.setTextColor(getResources().getColor(color));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        presenter.cancelSplit();
    }

    @OnClick(R.id.button_split_accept)
    public void onClickSplitAccept() {
        presenter.splitVideo();
    }

    @OnClick(R.id.button_split_cancel)
    public void onClickSplitCancel() {
        presenter.cancelSplit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            presenter.onSeekBarChanged(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void initSplitView(int maxSeekBar) {
        splitSeekBar.setMax(maxSeekBar);
    }

    @Override
    public void attachView(Context context) {
        videonaPlayer.attachView(context);
    }

    @Override
    public void detachView() {
        videonaPlayer.detachView();
    }

    @Override
    public void setVideonaPlayerListener() {
      videonaPlayer.setVideonaPlayerListener(this);
    }

  @Override
    public void initSingleClip(VMComposition vmComposition, int clipPosition) {
        videonaPlayer.initSingleClip(vmComposition, clipPosition);
    }

    @Override
    public void seekTo(int timeInMsec) {
        videonaPlayer.seekTo(timeInMsec);
    }

    @Override
    public void setAspectRatioVerticalVideos(int height) {
        videonaPlayer.setAspectRatioVerticalVideos(height);
    }

    @Override
    public void showError(int stringResourceId) {
        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, stringResourceId,
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        });
    }

  @Override
  public void updateSplitSeekbar(int progress) {
    splitSeekBar.setProgress(progress);
  }

    @Override
    public void refreshTimeTag(int currentPosition) {
        timeTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(currentPosition));
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter(videoIndexOnTrack);
    }

    @Override
    public void updateViewToThemeDark() {
        tintSplitButtons(R.color.button_trim_color_theme_dark);
        tintDurationTag(R.color.textColorDark);
    }

    @Override
    public void updateViewToThemeLight() {
        tintSplitButtons(R.color.button_trim_color_theme_light);
        tintDurationTag(R.color.textColorLight);
    }

    @Override
    public void updateRadioButtonToThemeDark(RadioButton radioButton) {
        radioButton.setTextColor(ContextCompat.getColorStateList(this,
            R.color.button_trim_color_theme_dark));
    }

    @Override
    public void updateRadioButtonToThemeLight(RadioButton radioButton) {
        radioButton.setTextColor(ContextCompat.getColorStateList(this,
            R.color.button_trim_color_theme_light));
    }

    @Override
    public void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @OnClick(R.id.player_advance_backward_split)
    public void onClickAdvanceBackward(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_LOW);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_MEDIUM);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_HIGH);
        }
    }

    @OnClick(R.id.player_advance_forward_split)
    public void onClickAdvanceForward(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_LOW);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_MEDIUM);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_HIGH);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (buttonSelectAdvanceLow.isChecked()) {
            showPlayerAdvanceLow();
        }
        if(buttonSelectAdvanceMedium.isChecked()) {
            showPlayerAdvanceMedium();
        }
        if(buttonSelectAdvanceHigh.isChecked()) {
            showPlayerAdvanceHigh();
        }
        updateRadioButtons();
    }

    private void updateRadioButtons() {
        presenter.updateRadioButtonsWithTheme(buttonSelectAdvanceLow, buttonSelectAdvanceMedium,
            buttonSelectAdvanceHigh);
    }

    private void showPlayerAdvanceLow() {
        playerAdvanceBackwardSplit.setImageResource
            (R.drawable.activity_edit_player_advance_low);
        playerAdvanceForwardSplit.setImageResource
            (R.drawable.activity_edit_player_advance_low);
    }

    private void showPlayerAdvanceMedium() {
        playerAdvanceBackwardSplit.setImageResource
            (R.drawable.activity_edit_player_advance_medium);
        playerAdvanceForwardSplit.setImageResource
            (R.drawable.activity_edit_player_advance_medium);
    }

    private void showPlayerAdvanceHigh() {
        playerAdvanceBackwardSplit.setImageResource
            (R.drawable.activity_edit_player_advance_high);
        playerAdvanceForwardSplit.setImageResource
            (R.drawable.activity_edit_player_advance_high);
    }

  @Override
  public void newClipPlayed(int currentClipIndex) {
    // Do nothing
  }

  @Override
  public void playerReady() {
    // Do nothing
  }

  @Override
  public void updatedSeekbarProgress(int progress) {
    splitSeekBar.setProgress(progress);
    refreshTimeTag(progress);
  }
}
