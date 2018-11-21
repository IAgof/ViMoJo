package com.videonasocialmedia.vimojo.sound.presentation.views.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.roughike.bottombar.BottomBar;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.sound.presentation.views.custom.CardViewAudioTrack;
import com.videonasocialmedia.vimojo.sound.presentation.views.custom.CardViewAudioTrackListener;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by ruth on 4/10/16.
 */

public class SoundActivity extends EditorActivity implements SoundView,
    VideonaPlayer.VideonaPlayerListener, CardViewAudioTrackListener{

  private static final String TAG = "SoundActivity";
  private final int ID_BUTTON_FAB_TOP=1;
  private final int ID_BUTTON_FAB_BOTTOM=3;

  @Inject SoundPresenter presenter;

  @Nullable @BindView( R.id.bottomBar)
  BottomBar bottomBar;
  @Nullable @BindView(R.id.relative_layout_activity_sound)
  RelativeLayout relativeLayoutActivitySound;

  @Nullable @BindView(R.id.cardview_audio_blocks_clips_video)
  CardViewAudioTrack videoTrack;
  @Nullable @BindView(R.id.cardview_audio_blocks_clips_audio_track_first)
  CardViewAudioTrack firstAudioTrack;
  @Nullable @BindView(R.id.cardview_audio_blocks_clips_audio_track_second)
  CardViewAudioTrack secondAudioTrack;
  @Nullable @BindView(R.id.scrollview_timeline_audio_blocks)
  ScrollView scrollViewTimeLineAudioBlocks;
  @Nullable @BindView(R.id.button_sound_warning_transcoding_file)
  ImageButton warningTranscodingFilesButton;

  @BindView(R.id.edit_activity_drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.coordinatorLayout)
  CoordinatorLayout coordinatorLayout;

  private int currentVideoIndex = 0;

  private boolean voiceOverActivated;
  private String warningTranscodingFilesMessage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      inflateLinearLayout(R.id.container_layout,R.layout.activity_sound);
      inflateLinearLayout(R.id.container_navigator,R.layout.sound_activity_layout_button_navigator);
      ButterKnife.bind(this);
      getActivityPresentersComponent().inject(this);
      bottomBar.selectTabWithId(R.id.tab_sound);
      setupBottomBar(bottomBar);
      setVideonaPlayerListener(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.updatePresenter();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  private void setupBottomBar(BottomBar bottomBar) {
    bottomBar.setOnTabSelectListener(tabId -> {
      switch (tabId){
        case(R.id.tab_editactivity):
          navigateTo(EditActivity.class);
          break;
        case (R.id.tab_share):
          navigateTo(ShareActivity.class);
          break;
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
      if (voiceOverActivated) {
        getMenuInflater().inflate(R.menu.menu_sound_activity, menu);
      } else {
        getMenuInflater().inflate(R.menu.menu_sound_activity_basic, menu);
      }
      return true;
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    switch (item.getItemId()) {
      case R.id.action_toolbar_record:
        navigateTo(RecordCamera2Activity.class);
        return true;
      case R.id.action_toolbar_music:
        navigateTo(MusicListActivity.class);
        return true;
      case R.id.action_toolbar_voice_over:
        navigateTo(VoiceOverRecordActivity.class);
        return true;
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
      default:
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void hideVoiceOverTrack() {
    secondAudioTrack.setVisibility(View.GONE);
  }

  @Override
  public void addVoiceOverOptionToToolbar() {
    voiceOverActivated = true;
  }

  @Override
  public void setVideoVolume(float volume) {
    //super needed, avoid recursion
    super.setVideoVolume(volume);
  }

  @Override
  public void setVoiceOverVolume(float volume) {
    //super needed, avoid recursion
    super.setVoiceOverVolume(volume);
  }

  @Override
  public void setMusicVolume(float volume) {
    //super needed, avoid recursion
    runOnUiThread(() -> {
      super.setMusicVolume(volume);
    });
  }

  @Override
  public void bindTrack(Track track) {
    runOnUiThread(() -> {
      switch (track.getId()) {
        case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_MEDIA_TRACK:
          videoTrack.setListener(this);
          videoTrack.setTrack(track);
          break;
        case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC:
          if (track.getPosition() == 1) {
            firstAudioTrack.setListener(this);
            firstAudioTrack.setTrack(track);
          } else {
            secondAudioTrack.setListener(this);
            secondAudioTrack.setTrack(track);
          }
          break;
        case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
          if (track.getPosition() == 1) {
            firstAudioTrack.setListener(this);
            firstAudioTrack.setTrack(track);
          } else {
            secondAudioTrack.setListener(this);
            secondAudioTrack.setTrack(track);
          }
          break;
      }
    });
  }

  @Override
  public void showTrackVideo() {
    videoTrack.setVisibility(View.VISIBLE);
  }

  @Override
  public void showTrackAudioFirst() {
    firstAudioTrack.setVisibility(View.VISIBLE);
  }

  @Override
  public void showTrackAudioSecond() {
    secondAudioTrack.setVisibility(View.VISIBLE);
  }

  @Override
  public void showWarningTempFile() {
    warningTranscodingFilesButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void setWarningMessageTempFile(String messageTempFile) {
    warningTranscodingFilesMessage = messageTempFile;
  }

  @Override
  public void updatePlayer() {
    updatePlayerVideos();
    seekToClip(currentVideoIndex);
  }

  @Override
  public void navigateToMusicDetail(Class<MusicDetailActivity> musicDetailActivityClass, String mediaPath) {
    Intent i = new Intent(this, MusicDetailActivity.class);
    i.putExtra(IntentConstants.MUSIC_DETAIL_SELECTED, mediaPath);
    startActivity(i);
  }

  @Override
  public void navigateToMusicList(Class<MusicListActivity> musicListActivityClass) {
    Intent intent = new Intent(this, musicListActivityClass);
    startActivity(intent);  }

  @Override
  public void showError(String message) {
    runOnUiThread(() -> {
      Snackbar snackbar = Snackbar.make(coordinatorLayout, message,
          Snackbar.LENGTH_LONG);
      snackbar.show();
    });
  }

  @Override
  public void resetPlayer() {
    // update player in EditorActivity
    runOnUiThread(() -> {
      super.resetPlayer();
    });
  }

  @Override
  public void updateAudioTracks() {
    runOnUiThread(() -> {
      firstAudioTrack.setVisibility(View.GONE);
      secondAudioTrack.setVisibility(View.GONE);
      focusOnView(videoTrack);
      scrollViewTimeLineAudioBlocks.scrollTo(0, 0);
    });
  }

  @Nullable @Override
  public void newClipPlayed(int currentClipIndex) {
    this.currentVideoIndex = currentClipIndex;
    videoTrack.updateClipSelection(currentClipIndex);
  }

  @Override
  public void playerReady() {
    // Do nothing
  }

  @Override
  public void updatedSeekbarProgress(int progress) {
    // Do nothing
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
        focusOnView(videoTrack);
        break;
      case 1:
        focusOnView(firstAudioTrack);
        break;
      case 2:
        focusOnView(secondAudioTrack);
        break;
    }
  }

  @Override
  public void onClickMediaClip(int position, int trackId) {
    seekToClip(position);
    presenter.updateClipPlayed(trackId);
    // TODO:(alvaro.martinez) 31/05/17 If Vimojo support more than one music or voice over, update and calculate correct position
    switch (trackId){
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_MEDIA_TRACK:
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC:
        videoTrack.updateClipSelection(position);
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        videoTrack.updateClipSelection(position);
        break;
    }
  }

  @Override
  public void onClickDeleteAudio(int id) {
    pausePreview();
    boolean deleteMusic = false;
    boolean deleteVoiceOver = false;
    switch (id){
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC:
        deleteMusic = true;
        break;
      case com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        deleteVoiceOver = true;
        break;
    }
    // Dialog delete music
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setMessage(getResources().getString(R.string.dialog_sound_delete_music));
    boolean finalDeleteMusic = deleteMusic;
    boolean finalDeleteVoiceOver = deleteVoiceOver;
    final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          if (finalDeleteMusic) {
            presenter.deleteMusic();
            break;
          }
          if (finalDeleteVoiceOver) {
            presenter.deleteVoiceOver();
            break;
          }
          break;
        case DialogInterface.BUTTON_NEGATIVE:

          break;
      }
    };
    builder.setCancelable(true)
        .setPositiveButton(R.string.dialog_sound_accept_delete, dialogClickListener)
        .setNegativeButton(R.string.dialog_sound_cancel_delete, dialogClickListener).show();
  }

  private final void focusOnView(final View view) {
    scrollViewTimeLineAudioBlocks.post(() -> scrollViewTimeLineAudioBlocks.scrollTo(0, view.getTop()));
  }


  @Optional @OnClick(R.id.button_sound_warning_transcoding_file)
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
