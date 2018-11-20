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
public class MusicListActivity extends VimojoActivity implements MusicListView,
    SoundRecyclerViewClickListener{

    @Inject MusicListPresenter presenter;

    @BindView(R.id.music_list)
    RecyclerView soundList;
    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    private SoundListAdapter soundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        setupToolbar();
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
        presenter.pausePresenter();
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
    public void showMusicSelected(int positionSelected) {
        soundList.scrollToPosition(positionSelected);
        soundAdapter.updateSelection(positionSelected);
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter();
    }

    @Override
    public void navigateToDetailMusic(String musicPath) {
        Intent i = new Intent(VimojoApplication.getAppContext(), MusicDetailActivity.class);
        i.putExtra(IntentConstants.MUSIC_DETAIL_SELECTED, musicPath);
        startActivity(i);
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
    public void setAspectRatioVerticalVideos(int height) {
        videonaPlayer.setAspectRatioVerticalVideos(height);
    }

    @Override
    public void onClick(Music music) {
        presenter.selectMusic(music);
    }

}





