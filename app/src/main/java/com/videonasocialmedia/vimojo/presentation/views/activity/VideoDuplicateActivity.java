package com.videonasocialmedia.vimojo.presentation.views.activity;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.DuplicatePreviewPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;

import com.videonasocialmedia.vimojo.settings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class VideoDuplicateActivity extends VimojoActivity implements DuplicateView,
        VideonaPlayer.VideonaPlayerListener {
    private static final String DUPLICATE_VIDEO_POSITION = "duplicate_video_position";
    private static final String NUM_DUPLICATE_VIDEOS = "num_duplicate_videos";
    private static final String TAG = "VideoDuplicateActivity";

    @Inject DuplicatePreviewPresenter presenter;

    @Bind(R.id.image_thumb_duplicate_video_left)
    ImageView imageThumbLeft;
    @Bind(R.id.image_thumb_duplicate_video_right)
    ImageView imageThumbRight;
    @Bind(R.id.textView_duplicate_num_increment)
    TextView textNumDuplicates;
    @Bind(R.id.button_duplicate_decrement_video)
    ImageButton decrementVideoButton;
    @Bind(R.id.button_duplicate_increment_video)
    ImageButton incrementVideoButton;
    @Bind(R.id.button_duplicate_cancel)
    ImageButton duplicateCancelButton;
    @Bind(R.id.button_duplicate_accept)
    ImageButton duplicateAcceptButton;
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    int videoIndexOnTrack;
    private Video video;
    private int currentPosition = 0;
    private int numDuplicateVideos = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_duplicate);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        setupActivityButtons();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.getActivityPresentersComponent().inject(this);

        videonaPlayer.setListener(this);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        restoreState(savedInstanceState);

        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    private void setupActivityButtons() {
        tintVideoDuplicateButtons(R.color.button_color);
    }

    private void tintVideoDuplicateButtons(int tintList) {
        tintButton(decrementVideoButton,tintList);
        tintButton(incrementVideoButton,tintList);
        tintButton(duplicateAcceptButton,tintList);
        tintButton(duplicateCancelButton,tintList);
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
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.loadProjectVideo(videoIndexOnTrack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(DUPLICATE_VIDEO_POSITION, 0);
            numDuplicateVideos = savedInstanceState.getInt(NUM_DUPLICATE_VIDEOS, 2);
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
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        startActivity(intent);
        finish();
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateTo(EditActivity.class, videoIndexOnTrack);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(DUPLICATE_VIDEO_POSITION, videonaPlayer.getCurrentPosition());
        outState.putInt(NUM_DUPLICATE_VIDEOS, numDuplicateVideos);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.button_duplicate_accept)
    public void onClickDuplicateAccept() {
        presenter.duplicateVideo(video, videoIndexOnTrack, numDuplicateVideos);
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnClick(R.id.button_duplicate_cancel)
    public void onClickDuplicateCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void initDuplicateView(String path) {
        if (numDuplicateVideos > 2)
            decrementVideoButton.setVisibility(View.VISIBLE);
        else
            decrementVideoButton.setVisibility(View.GONE);
        showThumbVideo(imageThumbLeft);
        showThumbVideo(imageThumbRight);
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
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        // TODO(jliarte): will need fix when all videos from project will be loaded
        videonaPlayer.initPreviewLists(movieList);
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showError(String message) {
    }

    private void showThumbVideo(ImageView imageThumbLeft) {
        int microSecond = video.getStartTime() * 1000;
        BitmapPool bitmapPool = Glide.get(this).getBitmapPool();
        FileDescriptorBitmapDecoder decoder = new FileDescriptorBitmapDecoder(
                new VideoBitmapDecoder(microSecond),
                bitmapPool,
                DecodeFormat.PREFER_ARGB_8888);

        String path = video.getIconPath() != null
                ? video.getIconPath() : video.getMediaPath();
        Glide.with(this)
                .load(path)
                .asBitmap()
                .videoDecoder(decoder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.fragment_gallery_no_image)
                .into(imageThumbLeft);
    }

    @OnClick(R.id.button_duplicate_increment_video)
    public void onClickIncrementVideo() {
        numDuplicateVideos++;
        decrementVideoButton.setVisibility(View.VISIBLE);
        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    @OnClick(R.id.button_duplicate_decrement_video)
    public void onClickDecrementVideo() {
        numDuplicateVideos--;
        if (numDuplicateVideos <= 2)
            decrementVideoButton.setVisibility(View.GONE);
        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }
}
