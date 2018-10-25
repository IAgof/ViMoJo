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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VMCompositionPlayer;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_HIGH;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_LOW;
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_MEDIUM;
import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class VideoTrimActivity extends VimojoActivity implements TrimView, VMCompositionPlayer,
    RangeSeekBar.OnRangeSeekBarChangeListener, VMCompositionPlayer.VMCompositionPlayerListener,
    RadioGroup.OnCheckedChangeListener {

    private String TAG = "VideoTrimActivity";

    @Inject
    TrimPreviewPresenter presenter;

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

    private int videoIndexOnTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trim);
        ButterKnife.bind(this);

        this.getActivityPresentersComponent().inject(this);
        trimmingRangeSeekBar.setOnRangeSeekBarChangeListener(this);
        trimmingRangeSeekBar.setNotifyWhileDragging(true);
        radioGroupAdvanceTrim.setOnCheckedChangeListener(this);
        buttonSelectAdvanceMedium.setChecked(true);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        setupActivityViews();
        setVMCompositionPlayerListener(this);
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
        presenter.removePresenter();
    }

    private void setupActivityViews() {
        presenter.setupActivityViews();
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
    }

    @OnClick(R.id.player_advance_backward_start_trim)
    public void onClickAdvanceLowBackwardStart(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceBackwardStartTrimming(ADVANCE_PLAYER_PRECISION_LOW);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceBackwardStartTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceBackwardStartTrimming(ADVANCE_PLAYER_PRECISION_HIGH);
        }
    }

    @OnClick(R.id.player_advance_forward_start_trim)
    public void onClickAdvanceLowForwardStart(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceForwardStartTrimming(ADVANCE_PLAYER_PRECISION_LOW);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceForwardStartTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceForwardStartTrimming(ADVANCE_PLAYER_PRECISION_HIGH);
        }
    }

    @OnClick(R.id.player_advance_backward_end_trim)
    public void onClickAdvanceLowBackwardEnd(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceBackwardEndTrimming(ADVANCE_PLAYER_PRECISION_LOW);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceBackwardEndTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceBackwardEndTrimming(ADVANCE_PLAYER_PRECISION_HIGH);
        }
    }

    @OnClick(R.id.player_advance_forward_end_trim)
    public void onClickAdvanceLowForwardEnd(){
        if (buttonSelectAdvanceLow.isChecked()) {
            presenter.advanceForwardEndTrimming(ADVANCE_PLAYER_PRECISION_LOW);
        }
        if (buttonSelectAdvanceMedium.isChecked()) {
            presenter.advanceForwardEndTrimming(ADVANCE_PLAYER_PRECISION_MEDIUM);
        }
        if (buttonSelectAdvanceHigh.isChecked()) {
            presenter.advanceForwardEndTrimming(ADVANCE_PLAYER_PRECISION_HIGH);
        }
    }

    @OnClick(R.id.button_trim_accept)
    public void onClickTrimAccept() {
        presenter.setTrim();
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_trim_cancel)
    public void onClickTrimCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void showTrimBar(int videoDuration) {
        trimmingRangeSeekBar.setRangeValues(0f, (float) videoDuration / Constants.MS_CORRECTION_FACTOR);
    }

    @Override
    public void refreshDurationTag(int duration) {
        durationTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(duration));
    }

    @Override
    public void updateStartTrimmingRangeSeekBar(float startValue) {
        trimmingRangeSeekBar.setSelectedMinValue(startValue);
    }

    @Override
    public void updateFinishTrimmingRangeSeekBar(float finishValue) {
        trimmingRangeSeekBar.setSelectedMaxValue(finishValue);
    }


    @Override
    public void updateProject() {
        presenter.updatePresenter(videoIndexOnTrack);
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
//        Log.d(TAG, " setRangeChangeListener " + minValue + " - " + maxValue);
        presenter.onRangeSeekBarChanged(minValue, maxValue);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
      videonaPlayer.playPreview();
    }

    @Override
    public void playerReady() {
        // Do nothing
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

    @Override
    public void attachView(Context context) {
        videonaPlayer.attachView(context);
    }

    @Override
    public void detachView() {
        videonaPlayer.detachView();
    }

    @Override
    public void setVMCompositionPlayerListener(VMCompositionPlayerListener
                                                       vmCompositionPlayerListener) {
        videonaPlayer.setVMCompositionPlayerListener(vmCompositionPlayerListener);
    }

    @Override
    public void init(VMComposition vmComposition) {
        videonaPlayer.init(vmComposition);
    }

    @Override
    public void initSingleClip(VMComposition vmComposition, int clipPosition) {
        videonaPlayer.initSingleClip(vmComposition, clipPosition);
    }

    @Override
    public void initSingleVideo(Video video) {
        videonaPlayer.initSingleVideo(video);
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
        videonaPlayer.seekTo(timeInMsec);
    }

    @Override
    public void seekToClip(int position) {
        videonaPlayer.seekToClip(position);
    }

    @Override
    public void setSeekBarLayoutEnabled(boolean seekBarEnabled) {
        videonaPlayer.setSeekBarLayoutEnabled(seekBarEnabled);
    }

    @Override
    public void setAspectRatioVerticalVideos(int height) {
        videonaPlayer.setAspectRatioVerticalVideos(height);
    }

    @Override
    public void setImageText(String text, String textPosition, boolean textWithShadow, int width,
                             int height) {
        videonaPlayer.setImageText(text, textPosition, textWithShadow, width, height);
    }

    @Override
    public void setVideoVolume(float volume) {
        videonaPlayer.setVideoVolume(volume);
    }

    @Override
    public void setVoiceOverVolume(float volume) {
        videonaPlayer.setVoiceOverVolume(volume);
    }

    @Override
    public void setMusicVolume(float volume) {
        videonaPlayer.setMusicVolume(volume);
    }
}
