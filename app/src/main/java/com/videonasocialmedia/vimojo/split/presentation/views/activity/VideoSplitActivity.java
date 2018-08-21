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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;

import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_HIGH;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_LOW;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_MEDIUM;
import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class VideoSplitActivity extends VimojoActivity implements SplitView,
        VideonaPlayer.VideonaPlayerListener, SeekBar.OnSeekBarChangeListener {
    private static final String SPLIT_POSITION = "split_position";
    private static final String SPLIT_VIDEO_POSITION = "split_video_position";

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
    private Video video;
    private int currentSplitPosition = 0;
    private int currentVideoPosition = 0;
    private int startTime = 0;

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

        videonaPlayer.setListener(this);
        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        restoreState(savedInstanceState);
        presenter.init(videoIndexOnTrack);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.updatePresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
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

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentSplitPosition = savedInstanceState.getInt(SPLIT_POSITION, 0);
            currentVideoPosition = savedInstanceState.getInt(SPLIT_VIDEO_POSITION, 0);
        }
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

    public void navigateTo(Class cls) {
        startActivity(new Intent(VimojoApplication.getAppContext(), cls));
    }

    @Override
    public void onBackPressed() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SPLIT_VIDEO_POSITION, videonaPlayer.getCurrentPosition()  );
        outState.putInt(SPLIT_POSITION, currentSplitPosition);
        super.onSaveInstanceState(outState);
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        //finish();
    }

    @OnClick(R.id.player_advance_low_backward_start_split)
    public void onClickAdvanceLowBackwardStart(){
        presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_LOW, currentSplitPosition);
    }

    @OnClick(R.id.player_advance_medium_backward_start_split)
    public void onClickAdvanceMediumBackwardStart(){
      presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_MEDIUM, currentSplitPosition);
    }

    @OnClick(R.id.player_advance_high_backward_start_split)
    public void onClickAdvanceHighBackwardStart(){
      presenter.advanceBackwardStartSplitting(ADVANCE_PLAYER_PRECISION_HIGH, currentSplitPosition);
    }

    @OnClick(R.id.player_advance_low_forward_end_split)
    public void onClickAdvanceLowForwardEnd(){
        presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_LOW, currentSplitPosition);
    }

    @OnClick(R.id.player_advance_medium_forward_end_split)
    public void onClickAdvanceMediumForwardEnd(){
      presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_MEDIUM, currentSplitPosition);
    }

    @OnClick(R.id.player_advance_high_forward_end_split)
    public void onClickAdvanceHighForwardEnd(){
      presenter.advanceForwardEndSplitting(ADVANCE_PLAYER_PRECISION_HIGH, currentSplitPosition);
    }

    @OnClick(R.id.button_split_accept)
    public void onClickSplitAccept() {
        presenter.splitVideo(videoIndexOnTrack, currentSplitPosition);
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_split_cancel)
    public void onClickSplitCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            currentSplitPosition = progress;
            refreshTimeTag(currentSplitPosition);
            videonaPlayer.seekClipToTime(video.getStartTime() + progress);
            videonaPlayer.setSeekBarProgress(progress);
        }
    }

    private void refreshTimeTag(int currentPosition) {
        timeTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(currentPosition + startTime));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void initSplitView(int startTime, int maxSeekBar) {
        splitSeekBar.setMax(maxSeekBar);
        splitSeekBar.setProgress(currentSplitPosition);
        this.startTime = startTime;
        refreshTimeTag(currentSplitPosition);
    }

    @Override
    public void playPreview() {
        videonaPlayer.playPreview();
    }

    @Override
    public void pausePreview() {
        videonaPlayer.pausePreview();
    }

    @Override
    public void showPreview(List<Video> movieList) {
        // (alvaro.martinez) 4/10/17 work on a copy to not modify original one until user accepts text
        video = new Video(movieList.get(0));
        videonaPlayer.initPreviewLists(movieList);
        videonaPlayer.initPreview(currentVideoPosition);
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
    public void showText(String text, String position) {
        videonaPlayer.setImageText(text, position);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }

  @Override
  public void updateSplitSeekbar(int progress) {
    onProgressChanged(splitSeekBar, progress, true);
    splitSeekBar.setProgress(progress);
  }

    @Override
    public void updateProject() {
        presenter.updatePresenter();
    }

}
