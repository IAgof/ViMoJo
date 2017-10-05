package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 14/09/16.
 */
public class VoiceOverActivity extends VimojoActivity implements VoiceOverView,
        VideonaPlayer.VideonaPlayerListener, View.OnTouchListener {
    private static final String VOICE_OVER_POSITION = "voice_over_position";
    private static final String TAG = "VoiceOverActivity";
    private static final String STATE_BUTTON_RECORD = "state_button_record";

    @Inject
    VoiceOverPresenter presenter;

    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Bind(R.id.text_time_video_voice_over)
    TextView timeTag;
    @Bind(R.id.text_time_start_voice_over)
    TextView timeStart;
    @Bind(R.id.text_time_final_voice_over)
    TextView timeFinal;
    @Bind(R.id.progressBar_voice_over)
    ProgressBar progressBarVoiceOver;
    @Bind(R.id.button_voice_over_accept)
    ImageButton buttonVoiceOverAccept;
    @Bind(R.id.button_voice_over_cancel)
    ImageButton buttonVoiceOverCancel;
    @Bind (R.id.button_record_voice_over)
    ImageButton buttonRecordVoiceOver;

    int videoIndexOnTrack;
    private int currentVoiceOverPosition = 0;
    private int startTime = 0;

    private CountDownTimer timer;
    private int millisecondsLeft;
    private int maxDuration;
    private boolean buttonRecordIsInStop = false;
    private float videoVolumeMute = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_voice_over);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);

        restoreState(savedInstanceState);

        changeVisibilityAndResouceButton();

        videonaPlayer.setSeekBarLayoutEnabled(false);
        videonaPlayer.setListener(this);
        buttonRecordVoiceOver.setOnTouchListener(this);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentVoiceOverPosition = savedInstanceState.getInt(VOICE_OVER_POSITION, 0);
            buttonRecordIsInStop=savedInstanceState.getBoolean(STATE_BUTTON_RECORD,false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
        videonaPlayer.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VOICE_OVER_POSITION, currentVoiceOverPosition);
        outState.putBoolean(STATE_BUTTON_RECORD, buttonRecordIsInStop);
        super.onSaveInstanceState(outState);
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_voice_over_accept)
    public void onClickVoiceOverAccept() {
        if (!buttonRecordIsInStop){
            navigateTo(SoundActivity.class);
        } else {
            presenter.addVoiceOver(Constants.AUDIO_TEMP_RECORD_VOICE_OVER_FILENAME);
        }
    }

    @OnClick(R.id.button_voice_over_cancel)
    public void onClickVoiceOverCancel(){
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        resetVoiceRecorder();
                        navigateTo(SoundActivity.class);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        // TODO:(alvaro.martinez) 16/09/16 Define these strings, es and eng
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        builder.setMessage("¿Desea descartar la locución y volver a grabarla de nuevo?")
                .setPositiveButton("Aceptar", dialogClickListener)
                .setNegativeButton("Declinar", dialogClickListener).show();
    }

    private void resetVoiceRecorder() {
        presenter.cleanDirectory();
        progressBarVoiceOver.setProgress(0);
        refreshTimeTag(0);
        buttonRecordIsInStop = false;
        changeVisibilityAndResouceButton();
        millisecondsLeft = maxDuration;
        videonaPlayer.seekTo(0);
    }

    private void refreshTimeTag(int currentPosition) {
        timeTag.setText(TimeUtils.toFormattedTimeWithMilliSecond(currentPosition + startTime));
    }

    @Override
    public void initVoiceOverView(int startTime, int maxSeekBar) {
        progressBarVoiceOver.setIndeterminate(false);
        progressBarVoiceOver.setMax(maxSeekBar);
        progressBarVoiceOver.setScaleY(5f);
        progressBarVoiceOver.setProgress(currentVoiceOverPosition);
        maxDuration = maxSeekBar;
        millisecondsLeft = maxSeekBar - currentVoiceOverPosition;
        this.startTime = startTime;
        timeStart.setText(TimeUtils.toFormattedTimeWithMilliSecond(startTime));
        timeFinal.setText(TimeUtils.toFormattedTimeWithMilliSecond(maxSeekBar));
        refreshTimeTag(currentVoiceOverPosition);
    }

    @Override
    public void bindVideoList(List<Video> movieList) {
        videonaPlayer.bindVideoList(movieList);
        videonaPlayer.seekToClip(0);
        videonaPlayer.seekTo(currentVoiceOverPosition);
        videonaPlayer.showPauseButton();
        videonaPlayer.setVideoVolume(videoVolumeMute);
        // Disable ontouch view playerExo. Now you can't play/pause video.
        enableDisableView(videonaPlayer, false);
    }

    @Override
    public void resetPreview() {
        videonaPlayer.resetPreview();
    }

    @Override
    public void playVideo() {
        videonaPlayer.playPreview();
        videonaPlayer.showPauseButton();
        videonaPlayer.setVideoVolume(videoVolumeMute);
    }

    @Override
    public void pauseVideo() {
        videonaPlayer.pausePreview();
        videonaPlayer.showPauseButton();
    }

    @Override
    public void navigateToSoundVolumeActivity(String voiceOverRecordedPath) {
        Intent intent = new Intent(this, SoundVolumeActivity.class);
        intent.putExtra(IntentConstants.VOICE_OVER_RECORDED_PATH, voiceOverRecordedPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar.make(videonaPlayer, errorMessage, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void setVideoFadeTransitionAmongVideos() {
        videonaPlayer.setVideoTransitionFade();
    }

    @Override
    public void setAudioFadeTransitionAmongVideos() {
        videonaPlayer.setAudioTransitionFade();
    }

    @Override
    public void updateProject() {
        presenter.init();
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
        videonaPlayer.setVideoVolume(videoVolumeMute);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // start recording.
            if (millisecondsLeft > 0) {
                presenter.requestRecord();
                buttonRecordVoiceOver
                        .setImageResource(R.drawable.activity_edit_sound_voice_record_pressed);
                timerStart(millisecondsLeft);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Stop recording and save file
            presenter.stopRecording();
            buttonRecordIsInStop =true;
            changeVisibilityAndResouceButton();
            cancelTimer();
            return true;
        }
        return false;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void timerStart(int timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 100) {
            @Override
            public void onTick(long milliTillFinish) {
                millisecondsLeft=(int)milliTillFinish;
                int timeProgress = maxDuration - (int) milliTillFinish;
                progressBarVoiceOver.setProgress(timeProgress);
                currentVoiceOverPosition = timeProgress;
                refreshTimeTag(timeProgress);
            }

            @Override
            public void onFinish() {
                presenter.stopRecording();
                buttonRecordIsInStop =true;
                progressBarVoiceOver.setProgress(maxDuration);
                changeVisibilityAndResouceButton();
                refreshTimeTag(maxDuration);
            }
        }.start();
    }

    private void changeVisibilityAndResouceButton() {
        buttonRecordVoiceOver.setImageResource(
            R.drawable.activity_edit_sound_voice_record_normal);
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;
            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }
}