package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverVolumePresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 19/09/16.
 */
public class VoiceOverVolumeActivity extends VimojoActivity implements SeekBar.OnSeekBarChangeListener,
        VideonaPlayer.VideonaPlayerListener, VoiceOverVolumeView {
    private static final String SOUND_VOLUME_POSITION_VOLUME = "sound_volume_position";
    private static final String SOUND_VOLUME_PROJECT_POSITION = "sound_volume_project_position";
    private static final String VOICE_OVER_RECORDED_PATH = "voice_over_recorded_path";
    private static final String TAG = "VoiceOverVolumeActivity";

    @Inject
    VoiceOverVolumePresenter presenter;

    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Bind(R.id.textView_seekBar_volume_sound)
    TextView textSeekBarVolume;
    @Bind (R.id.seekBar_volume_sound)
    SeekBar seekBarVolume;

    int videoIndexOnTrack;
    private int currentSoundVolumePosition =50;
    private int currentProjectPosition = 0;
    private String soundVoiceOverPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_volume);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        soundVoiceOverPath = intent.getStringExtra(IntentConstants.VOICE_OVER_RECORDED_PATH);

        getActivityPresentersComponent().inject(this);
        restoreState(savedInstanceState);
        videonaPlayer.setListener(this);
        seekBarVolume.setOnSeekBarChangeListener(this);
        seekBarVolume.setProgress(currentSoundVolumePosition);
        textSeekBarVolume.setText(currentSoundVolumePosition+" % ");
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentSoundVolumePosition = savedInstanceState.getInt(SOUND_VOLUME_POSITION_VOLUME, 0);
            currentProjectPosition = savedInstanceState.getInt(SOUND_VOLUME_PROJECT_POSITION, 0);
            soundVoiceOverPath = savedInstanceState.getString(VOICE_OVER_RECORDED_PATH);
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
        navigateTo(SoundActivity.class, videoIndexOnTrack);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SOUND_VOLUME_PROJECT_POSITION, videonaPlayer.getCurrentPosition());
        outState.putInt(SOUND_VOLUME_POSITION_VOLUME, seekBarVolume.getProgress());
        outState.putString(VOICE_OVER_RECORDED_PATH, soundVoiceOverPath);
        super.onSaveInstanceState(outState);
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_volume_sound_accept)
    public void onClickVolumeSoundAccept() {
        float volume = (float) (seekBarVolume.getProgress() * 0.01);
        presenter.setVoiceOverVolume(volume);
    }

    @OnClick(R.id.button_volume_sound_cancel)
    public void onClickVolumeSoundCancel() {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.deleteVoiceOver();
                        navigateTo(VoiceOverRecordActivity.class);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        builder.setMessage(R.string.exitSoundVolumeActivity)
                .setPositiveButton(R.string.acceptExitSoundVolumeActivity, dialogClickListener)
                .setNegativeButton(R.string.cancelExitSoundVolumeActvity, dialogClickListener).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textSeekBarVolume.setText(progress+" % ");
        videonaPlayer.setVoiceOverVolume(progress *0.01f);
        videonaPlayer.setVideoVolume((1-(progress*0.01f)));
        currentSoundVolumePosition = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void bindVideoList(List<Video> movieList) {
        videonaPlayer.bindVideoList(movieList);
        videonaPlayer.seekTo(currentProjectPosition);
        videonaPlayer.setVoiceOver(new Music(soundVoiceOverPath,
            FileUtils.getDuration(soundVoiceOverPath)));
        videonaPlayer.setVoiceOverVolume(currentSoundVolumePosition*0.01f);
    }

    @Override
    public void setMusic(Music music) {
        videonaPlayer.setMusic(music);
    }

    @Override
    public void resetPreview() {
        videonaPlayer.resetPreview();
    }

    @Override
    public void goToSoundActivity() {
        navigateTo(SoundActivity.class);
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
    public void showError(String message) {
        String title = getString(R.string.alert_dialog_title_voice_over);
        super.showAlertDialog(title, message);
    }

    @Override
    public void muteVideo() {
        videonaPlayer.setVideoVolume(0.0f);
    }

    @Override
    public void muteMusic() {
        videonaPlayer.setMusicVolume(0.0f);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }
}