package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.settings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;

import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicDetailActivity extends VimojoActivity implements MusicDetailView,
        VideonaPlayer.VideonaPlayerListener {

    private static final String MUSIC_DETAIL_PROJECT_POSITION = "music_detail_project_position";

    @Bind(R.id.music_title)
    TextView musicTitle;
    @Bind(R.id.music_author)
    TextView musicAuthor;
    @Bind(R.id.music_duration)
    TextView musicDuration;
    @Nullable
    @Bind(R.id.music_image)
    ImageView musicImage;
    @Nullable
    @Bind(R.id.scene_root)
    FrameLayout sceneRoot;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;

    @Inject MusicDetailPresenter presenter;
    private Scene acceptCancelScene;
    private Scene deleteSecene;
    private BroadcastReceiver exportReceiver;
    private String musicPath;
    private Music music;
    private int currentProjectPosition;

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
        videonaPlayer.setListener(this);
        createExportReceiver();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentProjectPosition = savedInstanceState.getInt(MUSIC_DETAIL_PROJECT_POSITION, 0);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void createExportReceiver() {
        exportReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String videoToSharePath = bundle.getString(ExportProjectService.FILEPATH);
                    int resultCode = bundle.getInt(ExportProjectService.RESULT);
                    if (resultCode == RESULT_OK) {
                        goToShare(videoToSharePath);
                    } else {
                        Snackbar.make(sceneRoot, R.string.shareError,
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        };

    }

    public void goToShare(String videoToSharePath) {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MUSIC_DETAIL_PROJECT_POSITION, videonaPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
        unregisterReceiver(exportReceiver);
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
        registerReceiver(exportReceiver, new IntentFilter(ExportProjectService.NOTIFICATION));
        presenter.onResume(musicPath);
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
    public void setMusic(Music music, boolean scene) {
        musicSelectedOptions(scene);
        videonaPlayer.setMusic(music);
        videonaPlayer.setVolume(1f);
        updateCoverInfo(music);
        this.music = music;
        videonaPlayer.playPreview();
    }

    private void updateCoverInfo(Music music) {
        musicAuthor.setText(music.getAuthor());
        musicTitle.setText(music.getTitle());
        musicDuration.setText(music.getMusicDuration());
        Glide.with(VimojoApplication.getAppContext()).load(music.getIconResourceId()).error(R.drawable.fragment_gallery_no_image);
        musicImage.setImageResource(music.getIconResourceId());
        //
    }

    @Override
    public void goToEdit(String musicTitle) {
        Intent i = new Intent(VimojoApplication.getAppContext(), EditActivity.class);
        i.putExtra(Constants.MUSIC_SELECTED_TITLE, musicTitle);
        startActivity(i);
        finish();

    }

    @Nullable
    @OnClick(R.id.select_music)
    public void selectMusic() {

        presenter.addMusic(music);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.go(deleteSecene);
        } else {
            LayoutInflater inflater = this.getLayoutInflater();
            inflater.inflate(R.layout.activity_music_detail_scene_delete, sceneRoot);
        }
        ButterKnife.bind(this);
    }

    @Nullable
    @OnClick(R.id.delete_music)
    public void deleteMusic() {
        presenter.removeMusic(music);
        goToMusicList();
    }

    private void goToMusicList() {
        Intent intent = new Intent(VimojoApplication.getAppContext(), MusicListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    @Nullable
    @OnClick(R.id.cancel_music)
    public void onBackPressed() {
            goToMusicList();
    }


    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}
