package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

/**
 * Created by ruth on 13/09/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.SoundListAdapter;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class MusicListActivity extends VimojoActivity implements MusicListView,
        SoundRecyclerViewClickListener, VideonaPlayer.VideonaPlayerListener {
    private static final String MUSIC_LIST_PROJECT_POSITION = "music_list_project_position";

    @Bind(R.id.music_list)
    RecyclerView soundList;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;

    private SoundListAdapter soundAdapter;
    private MusicListPresenter presenter;
    private BroadcastReceiver exportReceiver;
    private int currentProjectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);
        setupToolbar();
        createExportReceiver();
        restoreState(savedInstanceState);
        videonaPlayer.setListener(this);
        presenter = new MusicListPresenter(this);
        initVideoListRecycler();
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
                        Snackbar.make(soundList, R.string.shareError, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        };

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

    public void goToShare(String videoToSharePath) {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MUSIC_LIST_PROJECT_POSITION, videonaPlayer.getCurrentPosition());
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
        presenter.onResume();
        registerReceiver(exportReceiver, new IntentFilter(ExportProjectService.NOTIFICATION));
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
    public void onClick(Music music) {
        Intent i = new Intent(VimojoApplication.getAppContext(), MusicDetailActivity.class);
        i.putExtra(IntentConstants.MUSIC_DETAIL_SELECTED, music.getMediaPath());
        startActivity(i);
        finish();
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}





