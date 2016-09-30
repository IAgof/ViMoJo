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
import android.widget.ImageButton;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.SoundListAdapter;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class SoundListActivity  extends VimojoActivity implements SoundListView,
        SoundRecyclerViewClickListener, VideonaPlayerListener {

    @Bind(R.id.sound_list)
    RecyclerView soundList;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Bind(R.id.button_microphone)
    ImageButton buttonMicrophone;

    private SoundListAdapter soundAdapter;
    private SoundListPresenter presenter;
    private BroadcastReceiver exportReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_list);
        ButterKnife.bind(this);
        setupToolbar();
        createExportReceiver();
        videonaPlayer.setListener(this);
        presenter = new SoundListPresenter(this);
        initVideoListRecycler();
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
        videonaPlayer.seekToClip(0);
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

    @OnClick(R.id.button_microphone)
        public void goToVoiceOver() {
        navigateTo(VoiceOverActivity.class);
        }
    }




