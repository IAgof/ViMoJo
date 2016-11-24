package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.roughike.bottombar.BottomBar;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.EditorActivity;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.utils.Constants;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by ruth on 4/10/16.
 */

public class SoundActivity extends EditorActivity implements VideonaPlayerListener, SoundView {

    private static final String SOUND_ACTIVITY_PROJECT_POSITION = "sound_activity_project_position";

    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Bind(R.id.button_microphone)
    ImageButton buttonMicrophone;
    @Bind(R.id.button_music)
    ImageButton buttonMusic;
    @Bind (R.id.layout_options_sound_activity)
    LinearLayout layoutButtonSoundActivity;
    @Bind(R.id.bottomBar)
    BottomBar bottomBar;

    private BroadcastReceiver exportReceiver;
    private SoundPresenter presenter;
    private int currentProjectPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        ButterKnife.bind(this);
        setupActivityButtons();
        createExportReceiver();
        restoreState(savedInstanceState);
        presenter=new SoundPresenter(this);
        videonaPlayer.setListener(this);
        setupBottomNavigator(bottomBar);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentProjectPosition = savedInstanceState.getInt(SOUND_ACTIVITY_PROJECT_POSITION, 0);
        }
    }

    private void setupActivityButtons() {
        tintEditButtons(R.color.button_color);
    }

    private void tintEditButtons(int tintList) {
        tintButton(buttonMicrophone, tintList);
        tintButton(buttonMusic, tintList);

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
                        Snackbar.make(layoutButtonSoundActivity, R.string.shareError, Snackbar.LENGTH_LONG).show();
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SOUND_ACTIVITY_PROJECT_POSITION, videonaPlayer.getCurrentPosition());
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
        presenter.getMediaListFromProject();
        registerReceiver(exportReceiver, new IntentFilter(ExportProjectService.NOTIFICATION));
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

    @OnClick(R.id.button_music)
    public void goToMusicListActivity(){
        navigateTo(MusicListActivity.class);
    }

    @OnClick(R.id.button_microphone)
    public void goToVoiceOverActivity(){
        navigateTo(VoiceOverActivity.class);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }

}
