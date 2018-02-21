package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverRecordPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverRecordView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 14/09/16.
 */
public class VoiceOverRecordActivity extends VimojoActivity implements VoiceOverRecordView,
        VideonaPlayer.VideonaPlayerListener, View.OnTouchListener {
    private static final String VOICE_OVER_POSITION = "voice_over_position";
    private static final String TAG = "VoiceOverRecordActivity";
    private static final String STATE_BUTTON_RECORD = "state_button_record";

    @Inject
    VoiceOverRecordPresenter presenter;

    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView(R.id.text_time_video_voice_over)
    TextView timeTag;
    @BindView(R.id.text_time_start_voice_over)
    TextView timeStart;
    @BindView(R.id.text_time_final_voice_over)
    TextView timeFinal;
    @BindView(R.id.progressBar_voice_over)
    ProgressBar progressBarVoiceOver;
    @BindView(R.id.button_voice_over_accept)
    ImageButton buttonVoiceOverAccept;
    @BindView(R.id.button_voice_over_cancel)
    ImageButton buttonVoiceOverCancel;
    @BindView (R.id.button_record_voice_over)
    ImageButton buttonRecordVoiceOver;

    int videoIndexOnTrack;
    private int currentVoiceOverPosition = 0;
    private int startTime = 0;

    private CountDownTimer timer;
    private int millisecondsLeft;
    private int maxDuration;
    private boolean buttonRecordIsInStop = false;
    private float videoVolumeMute = 0f;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_voice_over);
        ButterKnife.bind(this);
        this.getActivityPresentersComponent().inject(this);
        restoreState(savedInstanceState);
        changeVisibilityAndResouceButton();
        videonaPlayer.setSeekBarLayoutEnabled(false);
        videonaPlayer.setListener(this);
        createProgressDialog();
        buttonRecordVoiceOver.setOnTouchListener(this);
    }

    private void createProgressDialog() {
        progressDialog = new ProgressDialog(VoiceOverRecordActivity.this, R.style.VideonaDialog);
        progressDialog.setTitle(R.string.alert_dialog_title_voice_over);
        progressDialog.setMessage(getString(R.string.dialog_generating_voice_over));
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
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
        if(presenter.isRecording()){
            presenter.pauseRecording();
        }
        videonaPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.init();
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
        navigateTo(SoundActivity.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VOICE_OVER_POSITION, currentVoiceOverPosition);
        outState.putBoolean(STATE_BUTTON_RECORD, buttonRecordIsInStop);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.button_voice_over_accept)
    public void onClickVoiceOverAccept() {
        presenter.setVoiceOver(Constants.AUDIO_TEMP_RECORD_VOICE_OVER_FILENAME);
    }

    @OnClick(R.id.button_voice_over_cancel)
    public void onClickVoiceOverCancel(){
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.cancelVoiceOverRecorded();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        builder.setMessage(getString(R.string.dialog_voice_over_cancel_title_message))
                .setPositiveButton(getString(R.string.dialog_voice_over_cancel_positive_button),
                    dialogClickListener)
                .setNegativeButton(getString(R.string.dialog_voice_over_cancel_negative_button),
                    dialogClickListener).show();
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
    public void navigateToVoiceOverVolumeActivity(String voiceOverRecordedPath) {
        Intent intent = new Intent(this, VoiceOverVolumeActivity.class);
        intent.putExtra(IntentConstants.VOICE_OVER_RECORDED_PATH, voiceOverRecordedPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar.make(videonaPlayer, errorMessage, Snackbar.LENGTH_LONG).show();
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
    public void resetVoiceOverRecorded() {
        progressBarVoiceOver.setProgress(0);
        buttonRecordVoiceOver.setEnabled(true);
        refreshTimeTag(0);
        buttonRecordIsInStop = false;
        changeVisibilityAndResouceButton();
        millisecondsLeft = maxDuration;
        videonaPlayer.seekTo(0);
    }

    @Override
    public void disableRecordButton() {
        buttonRecordVoiceOver.setEnabled(false);
    }

    @Override
    public void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    progressDialog.show();
                }
            }
        });
    }

    @Override
    public void hideProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
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
                if(presenter.isRecording()){
                    presenter.resumeRecording();
                } else {
                    presenter.startRecording();
                }
                buttonRecordVoiceOver
                        .setImageResource(R.drawable.activity_edit_sound_voice_record_pressed);
                timerStart(millisecondsLeft);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Stop recording and save file
            presenter.pauseRecording();
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