package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;


public class MusicDetailActivity extends VimojoActivity implements MusicDetailView,
    SeekBar.OnSeekBarChangeListener{

    private static final String MUSIC_DETAIL_PROJECT_POSITION = "music_detail_project_position";
    private String MUSIC_DETAIL_POSITION_VOLUME = "sound_volume_position";

    @BindView(R.id.music_title)
    TextView musicTitle;
    @BindView(R.id.music_author)
    TextView musicAuthor;
    @BindView(R.id.music_duration)
    TextView musicDuration;
    @Nullable @BindView(R.id.music_image)
    ImageView musicImage;
    @Nullable @BindView(R.id.scene_root)
    FrameLayout sceneRoot;
    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView (R.id.seekBar_volume_sound)
    SeekBar seekBarVolume;

    @Inject MusicDetailPresenter presenter;
    private Scene acceptCancelScene;
    private Scene deleteSecene;
    private String musicPath;
    private Music music;
    private int currentProjectPosition;
    private int currentSoundVolumePosition =50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        UserEventTracker userEventTracker = getUserEventTracker();
//        presenter = new MusicDetailPresenter(this, userEventTracker);
        getActivityPresentersComponent().inject(this);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);
        initToolbar();
        restoreState(savedInstanceState);
        seekBarVolume.setOnSeekBarChangeListener(this);
        seekBarVolume.setProgress(currentSoundVolumePosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        try {
            Bundle extras = this.getIntent().getExtras();
            musicPath = extras.getString(IntentConstants.MUSIC_DETAIL_SELECTED);
        } catch (Exception e) {
            //TODO show snackbar with error message
        }
        presenter.updatePresenter(musicPath);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentProjectPosition = savedInstanceState.getInt(MUSIC_DETAIL_PROJECT_POSITION, 0);
            currentSoundVolumePosition = savedInstanceState.getInt(MUSIC_DETAIL_POSITION_VOLUME, 0);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MUSIC_DETAIL_PROJECT_POSITION, videonaPlayer.getCurrentPosition());
        outState.putInt(MUSIC_DETAIL_POSITION_VOLUME, seekBarVolume.getProgress());
        super.onSaveInstanceState(outState);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Override
    public void musicSelectedOptions(boolean musicInProject) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            acceptCancelScene = Scene.getSceneForLayout(sceneRoot,
                    R.layout.activity_music_detail_scene_accept_cancel, this);
            deleteSecene = Scene.getSceneForLayout(sceneRoot,
                    R.layout.activity_music_detail_scene_delete, this);
            if (musicInProject) {
                TransitionManager.go(deleteSecene);
            } else {
                TransitionManager.go(acceptCancelScene);
            }

        } else {
            LayoutInflater inflater = this.getLayoutInflater();
            if (musicInProject) {
                inflater.inflate(R.layout.activity_music_detail_scene_delete, sceneRoot);
            } else {
                inflater.inflate(R.layout.activity_music_detail_scene_accept_cancel, sceneRoot);
            }
        }
        ButterKnife.bind(this);
    }

    @Override
    public void bindVideoList(List<Video> movieList) {
        videonaPlayer.bindVideoList(movieList);
        videonaPlayer.seekTo(currentProjectPosition);
    }

    @Override
    public void setVoiceOver(Music voiceOver) {
        videonaPlayer.pausePreview();
        videonaPlayer.setVoiceOver(voiceOver);
        videonaPlayer.playPreview();
    }

    @Override
    public void setMusic(Music music, boolean scene) {
        musicSelectedOptions(scene);
        videonaPlayer.setMusic(music);
        updateCoverInfo(music);
        seekBarVolume.setProgress((int)(music.getVolume()*100));
        this.music = music;
        videonaPlayer.playPreview();
    }

    private void updateCoverInfo(Music music) {
        musicAuthor.setText(music.getAuthor());
        musicTitle.setText(music.getTitle());
        musicDuration.setText(music.getMusicDuration());
        Glide.with(VimojoApplication.getAppContext()).load(music.getIconResourceId()).error(R.drawable.fragment_gallery_no_image);
        musicImage.setImageResource(music.getIconResourceId());
    }

    @Override
    public void goToSoundActivity() {
        Intent i = new Intent(VimojoApplication.getAppContext(), SoundActivity.class);
        startActivity(i);
    }

    @Override
    public void setVideoFadeTransitionAmongVideos() {
        videonaPlayer.setVideoTransitionFade();
    }

    @Override
    public void showError(String message) {
        String title = getString(R.string.alert_dialog_title_music);
        super.showAlertDialog(title, message);
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter(musicPath);
    }

    @Override
    public void setAspectRatioVerticalVideos() {
        videonaPlayer.setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
    }

    @Optional @OnClick(R.id.select_music)
    public void selectMusic() {
        float volume = (float) (seekBarVolume.getProgress() * 0.01);
        presenter.addMusic(music, volume);
        TransitionManager.go(deleteSecene);
        ButterKnife.bind(this);
    }

    @Optional @OnClick(R.id.delete_music)
    public void deleteMusic() {
        presenter.removeMusic(music);
    }

    private void goToMusicList() {
        Intent intent = new Intent(VimojoApplication.getAppContext(), MusicListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Optional @OnClick(R.id.cancel_music)
    public void onCancelMusicClickListener() {
            goToMusicList();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        videonaPlayer.setMusicVolume(progress *0.01f);
        videonaPlayer.setVideoVolume((1-(progress*0.01f)));
        currentSoundVolumePosition = progress;
        if(music!=null)
            presenter.setVolume(progress*0.01f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
