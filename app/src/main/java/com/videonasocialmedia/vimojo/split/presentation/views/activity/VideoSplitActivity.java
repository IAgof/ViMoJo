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
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
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
    SeekBar.OnSeekBarChangeListener {

    @Inject SplitPreviewPresenter presenter;

    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView(R.id.text_time_split)
    TextView timeTag;
    @BindView(R.id.seekBar_split)
    SeekBar splitSeekBar;
    @BindView(R.id.coordinator_layout_video_split)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.player_advance_low_backward_start_split)
    ImageButton playerAdvanceLowBackwardStartSplit;
    @BindView(R.id.player_advance_medium_backward_start_split)
    ImageButton playerAdvanceMediumBackwardStartSplit;
    @BindView(R.id.player_advance_high_backward_start_split)
    ImageButton playerAdvanceHighBackwardStartSplit;
    @BindView(R.id.player_advance_low_forward_end_split)
    ImageButton playerAdanceLowForwardEndSplit;
    @BindView(R.id.player_advance_medium_forward_end_split)
    ImageButton playerAdanceMediumForwardEndSplit;
    @BindView(R.id.player_advance_high_forward_end_split)
    ImageButton playerAdanceHighForwardEndSplit;

    int videoIndexOnTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_split);
        ButterKnife.bind(this);

        getActivityPresentersComponent().inject(this);
        setupActivityButtons();
        splitSeekBar.setProgress(0);
        splitSeekBar.setOnSeekBarChangeListener(this);
        timeTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(0));

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setupActivityButtons() {
      tintSplitButtons(R.color.button_color_theme_light);
    }

    private void tintSplitButtons(int button_color) {
        tintButton(playerAdvanceLowBackwardStartSplit, button_color);
        tintButton(playerAdvanceMediumBackwardStartSplit, button_color);
        tintButton(playerAdvanceHighBackwardStartSplit, button_color);
        tintButton(playerAdanceLowForwardEndSplit, button_color);
        tintButton(playerAdanceMediumForwardEndSplit, button_color);
        tintButton(playerAdanceHighForwardEndSplit, button_color);
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

    @OnClick(R.id.player_advance_low_backward_start_split)
    public void onClickAdvanceLowBackwardStart(){
        presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_LOW);
    }

    @OnClick(R.id.player_advance_medium_backward_start_split)
    public void onClickAdvanceMediumBackwardStart(){
      presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_MEDIUM);
    }

    @OnClick(R.id.player_advance_high_backward_start_split)
    public void onClickAdvanceHighBackwardStart(){
      presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_HIGH);
    }

    @OnClick(R.id.player_advance_low_forward_end_split)
    public void onClickAdvanceLowForwardEnd(){
        presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_LOW);
    }

    @OnClick(R.id.player_advance_medium_forward_end_split)
    public void onClickAdvanceMediumForwardEnd(){
      presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_MEDIUM);
    }

    @OnClick(R.id.player_advance_high_forward_end_split)
    public void onClickAdvanceHighForwardEnd(){
      presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_HIGH);
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
    public void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }
}
