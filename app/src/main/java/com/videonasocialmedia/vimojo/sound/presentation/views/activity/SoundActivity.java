package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.AudioTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.AudioTimeLineAdapter;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.MusicTimeLineAdapter;
import com.videonasocialmedia.vimojo.sound.presentation.views.custom.CardViewAudioTrack;
import com.videonasocialmedia.vimojo.sound.presentation.views.custom.CardViewAudioTrackListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FabUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

/**
 * Created by ruth on 4/10/16.
 */

public class SoundActivity extends EditorActivity implements VideonaPlayer.VideonaPlayerListener,
    SoundView, AudioTimeLineRecyclerViewClickListener, CardViewAudioTrackListener {

  private static final String SOUND_ACTIVITY_PROJECT_POSITION = "sound_activity_project_position";
  private static final String TAG = "SoundActivity";
  private static final int ID_TRACK_CLIP_VIDEO = 0;
  private static final int ID_TRACK_CLIP_AUDIO_FIRST = 1;
  private static final int ID_TRACK_CLIP_AUDIO_SECOND = 2;
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

  @Bind(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;
  private BroadcastReceiver exportReceiver;
  private int currentProjectPosition = 0;

  private RecyclerView audioListRecyclerView;
  private RecyclerView musicListRecyclerView;
  private RecyclerView voiceOverListRecyclerView;
  private AudioTimeLineAdapter audioTimeLineAdapter;
  private MusicTimeLineAdapter musicTimeLineAdapter;
  private MusicTimeLineAdapter voiceOverTimeLineAdapter;
  private boolean voiceOverActivated;
  private FloatingActionButton fabVoiceOver;

  private int num_grid_columns = 1;
  int orientation = LinearLayoutManager.HORIZONTAL;
  private int positionAudioTrackMusic;
  private RecyclerView.LayoutManager audioListLayoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      inflateLinearLayout(R.id.container_layout,R.layout.activity_sound);
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
            Intent intent = new Intent(VimojoApplication.getAppContext(),
                ExportProjectService.class);
            Snackbar.make(relativeLayoutActivitySound, "Starting export",
                Snackbar.LENGTH_INDEFINITE).show();
            VimojoApplication.getAppContext().startService(intent);
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
    audioTimeLineAdapter.setAudioList(movieList);

  }

  @Override
  public void bindVideoTrack(float volume, boolean muteAudio, boolean soloAudio) {
    trackClipsVideo.setListener(this, ID_TRACK_CLIP_VIDEO);
    audioListRecyclerView = trackClipsVideo.getRecyclerView();

    trackClipsVideo.setImageTrack(R.drawable.activity_edit_sound_original_down);
    trackClipsVideo.setTitleTrack(getString(R.string.title_track_clip_video));

    trackClipsVideo.setSeekBar((int) (volume*100));
    trackClipsVideo.setSwitchMuteAudio(muteAudio);
    trackClipsVideo.setSwitchSoloAudio(soloAudio);

    audioListLayoutManager = new GridLayoutManager(this, num_grid_columns,
        orientation, false);
    audioListRecyclerView.setLayoutManager(audioListLayoutManager);
    audioTimeLineAdapter = new AudioTimeLineAdapter(this);
    audioListRecyclerView.setAdapter(audioTimeLineAdapter);
  }

  @Override
  public void bindMusicList(List<Music> musicList) {
    musicTimeLineAdapter.setMusicList(musicList);
    videonaPlayer.setMusic(musicList.get(0));
  }

  @Override
  public void bindMusicTrack(float volume, boolean muteAudio, boolean soloAudio, int position) {

    if(position == 1) {
      initTrackClipAudioTrackFirst(volume, muteAudio, soloAudio);
      initTitleAndIconAudioTrackFirstMusic();
      musicListRecyclerView = trackClipsAudioTrackFirst.getRecyclerView();
    } else {
      initTrackClipAudioTrackSecond(volume, muteAudio, soloAudio);
      initTitleAndIconAudioTrackSecondMusic();
      musicListRecyclerView = trackClipsAudioTrackSecond.getRecyclerView();
    }

    RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(this, num_grid_columns,
        orientation, false);
    musicTimeLineAdapter = new MusicTimeLineAdapter(this);
    musicListRecyclerView.setLayoutManager(layoutManager2);
    musicListRecyclerView.setAdapter(musicTimeLineAdapter);
  }

  @Override
  public void bindVoiceOverList(List<Music> voiceOverList) {
   voiceOverTimeLineAdapter.setMusicList(voiceOverList);
    videonaPlayer.setVoiceOver(voiceOverList.get(0));
  }

  @Override
  public void bindVoiceOverTrack(float volume, boolean muteAudio, boolean soloAudio, int position) {

    if(position == 1) {
      initTrackClipAudioTrackFirst(volume, muteAudio, soloAudio);
      initTitleAndIconAudioTrackFirstVoiceOver();
      voiceOverListRecyclerView = trackClipsAudioTrackFirst.getRecyclerView();
    } else {
      initTrackClipAudioTrackSecond(volume, muteAudio, soloAudio);
      initTitleAndIconAudioTrackSecondVoiceOver();
      voiceOverListRecyclerView = trackClipsAudioTrackSecond.getRecyclerView();
    }

    RecyclerView.LayoutManager layoutManager3 = new GridLayoutManager(this, num_grid_columns,
        orientation, false);
    voiceOverTimeLineAdapter = new MusicTimeLineAdapter(this);
    voiceOverListRecyclerView.setLayoutManager(layoutManager3);
    voiceOverListRecyclerView.setAdapter(voiceOverTimeLineAdapter);
  }


  private void initTrackClipAudioTrackFirst(float volume, boolean muteAudio, boolean soloAudio) {
    trackClipsAudioTrackFirst.setListener(this, ID_TRACK_CLIP_AUDIO_FIRST);
    trackClipsAudioTrackFirst.setVisibility(View.VISIBLE);
    trackClipsAudioTrackFirst.isShowedAudioTrackOptions();
    trackClipsAudioTrackFirst.setSeekBar((int) (volume*100));
    trackClipsAudioTrackFirst.setSwitchMuteAudio(muteAudio);
    trackClipsAudioTrackFirst.setSwitchSoloAudio(soloAudio);
  }

  private void initTrackClipAudioTrackSecond(float volume, boolean muteAudio, boolean soloAudio) {
    trackClipsAudioTrackSecond.setListener(this, ID_TRACK_CLIP_AUDIO_SECOND);
    trackClipsAudioTrackSecond.setVisibility(View.VISIBLE);

    trackClipsAudioTrackSecond.isShowedAudioTrackOptions();
    trackClipsAudioTrackSecond.setSeekBar((int) (volume*100));
    trackClipsAudioTrackSecond.setSwitchMuteAudio(muteAudio);
    trackClipsAudioTrackSecond.setSwitchSoloAudio(soloAudio);
  }

  private void initTitleAndIconAudioTrackFirstMusic() {
    positionAudioTrackMusic = 1;
    trackClipsAudioTrackFirst.setImageTrack(R.drawable.activity_edit_sound_music_down);
    trackClipsAudioTrackFirst.setTitleTrack(getString(R.string.title_track_clip_music));
  }

  private void initTitleAndIconAudioTrackSecondMusic() {
    positionAudioTrackMusic = 2;
    trackClipsAudioTrackSecond.setImageTrack(R.drawable.activity_edit_sound_music_down);
    trackClipsAudioTrackSecond.setTitleTrack(getString(R.string.title_track_clip_music));
  }

  private void initTitleAndIconAudioTrackFirstVoiceOver() {
    trackClipsAudioTrackFirst.setImageTrack(R.drawable.activity_edit_sound_voice_over_down);
    trackClipsAudioTrackFirst.setTitleTrack(getString(R.string.title_track_clip_voice_over));
  }

  private void initTitleAndIconAudioTrackSecondVoiceOver() {
    trackClipsAudioTrackSecond.setImageTrack(R.drawable.activity_edit_sound_voice_over_down);
    trackClipsAudioTrackSecond.setTitleTrack(getString(R.string.title_track_clip_voice_over));
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


  @Nullable @Override
  public void newClipPlayed(int currentClipIndex) {
    audioTimeLineAdapter.updateSelection(currentClipIndex);
    audioListRecyclerView.scrollToPosition(currentClipIndex);
  }

  @Override
  public void onAudioClipClicked(int position) {
    Log.d(TAG, "onAudioClipClicked, position " + position);
    videonaPlayer.seekToClip(position);
    int lastPosition = audioTimeLineAdapter.getSelectedVideoPosition();
    audioTimeLineAdapter.updateSelection(position);
    //audioListLayoutManager.scrollToPosition(position + 2);
    if(lastPosition > position && position > 0) {
      audioListRecyclerView.smoothScrollToPosition(--position);
    } else {
      audioListRecyclerView.smoothScrollToPosition(++position);
    }
  }

  @Override
  public void onMusicClipClicked(int position) {
    videonaPlayer.seekTo(0);
    audioTimeLineAdapter.updateSelection(0);
    audioListRecyclerView.smoothScrollToPosition(0);
  }

  @Override
  public void onVoiceOverClipClicked(int position) {
    videonaPlayer.seekTo(0);
    audioTimeLineAdapter.updateSelection(0);
    audioListRecyclerView.smoothScrollToPosition(0);
  }

  @Override
  public void setSeekBarProgress(int progress, int id) {
    switch (id){
      case ID_TRACK_CLIP_VIDEO:
        videonaPlayer.setVideoVolume(progress);
        presenter.setVideoVolume(progress);
        break;
      case ID_TRACK_CLIP_AUDIO_FIRST:
        if(isMusicFirstTrack()) {
          videonaPlayer.setMusicVolume(progress);
          presenter.setMusicVolume(progress);
        } else {
          videonaPlayer.setVoiceOverVolume(progress);
          presenter.setVoiceOverVolume(progress);
        }
        break;
      case ID_TRACK_CLIP_AUDIO_SECOND:
        if(isMusicSecondTrack()) {
          videonaPlayer.setMusicVolume(progress);
          presenter.setMusicVolume(progress);
        } else {
          videonaPlayer.setVoiceOverVolume(progress);
          presenter.setVoiceOverVolume(progress);
        }
      default:
        return;
    }
  }

  @Override
  public void setSwitchSoloAudio(boolean isChecked, int id) {
    switch (id){
      case ID_TRACK_CLIP_VIDEO:
        if(isChecked)
          videonaPlayer.setVideoVolume(0f);
        presenter.soloVideo(isChecked);
        break;
      case ID_TRACK_CLIP_AUDIO_FIRST:
        if(isMusicFirstTrack()) {
          if (isChecked)
            videonaPlayer.setMusicVolume(0f);
          presenter.soloMusic(isChecked);
        } else {
          if(isChecked)
            videonaPlayer.setVoiceOverVolume(0f);
          presenter.soloVoiceOver(isChecked);
        }
        break;
      case ID_TRACK_CLIP_AUDIO_SECOND:
        if(isMusicSecondTrack()) {
          if (isChecked)
            videonaPlayer.setMusicVolume(0f);
          presenter.soloMusic(isChecked);
        } else {
          if (isChecked)
            videonaPlayer.setVoiceOverVolume(0f);
          presenter.soloVoiceOver(isChecked);
        }
      default:
        return;
    }
  }

  @Override
  public void setSwitchMuteAudio(boolean isChecked, int id) {
    switch (id){
      case ID_TRACK_CLIP_VIDEO:
        if(isChecked)
          videonaPlayer.setVideoVolume(0f);
        presenter.muteVideo(isChecked);
        break;
      case ID_TRACK_CLIP_AUDIO_FIRST:
        if(isMusicFirstTrack()) {
          if (isChecked)
            videonaPlayer.setMusicVolume(0f);
          presenter.muteMusic(isChecked);
        } else {
          if(isChecked)
            videonaPlayer.setVoiceOverVolume(0f);
          presenter.muteVoiceOver(isChecked);
        }
        break;
      case ID_TRACK_CLIP_AUDIO_SECOND:
        if(isMusicSecondTrack()) {
          if (isChecked)
            videonaPlayer.setMusicVolume(0f);
          presenter.muteMusic(isChecked);
        } else {
          if (isChecked)
            videonaPlayer.setVoiceOverVolume(0f);
          presenter.muteVoiceOver(isChecked);
        }
      default:
        return;
    }
  }

  @Override
  public void onClickImageIconTrack(int id) {
    switch (id){
      case ID_TRACK_CLIP_VIDEO:
        focusOnView(trackClipsVideo);
        if(trackClipsVideo.isShowedAudioTrackOptions()) {
          trackClipsVideo.setImageTrack(R.drawable.activity_edit_sound_original_up);
        } else {
          trackClipsVideo.setImageTrack(R.drawable.activity_edit_sound_original_down);
        }
        break;
      case ID_TRACK_CLIP_AUDIO_FIRST:
        focusOnView(trackClipsAudioTrackFirst);
        if(trackClipsAudioTrackFirst.isShowedAudioTrackOptions()){
          if(isMusicFirstTrack()){
            trackClipsAudioTrackFirst.setImageTrack(R.drawable.activity_edit_sound_music_up);
          } else {
            trackClipsAudioTrackFirst.setImageTrack(R.drawable.activity_edit_sound_voice_over_up);
          }
        } else {
          if(isMusicFirstTrack()){
            trackClipsAudioTrackFirst.setImageTrack(R.drawable.activity_edit_sound_music_down);
          } else {
            trackClipsAudioTrackFirst.setImageTrack(R.drawable.activity_edit_sound_voice_over_down);
          }
        }
        break;
      case ID_TRACK_CLIP_AUDIO_SECOND:
        focusOnView(trackClipsAudioTrackSecond);
        if(trackClipsAudioTrackSecond.isShowedAudioTrackOptions()){
          if(isMusicSecondTrack()){
            trackClipsAudioTrackSecond.setImageTrack(R.drawable.activity_edit_sound_music_up);
          } else {
            trackClipsAudioTrackSecond.setImageTrack(R.drawable.activity_edit_sound_voice_over_up);
          }
        } else {
          if(isMusicSecondTrack()){
            trackClipsAudioTrackSecond.setImageTrack(R.drawable.activity_edit_sound_music_down);
          } else {
            trackClipsAudioTrackSecond.setImageTrack(R.drawable.activity_edit_sound_voice_over_down);
          }
        }
        break;
      default:
        return;
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


  private boolean isMusicFirstTrack() {
    return positionAudioTrackMusic == 1;
  }

  private boolean isMusicSecondTrack() {
    return positionAudioTrackMusic == 2;
  }
}
