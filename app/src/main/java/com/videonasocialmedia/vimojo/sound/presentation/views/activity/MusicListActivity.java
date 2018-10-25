package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

/**
 * Created by ruth on 13/09/16.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.playback.VMCompositionPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.SoundListAdapter;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class MusicListActivity extends VimojoActivity implements MusicListView, VMCompositionPlayer,
        SoundRecyclerViewClickListener{
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
        initVideoListRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updatePresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.removePresenter();
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
        super.onSaveInstanceState(outState);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Override
    public void showMusicList(List<Music> musicList) {
        soundAdapter.setMusicList(musicList);
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





