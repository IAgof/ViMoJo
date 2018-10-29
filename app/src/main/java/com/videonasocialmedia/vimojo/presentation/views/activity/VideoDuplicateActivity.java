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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.DuplicatePreviewPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class VideoDuplicateActivity extends VimojoActivity implements DuplicateView,
    VideonaPlayer {
    private static final String NUM_DUPLICATE_VIDEOS = "num_duplicate_videos";
    private static final String TAG = "VideoDuplicateActivity";

    @Inject DuplicatePreviewPresenter presenter;

    @BindView(R.id.coordinator_layout_video_duplicate)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.image_thumb_duplicate_video_left)
    ImageView imageThumbLeft;
    @BindView(R.id.image_thumb_duplicate_video_right)
    ImageView imageThumbRight;
    @BindView(R.id.textView_duplicate_num_increment)
    TextView textNumDuplicates;
    @BindView(R.id.button_duplicate_decrement_video)
    ImageButton decrementVideoButton;
    @BindView(R.id.button_duplicate_increment_video)
    ImageButton incrementVideoButton;
    @BindView(R.id.button_duplicate_cancel)
    ImageButton duplicateCancelButton;
    @BindView(R.id.button_duplicate_accept)
    ImageButton duplicateAcceptButton;
    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    int videoIndexOnTrack;
    private int numDuplicateVideos = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_duplicate);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        setupActivityButtons();

        this.getActivityPresentersComponent().inject(this);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        restoreState(savedInstanceState);

        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updatePresenter(videoIndexOnTrack);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.removePresenter();
    }

    private void setupActivityButtons() {
        tintVideoDuplicateButtons(R.color.button_color_theme_light);
    }

    private void tintVideoDuplicateButtons(int tintList) {
        tintButton(decrementVideoButton,tintList);
        tintButton(incrementVideoButton,tintList);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            numDuplicateVideos = savedInstanceState.getInt(NUM_DUPLICATE_VIDEOS, 2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
              onBackPressed();
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

    @Override
    public void onBackPressed() {
        presenter.cancelDuplicate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NUM_DUPLICATE_VIDEOS, numDuplicateVideos);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.button_duplicate_accept)
    public void onClickDuplicateAccept() {
        presenter.duplicateVideo(numDuplicateVideos);
    }

    @OnClick(R.id.button_duplicate_cancel)
    public void onClickDuplicateCancel() {
        presenter.cancelDuplicate();
    }

    @Override
    public void initDuplicateView(Video video) {
        updateDecrementVideoButton();
        showThumbVideo(imageThumbLeft, video);
        showThumbVideo(imageThumbRight, video);
    }

    @Override
    public void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter(videoIndexOnTrack);
    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, errorMessage,
                Snackbar.LENGTH_SHORT);
            snackbar.show();
        });
    }

    private void updateDecrementVideoButton() {
        if (numDuplicateVideos > 2) {
            decrementVideoButton.setVisibility(View.VISIBLE);
            decrementVideoButton.setEnabled(true);
            decrementVideoButton.setAlpha(1f);
        }else {
            decrementVideoButton.setEnabled(false);
            decrementVideoButton.setAlpha(0.5f);
        }
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
    public void setVideonaPlayerListener(VideonaPlayerListener
                                                   videonaPlayerListener) {
        videonaPlayer.setVideonaPlayerListener(videonaPlayerListener);
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

    private void showThumbVideo(ImageView imageThumbLeft, Video video) {
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
        updateDecrementVideoButton();
        textNumDuplicates.setText("x" + numDuplicateVideos);
    }

    @OnClick(R.id.button_duplicate_decrement_video)
    public void onClickDecrementVideo() {
        numDuplicateVideos--;
        updateDecrementVideoButton();
        textNumDuplicates.setText("x" + numDuplicateVideos);
    }
}
