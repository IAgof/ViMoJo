package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverVolumePresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 19/09/16.
 */
public class VoiceOverVolumeActivity extends VimojoActivity implements
    SeekBar.OnSeekBarChangeListener, VoiceOverVolumeView {
    private static final String SOUND_VOLUME_POSITION_VOLUME = "sound_volume_position";
    private static final String VOICE_OVER_RECORDED_PATH = "voice_over_recorded_path";
    private static final String TAG = "VoiceOverVolumeActivity";

    @Inject
    VoiceOverVolumePresenter presenter;

    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView(R.id.textView_seekBar_volume_sound)
    TextView textSeekBarVolume;
    @BindView (R.id.seekBar_volume_sound)
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
        seekBarVolume.setOnSeekBarChangeListener(this);
        seekBarVolume.setProgress(currentSoundVolumePosition);
        textSeekBarVolume.setText(currentSoundVolumePosition+" % ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updatePresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pausePresenter();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentSoundVolumePosition = savedInstanceState.getInt(SOUND_VOLUME_POSITION_VOLUME, 0);
            soundVoiceOverPath = savedInstanceState.getString(VOICE_OVER_RECORDED_PATH);
        }
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
        builder.setMessage(R.string.exitVoiceOverVolumeActivity)
                .setPositiveButton(R.string.acceptExitVoiceOverVolumeActivity, dialogClickListener)
                .setNegativeButton(R.string.cancelExitVoiceOverVolumeActvity, dialogClickListener).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        presenter.setVolumeProgress(progress);
        currentSoundVolumePosition = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void goToSoundActivity() {
        navigateTo(SoundActivity.class);
    }

    @Override
    public void showError(String message) {
        runOnUiThread(() -> {
            String title = getString(R.string.alert_dialog_title_voice_over);
            super.showAlertDialog(title, message);
        });
    }

    @Override
    public void updateTagVolume(String percentageVolume) {
        textSeekBarVolume.setText(percentageVolume);
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
    public void init(VMComposition vmComposition) {
        videonaPlayer.init(vmComposition);
    }

    @Override
    public void setVoiceOverVolume(float volume) {
        videonaPlayer.setVoiceOverVolume(volume);
    }

    @Override
    public void setAspectRatioVerticalVideos(int height) {
        videonaPlayer.setAspectRatioVerticalVideos(height);
    }
}