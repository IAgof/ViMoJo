package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.sound.presentation.views.custom.CardViewAudioTrack;
import com.videonasocialmedia.vimojo.sound.presentation.views.custom.CardViewAudioTrackListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FabUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

/**
 * Created by ruth on 4/10/16.
 */

public class SoundActivity extends EditorActivity implements VideonaPlayer.VideonaPlayerListener,
    SoundView, CardViewAudioTrackListener{

  private static final String SOUND_ACTIVITY_PROJECT_POSITION = "sound_activity_project_position";
  private static final String TAG = "SoundActivity";
  private final int ID_BUTTON_FAB_TOP=1;
  private final int ID_BUTTON_FAB_BOTTOM=3;

  @Inject SoundPresenter presenter;

  @Nullable @Bind(R.id.videona_player)
  VideonaPlayerExo videonaPlayer;
  @Nullable @Bind( R.id.bottomBar)
  BottomBar bottomBar;
  @Nullable @Bind(R.id.relative_layout_activity_sound)
  RelativeLayout relativeLayoutActivitySound;

  @Nullable @Bind(R.id.cardview_audio_blocks_clips_video)
  CardViewAudioTrack trackClipsVideo;
  @Nullable @Bind(R.id.cardview_audio_blocks_clips_audio_track_first)
  CardViewAudioTrack trackClipsAudioTrackFirst;
  @Nullable @Bind(R.id.cardview_audio_blocks_clips_audio_track_second)
  CardViewAudioTrack trackClipsAudioTrackSecond;
  @Nullable @Bind(R.id.scrollview_timeline_audio_blocks)
  ScrollView scrollViewTimeLineAudioBlocks;
  @Nullable @Bind(R.id.button_sound_warning_transcoding_file)
  ImageButton warningTranscodingFilesButton;

  @Bind(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;
  private BroadcastReceiver exportReceiver;
  private int currentProjectPosition = 0;

  private boolean voiceOverActivated;
  private FloatingActionButton fabVoiceOver;
  private String warningTranscodingFilesMessage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      inflateLinearLayout(R.id.container_layout,R.layout.activity_sound);
    inflateLinearLayout(R.id.container_navigator,R.layout.sound_activity_layout_button_navigator);
      ButterKnife.bind(this);
      getActivityPresentersComponent().inject(this);
      createExportReceiver();
      restoreState(savedInstanceState);
      videonaPlayer.setListener(this);
      bottomBar.selectTabWithId(R.id.tab_sound);
      setupBottomBar(bottomBar);
      setupFab();
  }

  private void setupBottomBar(BottomBar bottomBar) {
    bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
      @Override
      public void onTabSelected(@IdRes int tabId) {
        switch (tabId){
          case(R.id.tab_editactivity):
            navigateTo(EditActivity.class);
            break;
          case (R.id.tab_share):
            navigateTo(ShareActivity.class);
            break;
        }
      }
    });
  }

  private void setupFab() {
    addAndConfigurateFabButton(ID_BUTTON_FAB_TOP, R.drawable.activity_edit_sound_music_normal,
        R.color.colorWhite);
  }
  protected void addAndConfigurateFabButton(int id, int icon, int color) {
    FloatingActionButton newFabMini = FabUtils.createNewFabMini(id, icon, color);
    onClickFabButton(newFabMini);
    fabMenu.addButton(newFabMini);
  }

  protected void onClickFabButton(final FloatingActionButton fab) {
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          switch (fab.getId()){
            case ID_BUTTON_FAB_TOP:
              fabMenu.collapse();
              navigateTo(MusicListActivity.class);
              break;
            case ID_BUTTON_FAB_BOTTOM:
              fabMenu.collapse();
              navigateTo(VoiceOverActivity.class);
              break;
          }
      }
    });

  }

  private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentProjectPosition = savedInstanceState.getInt(SOUND_ACTIVITY_PROJECT_POSITION, 0);
        }
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
                      Snackbar.make(relativeLayoutActivitySound, R.string.shareError, Snackbar.LENGTH_LONG).show();
                      bottomBar.selectTabWithId(R.id.tab_sound);
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
      if(voiceOverActivated){
        removeFabVoiceOver();
      }
  }

  private void removeFabVoiceOver() {
    fabMenu.removeButton(fabVoiceOver);
  }

  @Override
  protected void onResume() {
      super.onResume();
      videonaPlayer.onShown(this);
      presenter.init();
      registerReceiver(exportReceiver, new IntentFilter(ExportProjectService.NOTIFICATION));
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  public void bindVideoList(List<Video> movieList) {
    videonaPlayer.bindVideoList(movieList);
    videonaPlayer.seekTo(currentProjectPosition);
  }

  @Override
  public void bindMusicList(List<Music> musicList) {
    videonaPlayer.setMusic(musicList.get(0));
  }

  @Override
  public void bindVoiceOverList(List<Music> voiceOverList) {
    videonaPlayer.setVoiceOver(voiceOverList.get(0));
  }

  @Override
  public void hideVoiceOverCardView() {
    trackClipsAudioTrackSecond.setVisibility(View.GONE);
  }

  @Override
  public void addVoiceOverOptionToFab() {
    voiceOverActivated = true;
    fabVoiceOver = FabUtils.createNewFabMini(ID_BUTTON_FAB_BOTTOM,
        R.drawable.activity_edit_sound_voice_normal,R.color.colorWhite);
    onClickFabButton(fabVoiceOver);
    fabMenu.addButton(fabVoiceOver);
  }

  @Override
  public void setVideoFadeTransitionAmongVideos() {
    videonaPlayer.setVideoTransitionFade();
  }

  @Override
  public void setAudioFadeTransitionAmongVideos() {
    videonaPlayer.setAudioTransitionFade();
  }

  @Override
  public void resetPreview() {
      videonaPlayer.resetPreview();
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

  @Override
  public void bindTrack(Track track) {
    switch (track.getId()) {
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_MEDIA_TRACK:
        trackClipsVideo.setListener(this);
        trackClipsVideo.setTrack(track);
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC:
          if(track.getPosition() == 1) {
            trackClipsAudioTrackFirst.setListener(this);
            trackClipsAudioTrackFirst.setTrack(track);
          }else {
            trackClipsAudioTrackSecond.setListener(this);
            trackClipsAudioTrackSecond.setTrack(track);
          }
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if(track.getPosition() == 1) {
          trackClipsAudioTrackFirst.setListener(this);
          trackClipsAudioTrackFirst.setTrack(track);
        }else {
          trackClipsAudioTrackSecond.setListener(this);
          trackClipsAudioTrackSecond.setTrack(track);
        }
        break;
    }
  }

  @Override
  public void showTrackVideo() {
    trackClipsVideo.setVisibility(View.VISIBLE);
  }

  @Override
  public void showTrackAudioFirst() {
    trackClipsAudioTrackFirst.setVisibility(View.VISIBLE);
  }

  @Override
  public void showTrackAudioSecond() {
    trackClipsAudioTrackSecond.setVisibility(View.VISIBLE);
  }

  @Override
  public void showWarningTempFile() {
    warningTranscodingFilesButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void setWarningMessageTempFile(String messageTempFile) {
    warningTranscodingFilesMessage = messageTempFile;
  }

  @Nullable @Override
  public void newClipPlayed(int currentClipIndex) {
    trackClipsVideo.updateClipSelection(currentClipIndex);
  }

  @Override
  public void setSeekBarProgress(int id, int seekBarProgress) {
    presenter.setTrackVolume(id, seekBarProgress);
  }

  @Override
  public void setSwitchMuteAudio(int id, boolean isChecked) {
    presenter.setTrackMute(id, isChecked);
  }

  @Override
  public void onClickExpandInfoTrack(int positionInTrack) {
    switch (positionInTrack){
      case 0:
        focusOnView(trackClipsVideo);
        break;
      case 1:
        focusOnView(trackClipsAudioTrackFirst);
        break;
      case 2:
        focusOnView(trackClipsAudioTrackSecond);
        break;
    }
  }

  @Override
  public void onClickMediaClip(int position, int trackId) {
    videonaPlayer.seekToClip(position);
    // TODO:(alvaro.martinez) 31/05/17 If Vimojo support more than one music or voice over, update and calculate correct position
    switch (trackId){
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_MEDIA_TRACK:
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC:
        trackClipsVideo.updateClipSelection(position);
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        trackClipsVideo.updateClipSelection(position);
        break;
    }
  }

  private final void focusOnView(final View view) {
    scrollViewTimeLineAudioBlocks.post(new Runnable() {
      @Override
      public void run() {
        scrollViewTimeLineAudioBlocks.scrollTo(0, view.getTop());
      }
    });
  }


  @Nullable @OnClick(R.id.button_sound_warning_transcoding_file)
  public void onClickWarningTranscodingFile(){
    AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.VideonaDialog);
    dialog.setTitle(getString(R.string.dialog_title_warning_error_transcoding_file));
    dialog.setMessage(getString(R.string.dialog_message_warning_error_transcoding_file));
    dialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    dialog.show();
  }
}
