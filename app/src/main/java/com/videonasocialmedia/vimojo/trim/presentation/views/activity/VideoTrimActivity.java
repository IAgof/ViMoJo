package com.videonasocialmedia.vimojo.trim.presentation.views.activity;
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
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_HIGH;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_LOW;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_MEDIUM;
import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class VideoTrimActivity extends VimojoActivity implements TrimView,
        RangeSeekBar.OnRangeSeekBarChangeListener, VideonaPlayer.VideonaPlayerListener, RadioGroup.OnCheckedChangeListener {

    @Inject TrimPreviewPresenter presenter;

    @BindView(R.id.text_time_trim)
    TextView durationTag;
    @BindView(R.id.trim_rangeSeekBar)
    RangeSeekBar trimmingRangeSeekBar;
    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView(R.id.player_advance_backward_start_trim)
    ImageButton playerAdvanceBackwardStartTrim;
    @BindView(R.id.player_advance_forward_start_trim)
    ImageButton playerAdvanceForwardStartTrim;
    @BindView(R.id.player_advance_backward_end_trim)
    ImageButton playerAdvanceBackwardEndTrim;
    @BindView(R.id.player_advance_forward_end_trim)
    ImageButton playerAdvanceForwardEndTrim;

    @BindView(R.id.radio_group_trim_advance)
    RadioGroup radioGroupAdvanceTrim;
    @BindView(R.id.radio_button_trim_advance_low)
    RadioButton buttonSelectAdvanceLow;
    @BindView(R.id.radio_button_trim_advance_medium)
    RadioButton buttonSelectAdvanceMedium;
    @BindView(R.id.radio_button_trim_advance_high)
    RadioButton buttonSelectAdvanceHigh;

    int videoIndexOnTrack;

    private Video video;
    private int videoDuration = 1;
    private int startTimeMs = 0;
    private int finishTimeMs = 100;
    private String TAG = "VideoTrimActivity";
    private float seekBarMinPosition = 0f;
    private float seekBarMaxPosition = 1f;
    private int currentPosition = 0;
    private String VIDEO_POSITION = "video_position";
    private String START_TIME_TAG = "start_time_tag";
    private String STOP_TIME_TAG = "stop_time_tag";
    private boolean stateWasRestored = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trim);
        ButterKnife.bind(this);

        this.getActivityPresentersComponent().inject(this);
        trimmingRangeSeekBar.setOnRangeSeekBarChangeListener(this);
        trimmingRangeSeekBar.setNotifyWhileDragging(true);
        videonaPlayer.setListener(this);
        radioGroupAdvanceTrim.setOnCheckedChangeListener(this);
        buttonSelectAdvanceMedium.setChecked(true);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        restoreState(savedInstanceState);
        setupActivityViews();
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
        stateWasRestored = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
    }

    private void setupActivityViews() {
        presenter.setupActivityViews();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(VIDEO_POSITION, 0);
            startTimeMs = savedInstanceState.getInt(START_TIME_TAG);
            finishTimeMs = savedInstanceState.getInt(STOP_TIME_TAG);
            stateWasRestored = true;
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
        finish();
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
      //  finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VIDEO_POSITION, videonaPlayer.getCurrentPosition());
        outState.putInt(START_TIME_TAG, startTimeMs);
        outState.putInt(STOP_TIME_TAG, finishTimeMs);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.player_advance_backward_start_trim)
    public void onClickAdvanceLowBackwardStart(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceBackwardStartTrimming(ADVANCE_PLAYER_PRECISION_LOW, startTimeMs);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceBackwardStartTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM, startTimeMs);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceBackwardStartTrimming(ADVANCE_PLAYER_PRECISION_HIGH, startTimeMs);
        }
    }

    @OnClick(R.id.player_advance_forward_start_trim)
    public void onClickAdvanceLowForwardStart(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceForwardStartTrimming(ADVANCE_PLAYER_PRECISION_LOW, startTimeMs,
                finishTimeMs);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceForwardStartTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM, startTimeMs,
                finishTimeMs);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceForwardStartTrimming(ADVANCE_PLAYER_PRECISION_HIGH, startTimeMs,
                finishTimeMs);
        }
    }

    @OnClick(R.id.player_advance_backward_end_trim)
    public void onClickAdvanceLowBackwardEnd(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceBackwardEndTrimming(ADVANCE_PLAYER_PRECISION_LOW, startTimeMs,
                finishTimeMs);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceBackwardEndTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM, startTimeMs,
                finishTimeMs);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceBackwardEndTrimming(ADVANCE_PLAYER_PRECISION_HIGH, startTimeMs,
                finishTimeMs);
        }
    }

    @OnClick(R.id.player_advance_forward_end_trim)
    public void onClickAdvanceLowForwardEnd(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceForwardEndTrimming(ADVANCE_PLAYER_PRECISION_LOW, finishTimeMs);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceForwardEndTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM, finishTimeMs);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceForwardEndTrimming(ADVANCE_PLAYER_PRECISION_HIGH, finishTimeMs);
        }
    }

    @OnClick(R.id.button_trim_accept)
    public void onClickTrimAccept() {
        presenter.setTrim(startTimeMs, finishTimeMs);
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_trim_cancel)
    public void onClickTrimCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void showTrimBar(int videoStartTime, int videoStopTime, int videoFileDuration) {
        if (stateWasRestored) {
            updateTrimmingTextTags();
            updateTimeVideoPlaying();
            stateWasRestored = false;
        } else {
            startTimeMs = videoStartTime;
            finishTimeMs = videoStopTime;
        }
        seekBarMinPosition = (float) startTimeMs / Constants.MS_CORRECTION_FACTOR;
        seekBarMaxPosition = (float) finishTimeMs / Constants.MS_CORRECTION_FACTOR;
        trimmingRangeSeekBar.setRangeValues(0f, (float) videoDuration / Constants.MS_CORRECTION_FACTOR);
        trimmingRangeSeekBar.setSelectedMinValue(seekBarMinPosition);
        trimmingRangeSeekBar.setSelectedMaxValue(seekBarMaxPosition);
        videonaPlayer.seekClipToTime(startTimeMs);
    }

    @Override
    public void refreshDurationTag(int duration) {
        durationTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(duration));
    }

    @Override
    public void refreshStartTimeTag(int startTime) {
    }

    @Override
    public void refreshStopTimeTag(int stopTime) {
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
    public void seekTo(int timeInMsec) {
        videonaPlayer.seekClipToTime(timeInMsec);
    }

    @Override
    public void showPreview(List<Video> movieList) {
        // (jliarte): 7/09/16 work on a copy to not modify original one until user accepts trimming
        video = new Video(movieList.get(0));
        videoDuration = video.getFileDuration();
        ArrayList<Video> clipList = new ArrayList<>();
        clipList.add(video);

        videonaPlayer.initPreviewLists(clipList);
//        initCurrentPosition();
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showText(String text, String position) {
        videonaPlayer.setImageText(text,position);
    }

    private void initCurrentPosition() {
        // TODO(jliarte): 5/09/16 this will give problems with state restoring as in config changes, cause it overrides restored currentPosition
        if (currentPosition == -1) currentPosition = video.getStartTime();
    }

    @Override
    public void showError(String message) {
    }

    @Override
    public void updateStartTrimmingRangeSeekBar(float minValue) {
        onRangeSeekBarValuesChanged(trimmingRangeSeekBar, minValue, seekBarMaxPosition);
        trimmingRangeSeekBar.setSelectedMinValue(minValue);
        this.startTimeMs = (int) ( minValue * Constants.MS_CORRECTION_FACTOR);
    }

    @Override
    public void updateFinishTrimmingRangeSeekBar(float maxValue) {
        onRangeSeekBarValuesChanged(trimmingRangeSeekBar, seekBarMinPosition, maxValue);
        trimmingRangeSeekBar.setSelectedMaxValue(maxValue);
        this.finishTimeMs = (int) ( maxValue * Constants.MS_CORRECTION_FACTOR);
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter();
    }

    private void updateTrimmingTextTags() {
        int duration = finishTimeMs - startTimeMs;
        durationTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(duration));
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
//        Log.d(TAG, " setRangeChangeListener " + minValue + " - " + maxValue);
        videonaPlayer.pausePreview();
        try {
            float minValueFloat = (float) minValue;
            float maxValueFloat = (float) maxValue;
            if (isRangeSeekBarLessThanMinTrimOffset(minValueFloat, maxValueFloat)) {
                if(seekBarMinPosition != minValueFloat) {
                    maxValueFloat = minValueFloat + Constants.MIN_TRIM_OFFSET;
                } else {
                    minValueFloat = maxValueFloat - Constants.MIN_TRIM_OFFSET;
                }
                updateTimesAndRangeSeekBar(minValueFloat, maxValueFloat);
                seekBarMinPosition = minValueFloat;
                seekBarMaxPosition = maxValueFloat;
                video.setStartTime(startTimeMs);
                video.setStopTime(finishTimeMs);
                currentPosition = startTimeMs;
                videonaPlayer.seekClipToTime(currentPosition);
                videonaPlayer.updatePreviewTimeLists();
                updateTrimmingTextTags();
                refreshDurationTag(finishTimeMs - startTimeMs);
                return;
            }
            this.startTimeMs = (int) ( minValueFloat * Constants.MS_CORRECTION_FACTOR);
            this.finishTimeMs = (int) ( maxValueFloat * Constants.MS_CORRECTION_FACTOR);

            if (seekBarMinPosition != minValueFloat) {
                seekBarMinPosition = minValueFloat;
                video.setStartTime(startTimeMs);
                currentPosition = startTimeMs;
            }
            if (seekBarMaxPosition != maxValueFloat) {
                seekBarMaxPosition = maxValueFloat;
                video.setStopTime(finishTimeMs);
                currentPosition = finishTimeMs;
            }
            videonaPlayer.updatePreviewTimeLists();
            updateTrimmingTextTags();
            refreshDurationTag(finishTimeMs - startTimeMs);
            videonaPlayer.seekClipToTime(currentPosition);
        } catch (Exception e) {
            Log.d(TAG, "Exception updating range seekbar selection values");
        }
    }

    private void updateTimesAndRangeSeekBar(float minValueFloat, float maxValueFloat) {
        this.startTimeMs = (int) ((minValueFloat) * Constants.MS_CORRECTION_FACTOR);
        trimmingRangeSeekBar.setSelectedMinValue(minValueFloat);
        this.finishTimeMs = (int) ( maxValueFloat * Constants.MS_CORRECTION_FACTOR);
        trimmingRangeSeekBar.setSelectedMaxValue(maxValueFloat);
    }

    private boolean isRangeSeekBarLessThanMinTrimOffset(float minValueFloat, float maxValueFloat) {
        return Math.abs(maxValueFloat - minValueFloat) <= Constants.MIN_TRIM_OFFSET;
    }

    private void updateTimeVideoPlaying() {
        if (video != null){
            // (jliarte): 22/08/16 As video in videonaPlayer view is a reference to this, we just
            //            update this video times and recalculate videonaPlayer time lists
            video.setStartTime(startTimeMs);
            video.setStopTime(finishTimeMs);
            videonaPlayer.updatePreviewTimeLists();
            videonaPlayer.seekClipToTime(currentPosition);
        }
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
      videonaPlayer.playPreview();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
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

    @Override
    public void updateViewToThemeDark() {
        tintAdvanceButtons(R.color.button_trim_color_theme_dark);
        tintDurationTag(R.color.textColorDark);
    }

    @Override
    public void updateViewToThemeLight() {
        tintAdvanceButtons(R.color.button_trim_color_theme_light);
        tintDurationTag(R.color.textColorLight);
    }

    private void tintAdvanceButtons(int button_color) {
        tintButton(playerAdvanceBackwardStartTrim, button_color);
        tintButton(playerAdvanceForwardStartTrim, button_color);
        tintButton(playerAdvanceBackwardEndTrim, button_color);
        tintButton(playerAdvanceForwardEndTrim, button_color);
    }

    private void tintDurationTag(int color) {
        durationTag.setTextColor(getResources().getColor(color));
    }

    private void showPlayerAdvanceLow() {
        playerAdvanceBackwardStartTrim.setImageResource
            (R.drawable.activity_edit_player_advance_low);
        playerAdvanceBackwardEndTrim.setImageResource
            (R.drawable.activity_edit_player_advance_low);
        playerAdvanceForwardEndTrim.setImageResource
            (R.drawable.activity_edit_player_advance_low);
        playerAdvanceForwardStartTrim.setImageResource
            (R.drawable.activity_edit_player_advance_low);
    }

    private void showPlayerAdvanceMedium() {
        playerAdvanceBackwardStartTrim.setImageResource
            (R.drawable.activity_edit_player_advance_medium);
        playerAdvanceBackwardEndTrim.setImageResource
            (R.drawable.activity_edit_player_advance_medium);
        playerAdvanceForwardEndTrim.setImageResource
            (R.drawable.activity_edit_player_advance_medium);
        playerAdvanceForwardStartTrim.setImageResource
            (R.drawable.activity_edit_player_advance_medium);
    }

    private void showPlayerAdvanceHigh() {
        playerAdvanceBackwardStartTrim.setImageResource
            (R.drawable.activity_edit_player_advance_high);
        playerAdvanceBackwardEndTrim.setImageResource
            (R.drawable.activity_edit_player_advance_high);
        playerAdvanceForwardEndTrim.setImageResource
            (R.drawable.activity_edit_player_advance_high);
        playerAdvanceForwardStartTrim.setImageResource
            (R.drawable.activity_edit_player_advance_high);
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
}
