package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

/**
 * Created by ruth on 13/09/16.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.SoundListAdapter;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;

/**
 *
 */
public class MusicListActivity extends VimojoActivity implements MusicListView,
        SoundRecyclerViewClickListener, VideonaPlayer.VideonaPlayerListener {
    private static final String MUSIC_LIST_PROJECT_POSITION = "music_list_project_position";

    @Inject MusicListPresenter presenter;

    @BindView(R.id.music_list)
    RecyclerView soundList;

    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    private SoundListAdapter soundAdapter;
    private int currentProjectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        setupToolbar();
        restoreState(savedInstanceState);
        videonaPlayer.setListener(this);
        initVideoListRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.updatePresenter();
        if (BuildConfig.FEATURE_VERTICAL_VIDEOS) {
            videonaPlayer.setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentProjectPosition = savedInstanceState.getInt(MUSIC_LIST_PROJECT_POSITION, 0);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initVideoListRecycler() {
        soundAdapter = new SoundListAdapter();
        soundAdapter.setSoundRecyclerViewClickListener(this);
        presenter.getAvailableMusic();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        soundList.setLayoutManager(layoutManager);
        soundList.setAdapter(soundAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MUSIC_LIST_PROJECT_POSITION, videonaPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Override
    public void showVideoList(List<Music> musicList) {
        soundAdapter.setMusicList(musicList);
    }

    @Override
    public void bindVideoList(List<Video> movieList) {

        videonaPlayer.bindVideoList(movieList);
        videonaPlayer.seekTo(currentProjectPosition);
    }

    @Override
    public void resetPreview() {
        videonaPlayer.resetPreview();
    }

    @Override
    public void goToDetailActivity(String mediaPath) {
        navigateToMusicDetailActivity(mediaPath);
    }

    @Override
    public void setVideoFadeTransitionAmongVideos() {
        videonaPlayer.setVideoTransitionFade();
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter();
    }

    @Override
    public void onClick(Music music) {
        navigateToMusicDetailActivity(music.getMediaPath());
    }

    private void navigateToMusicDetailActivity(String mediaPath) {
        Intent i = new Intent(VimojoApplication.getAppContext(), MusicDetailActivity.class);
        i.putExtra(IntentConstants.MUSIC_DETAIL_SELECTED, mediaPath);
        startActivity(i);
        finish();
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}





