package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.ftp.presentation.services.FtpUploaderService;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.presentation.views.adapter.SocialNetworkAdapter;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by root on 31/05/16.
 */
public class ShareActivity extends VimojoActivity implements ShareVideoView, VideonaPlayerListener,
        SocialNetworkAdapter.OnSocialNetworkClickedListener{

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Bind(R.id.main_social_network_list)
    RecyclerView mainSocialNetworkList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab_share_room)
    FloatingActionButton fab;

    private String videoPath;
    private ShareVideoPresenter presenter;
    private SocialNetworkAdapter mainSocialNetworkAdapter;
    private int currentPosition;

    private SharedPreferences sharedPreferences;
    protected UserEventTracker userEventTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.userEventTracker = UserEventTracker.getInstance(MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN));
        sharedPreferences = getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        presenter = new ShareVideoPresenter(this, userEventTracker, sharedPreferences);

        presenter.onCreate();
        videoPath = getIntent().getStringExtra(Constants.VIDEO_TO_SHARE_PATH);
        videonaPlayer.setListener(this);
        initNetworksList();

        restoreState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        videonaPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.onResume();
        showPreview();
    }


    private void initNetworksList() {
        mainSocialNetworkAdapter = new SocialNetworkAdapter(this);

        int orientation = LinearLayoutManager.VERTICAL;
        mainSocialNetworkList.setLayoutManager(
                new LinearLayoutManager(this, orientation, false));
        mainSocialNetworkList.setAdapter(mainSocialNetworkAdapter);
    }

    @Override
    public void playPreview() {
        videonaPlayer.playPreview();
    }

    @Override
    public void pausePreview() {
        videonaPlayer.pausePreview();
    }


    public void showPreview() {

        List<Video> shareVideoList = new ArrayList<Video>();
        Video videoShare = new Video(videoPath);
        shareVideoList.add(videoShare);

        videonaPlayer.initPreviewLists(shareVideoList);
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showError(String message) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPosition", videonaPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("currentPosition", 0);
        }
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
        if (cls == GalleryActivity.class) {
            intent.putExtra("SHARE", false);
        }
        startActivity(intent);
    }

    @OnClick(R.id.fab_share_room)
    public void showMoreNetworks() {
        updateNumTotalVideosShared();
        presenter.trackVideoShared("Other network");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        Uri uri = Utils.obtainUriToShare(this, videoPath);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }


    private void updateNumTotalVideosShared() {
        presenter.updateNumTotalVideosShared();
    }

    @Override
    public void showShareNetworksAvailable(List<SocialNetwork> networks) {
        // TODO move this to presenter in merging alpha and stable.
        SocialNetwork saveToGallery = new SocialNetwork("SaveToGallery",getString(R.string.save_to_gallery), "", "",
                this.getResources().getDrawable(R.drawable.activity_share_save_to_gallery), "");
        networks.add(saveToGallery);
        mainSocialNetworkAdapter.setSocialNetworkList(networks);
    }

    @Override
    public void hideShareNetworks() {

    }

    @Override
    public void showMoreNetworks(List<SocialNetwork> networks) {

    }

    @Override
    public void hideExtraNetworks() {

    }

    @Override
    public void onSocialNetworkClicked(SocialNetwork socialNetwork) {
        presenter.trackVideoShared(socialNetwork.getIdSocialNetwork());
        if (socialNetwork.getName().equals(getString(R.string.save_to_gallery))) {
            showMessage(R.string.video_saved);
            return;
        }
        presenter.shareVideo(videoPath, socialNetwork, this);
        updateNumTotalVideosShared();
    }

    public void showMessage(final int stringToast) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, stringToast, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @OnClick({R.id.ftp_container, R.id.ftp_icon})
    public void requestFtpUpload() {

        Intent intent = new Intent(this, FtpUploaderService.class);
        intent.putExtra("VIDEO_FOLDER_PATH", videoPath);
        startService(intent);

    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}
