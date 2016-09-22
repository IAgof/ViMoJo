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
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;

import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicDetailActivity extends VimojoActivity implements MusicDetailView, VideonaPlayerListener {

    public static String KEY_MUSIC_ID = "KEY_MUSIC_ID";

    @Bind(R.id.music_title)
    TextView musicTitle;
    @Bind(R.id.music_author)
    TextView musicAuthor;
    @Nullable
    @Bind(R.id.music_image)
    ImageView musicImage;
    @Nullable
    @Bind(R.id.scene_root)
    FrameLayout sceneRoot;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;

    private Scene acceptCancelScene;
    private Scene deleteSecene;

    private MusicDetailPresenter musicDetailPresenter;

    private BroadcastReceiver exportReceiver;
    private int musicId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);
        initToolbar();
        videonaPlayer.setListener(this);
        videonaPlayer.initPreview(0);

        UserEventTracker userEventTracker = UserEventTracker.getInstance(MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN));
        musicDetailPresenter = new MusicDetailPresenter(this, videonaPlayer, userEventTracker);
        createExportReceiver();
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
                        Snackbar.make(sceneRoot, R.string.shareError, Snackbar.LENGTH_LONG).show();
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
            musicId = extras.getInt(KEY_MUSIC_ID);
            musicDetailPresenter.onResume(musicId);
        } catch (Exception e) {
            //TODO show snackbar with error message
        }
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
    public void showAuthor(String author) {
        musicAuthor.setText(author);
    }

    @Override
    public void showTitle(String title) {
        musicTitle.setText(title);
    }

    @Override
    public void showImage(String imagePath) {
        Glide.with(this).load(imagePath).into(musicImage);
    }

    @Override
    public void showImage(int resourceId) {
        musicImage.setImageResource(resourceId);
    }

    @Override
    public void setupScene(boolean musicInProject) {
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
    public void goToEdit(String musicTitle) {
        Intent i = new Intent(this, EditActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(Constants.MUSIC_SELECTED_TITLE, musicTitle);
        startActivity(i);

    }

    @Nullable
    @OnClick(R.id.select_music)
    public void selectMusic() {
        musicDetailPresenter.addMusic();
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
        musicDetailPresenter.removeMusic();
        goToMusicList();
    }

    private void goToMusicList() {
        Intent intent = new Intent(this, SoundListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    @Nullable
    @OnClick(R.id.cancel_music)
    public void onBackPressed() {
        if (musicDetailPresenter.isMusicAddedToProject())
            goToEditActivity();
        else
            goToMusicList();
    }

    private void goToEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}
