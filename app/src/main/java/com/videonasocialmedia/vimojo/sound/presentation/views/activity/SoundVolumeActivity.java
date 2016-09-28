package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundVolumePresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 19/09/16.
 */
public class SoundVolumeActivity extends VimojoActivity implements SeekBar.OnSeekBarChangeListener, VideonaPlayerListener, SoundVolumeView {

    private static final String SOUND_VOLUME_POSITION_VOLUME = "sound_volume_position";
    private static final String SOUND_VOLUME_PROJECT_POSITION = "sound_volume_project_position";
    private static final String VOICE_OVER_RECORDED_PATH = "voice_over_recorded_path";
    private static final String TAG = "SoundVolumeActivity";

    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    SoundVolumePresenter presenter;
    @Bind(R.id.textView_seekBar_volume_sound)
    TextView textSeekBarVolume;
    @Bind (R.id.seekBar_volume_sound)
    SeekBar seekBarVolume;
    @Bind (R.id.button_volume_sound_accept)
    ImageButton buttonVolumeSoundAccept;
    @Bind (R.id.button_volume_sound_cancel)
    ImageButton buttonVolumeSoundCancel;
    int videoIndexOnTrack;
    private int currentSoundVolumePosition =50;
    private int currentProjectPosition = 0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String videoTemPathMixAudio = bundle.getString(ExportProjectService.FILEPATH);
                int resultCode = bundle.getInt(ExportProjectService.RESULT);
                if (resultCode == RESULT_OK) {
                    goToMixAudio(videoTemPathMixAudio);
                } else {

                    //showError(R.string.addMediaItemToTrackError);
                }
            }
        }
    };
    private String soundVoiceOverPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_volume);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        soundVoiceOverPath = intent.getStringExtra(ExportIntentConstants.VOICE_OVER_RECORDED_PATH);

        restoreState(savedInstanceState);
        presenter = new SoundVolumePresenter(this);
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
        unregisterReceiver(receiver);
        videonaPlayer.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ExportProjectService.NOTIFICATION));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        if (cls == GalleryActivity.class) {
            intent.putExtra("SHARE", false);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        navigateTo(EditActivity.class, videoIndexOnTrack);
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

    private void goToMixAudio(String videoTemPathMixAudio) {

        float volumen = (float) (seekBarVolume.getProgress() * 0.01);

        presenter.setVolume(soundVoiceOverPath, videoTemPathMixAudio ,volumen);
    }

    @OnClick(R.id.button_volume_sound_accept)
    public void onClickVolumeSoundAccept(){

        Intent intent = new Intent(this, ExportProjectService.class);
        Snackbar.make(videonaPlayer,"Starting mixing audio", Snackbar.LENGTH_INDEFINITE).show();
        this.startService(intent);
    }

    @OnClick(R.id.button_volume_sound_cancel)
    public void onClickVolumeSoundCancel(){


        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        navigateTo(VoiceOverActivity.class);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        builder.setMessage(R.string.exitSoundVolumeActivity).setPositiveButton(R.string.acceptExitSoundVolumeActivity, dialogClickListener)
                .setNegativeButton(R.string.cancelExitSoundVolumeActvity, dialogClickListener).show();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textSeekBarVolume.setText(progress+" % ");
        videonaPlayer.changeVolume(progress *0.01f);
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
        videonaPlayer.setMusic(new Music(soundVoiceOverPath));
        videonaPlayer.changeVolume(currentSoundVolumePosition*0.01f);
    }


    @Override
    public void resetPreview() {
        videonaPlayer.resetPreview();
    }


    @Override
    public void goToEditActivity() {

        // Add volume to project
        navigateTo(EditActivity.class);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}
