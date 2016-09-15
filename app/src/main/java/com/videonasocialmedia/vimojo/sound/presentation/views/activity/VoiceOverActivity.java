package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.TimeUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 14/09/16.
 */
public class VoiceOverActivity extends VimojoActivity implements VoiceOverView, VideonaPlayerListener,
        SeekBar.OnSeekBarChangeListener {

    private static final String VOICE_OVER_POSITION = "voice_over_position";
    private static final String VOICE_OVER_PROJECT_POSITION = "voice_over_project_position";

    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;

    @Bind(R.id.text_time_video_voice_over)
    TextView timeTag;
    @Bind(R.id.text_time_start_voice_over)
    TextView timeStart;
    @Bind(R.id.text_time_final_voice_over)
    TextView timeFinal;
    @Bind(R.id.seekBar_voice_over)
    SeekBar seekBarVoiceOver;
    @Bind(R.id.button_voice_over_accept)
    ImageButton buttonVoiceOverAccept;
    @Bind(R.id.button_voice_over_cancel)
    ImageButton buttonVoiceOverCancel;
    @Bind (R.id.button_record_voice_over)
    ImageButton buttonRecordVoiceOver;

    int videoIndexOnTrack;
    private VoiceOverPresenter presenter;
    private int currentVoiceOverPosition = 0;
    private int currentProjectPosition = 0;
    private int startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_voice_over);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        presenter = new VoiceOverPresenter(this);
        videonaPlayer.setSeekBarEnabled(false);
        videonaPlayer.setListener(this);
        presenter.onCreate();

        seekBarVoiceOver.setProgress(0);
        seekBarVoiceOver.setOnSeekBarChangeListener(this);
        timeTag.setText(TimeUtils.toFormattedTime(0));

        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentVoiceOverPosition = savedInstanceState.getInt(VOICE_OVER_POSITION, 0);
            currentProjectPosition = savedInstanceState.getInt(VOICE_OVER_PROJECT_POSITION, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
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

    @Override
    public void onBackPressed() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VOICE_OVER_PROJECT_POSITION, videonaPlayer.getCurrentPosition()  );
        outState.putInt(VOICE_OVER_POSITION, currentVoiceOverPosition);
        super.onSaveInstanceState(outState);
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_voice_over_accept)
    public void onClickVoiceOverAccept() {

    }

    @OnClick(R.id.button_voice_over_cancel)
    public void onClickVoiceOverCancel(){
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            currentVoiceOverPosition = progress;
            //splitSeekBar.setProgress(progress);
            refreshTimeTag(currentVoiceOverPosition);
            videonaPlayer.seekTo(progress);
        }
    }

    private void refreshTimeTag(int currentPosition) {

        timeTag.setText(TimeUtils.toFormattedTime(currentPosition + startTime));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void initVoiceOverView(int startTime, int maxSeekBar) {
        seekBarVoiceOver.setMax(maxSeekBar);
        seekBarVoiceOver.setProgress(currentVoiceOverPosition);
        this.startTime = startTime;
        refreshTimeTag(currentVoiceOverPosition);
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
    public void newClipPlayed(int currentClipIndex) {
    }

}

