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
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoTrimActivity extends VimojoActivity implements TrimView,
        RangeSeekBar.OnRangeSeekBarChangeListener, VideonaPlayer.VideonaPlayerListener {

    public static final float MS_CORRECTION_FACTOR = 1000f;
    public static final float MIN_TRIM_OFFSET = 0.35f;

    @Inject TrimPreviewPresenter presenter;

    @Bind(R.id.text_time_trim)
    TextView durationTag;
    @Bind(R.id.trim_rangeSeekBar)
    RangeSeekBar trimmingRangeSeekBar;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;

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
    private boolean activityStateHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trim);
        ButterKnife.bind(this);

        this.getActivityPresentersComponent().inject(this);

        trimmingRangeSeekBar.setOnRangeSeekBarChangeListener(this);
        trimmingRangeSeekBar.setNotifyWhileDragging(true);
        videonaPlayer.setListener(this);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        restoreState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.init(videoIndexOnTrack);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(VIDEO_POSITION, 0);
            startTimeMs = savedInstanceState.getInt(START_TIME_TAG);
            finishTimeMs = savedInstanceState.getInt(STOP_TIME_TAG);

            activityStateHasChanged = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if (activityStateHasChanged) {
            updateTrimmingTextTags();
            updateTimeVideoPlaying();
            activityStateHasChanged = false;
        } else {
            startTimeMs = videoStartTime;
            finishTimeMs = videoStopTime;
        }
        seekBarMinPosition = (float) startTimeMs / MS_CORRECTION_FACTOR;
        seekBarMaxPosition = (float) finishTimeMs / MS_CORRECTION_FACTOR;
        trimmingRangeSeekBar.setRangeValues(0f, (float) videoDuration / MS_CORRECTION_FACTOR);
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
            if (Math.abs(maxValueFloat - minValueFloat) <= MIN_TRIM_OFFSET) {
                return;
            }
            this.startTimeMs = (int) ( minValueFloat * MS_CORRECTION_FACTOR);
            this.finishTimeMs = (int) ( maxValueFloat * MS_CORRECTION_FACTOR);

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
            videonaPlayer.seekClipToTime(currentPosition);
            videonaPlayer.updatePreviewTimeLists();
            updateTrimmingTextTags();
        } catch (Exception e) {
            Log.d(TAG, "Exception updating range seekbar selection values");
        }
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
    }
}
