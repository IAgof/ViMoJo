package com.videonasocialmedia.vimojo.sound.presentation.views.custom;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MediaListTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.adapter.MediaListTimeLineAdapter;

/**
 * Created by alvaro on 10/04/17.
 */

public class CardViewAudioTrack extends CardView implements CardViewTrack,
    MediaListTimeLineRecyclerViewClickListener{

  private final View cardViewTrack;
  private ImageView imageMediaBlocks;
  private RecyclerView recyclerViewTimeLineMediaBlocks;
  private TextView textTitleMediaBlocks;
  private SeekBar seekBarMediaBlock;
  private Switch switchMuteMedia;
  private RelativeLayout relativeLayoutMediaVolume;

  private CardViewAudioTrackListener listener;
  private int id;
  private int position;
  private boolean activatedUiAudioOptions = false;

  private MediaListTimeLineAdapter mediaListTimeLineAdapter;
  private RecyclerView.LayoutManager mediaListLayoutManager;

  private int num_grid_columns = 1;
  int orientation = LinearLayoutManager.HORIZONTAL;
  private Context context;


  public CardViewAudioTrack(Context context){
    super(context);
    this.cardViewTrack = ((Activity) getContext()).getLayoutInflater()
        .inflate(R.layout.custom_sound_cardview_track, this, true);
    this.context = context;
    initLayoutsComponents();
  }

  /**
   * Constructor with attributes.
   *
   * @param context view context
   * @param attrs   view attributes
   */
  public CardViewAudioTrack(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.cardViewTrack = ((Activity) getContext()).getLayoutInflater()
        .inflate(R.layout.custom_sound_cardview_track, this, true);
    this.context = context;
    initLayoutsComponents();
  }

  /**
   * Contructor with attributes and style.
   *
   * @param context  view context
   * @param attrs    view attributes
   * @param defStyle view style
   */
  public CardViewAudioTrack(Context context, AttributeSet attrs, int defStyle) {
    super(context,attrs,defStyle);
    this.cardViewTrack = ((Activity) getContext()).getLayoutInflater()
        .inflate(R.layout.custom_sound_cardview_track, this, true);
    this.context = context;
    initLayoutsComponents();
  }

  public void setTrack(Track track) {
    this.id = track.getId();
    this.position = track.getPosition();
    initUIComponentsById(track.getId());
    initAdapter(track);
    setSeekBar((int) (track.getVolume()*100));
    setSwitchMuteMedia(track.isMuted());
  }

  public void setListener(CardViewAudioTrackListener listener){
    this.listener = listener;
  }

  private void initUIComponentsById(int id) {
    switch (id) {
      case Constants.INDEX_MEDIA_TRACK:
        setImageTrack(R.drawable.activity_edit_sound_original_down);
        setTitleTrack(context.getString(R.string.title_track_clip_video));
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        setImageTrack(R.drawable.activity_edit_sound_music_down);
        setTitleTrack(context.getString(R.string.title_track_clip_music));
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        setImageTrack(R.drawable.activity_edit_sound_voice_over_down);
        setTitleTrack(context.getString(R.string.title_track_clip_voice_over));
        break;
    }
  }

  private void initAdapter(Track track) {
    mediaListLayoutManager = new GridLayoutManager(context, num_grid_columns,
        orientation, false);
    recyclerViewTimeLineMediaBlocks.setLayoutManager(mediaListLayoutManager);
    mediaListTimeLineAdapter = new MediaListTimeLineAdapter(this, track.getId());
    recyclerViewTimeLineMediaBlocks.setAdapter(mediaListTimeLineAdapter);
    mediaListTimeLineAdapter.setMediaList(track.getItems());
  }

  private void initLayoutsComponents() {
    imageMediaBlocks = (ImageView) findViewById(R.id.image_audio_blocks);
    imageMediaBlocks.setOnClickListener(onClickImageTrackListener());
    recyclerViewTimeLineMediaBlocks = (RecyclerView) findViewById(R.id.recyclerview_editor_timeline_audio_blocks);
    textTitleMediaBlocks = (TextView) findViewById(R.id.volume_view_title);
    seekBarMediaBlock = (SeekBar) findViewById(R.id.seekBar_volume_sound);
    seekBarMediaBlock.setOnSeekBarChangeListener(onClickSeekBarProgressListener());
    switchMuteMedia = (Switch) findViewById(R.id.switchMute);
    switchMuteMedia.setOnCheckedChangeListener(onClickMuteAudioListener());
    relativeLayoutMediaVolume = (RelativeLayout) findViewById(R.id.relative_layout_audio_volume);
  }

  private SeekBar.OnSeekBarChangeListener onClickSeekBarProgressListener() {
    return new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        listener.setSeekBarProgress(id, progress);
//        switchMuteMedia.setChecked(false);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO(jliarte): 14/09/18 moved here to set volume only when user relases seekbar
        listener.setSeekBarProgress(id, seekBar.getProgress());
        switchMuteMedia.setChecked(false);
      }
    };
  }

  private CompoundButton.OnCheckedChangeListener onClickMuteAudioListener() {
    return new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.setSwitchMuteAudio(id, isChecked);
      }
    };
  }


  @NonNull
  private OnClickListener onClickImageTrackListener() {
    return new OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickImageIconTrack(id);
        listener.onClickExpandInfoTrack(position);
        if (relativeLayoutMediaVolume.getVisibility() == View.VISIBLE) {
          relativeLayoutMediaVolume.setVisibility(View.GONE);
          activatedUiAudioOptions = false;
          return;
        }
        if (relativeLayoutMediaVolume.getVisibility() == View.GONE) {
          relativeLayoutMediaVolume.setVisibility(View.VISIBLE);
          activatedUiAudioOptions = true;
          return;
        }
      }
    };
  }

  private void onClickImageIconTrack(int id) {
    switch (id){
      case Constants.INDEX_MEDIA_TRACK:
        updateImageIconTrackVideo();
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        updateImageIconTrackMusic();
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        updateImageIconTrackVoiceOver();
        break;
    }
  }

  private void updateImageIconTrackVideo() {
    if(isShowedAudioTrackOptions()) {
      setImageTrack(R.drawable.activity_edit_sound_original_down);
    } else {
      setImageTrack(R.drawable.activity_edit_sound_original_up);
    }
  }

  private void updateImageIconTrackMusic() {
    if(isShowedAudioTrackOptions()) {
      setImageTrack(R.drawable.activity_edit_sound_music_down);
    } else {
      setImageTrack(R.drawable.activity_edit_sound_music_up);
    }
  }

  private void updateImageIconTrackVoiceOver() {
    if(isShowedAudioTrackOptions()) {
      setImageTrack(R.drawable.activity_edit_sound_voice_over_down);
    } else {
      setImageTrack(R.drawable.activity_edit_sound_voice_over_up);
    }
  }


  @Override
  public void setSeekBar(int progress) {
    seekBarMediaBlock.setProgress(progress);
  }

  public void setSwitchMuteMedia(boolean state) {
    switchMuteMedia.setChecked(state);
  }

  @Override
  public void setTitleTrack(String title) {
    textTitleMediaBlocks.setText(title);
  }

  @Override
  public void setImageTrack(int resourceId) {
    imageMediaBlocks.setImageResource(resourceId);
  }

  @Override
  public boolean isShowedAudioTrackOptions() {
    return activatedUiAudioOptions;
  }

  @Override
  public void onClipClicked(int position, int trackId) {
        listener.onClickMediaClip(position, trackId);
    switch (trackId){
      case Constants.INDEX_MEDIA_TRACK:
        onAudioClipClicked(position);
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        onMusicClipClicked(position);
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        onVoiceOverClipClicked(position);
    }
  }

  public void onAudioClipClicked(int position) {
    int lastPosition = mediaListTimeLineAdapter.getSelectedVideoPosition();
    mediaListTimeLineAdapter.updateSelection(position);
    if(lastPosition > position && position > 0) {
      recyclerViewTimeLineMediaBlocks.smoothScrollToPosition(--position);
    } else {
      recyclerViewTimeLineMediaBlocks.smoothScrollToPosition(++position);
    }
  }

  public void onMusicClipClicked(int position) {
    mediaListTimeLineAdapter.updateSelection(0);
    recyclerViewTimeLineMediaBlocks.smoothScrollToPosition(0);
  }

  public void onVoiceOverClipClicked(int position) {
    mediaListTimeLineAdapter.updateSelection(0);
    recyclerViewTimeLineMediaBlocks.smoothScrollToPosition(0);
  }

  public void updateClipSelection(int currentClipIndex) {
    mediaListTimeLineAdapter.updateSelection(currentClipIndex);
    recyclerViewTimeLineMediaBlocks.smoothScrollToPosition(currentClipIndex + 1);
  }
}
