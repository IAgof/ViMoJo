package com.videonasocialmedia.vimojo.sound.presentation.views.custom;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;

/**
 * Created by alvaro on 10/04/17.
 */

public class CardViewAudioTrack extends CardView implements CardViewTrack {

  private final View cardViewTrack;
  private ImageView imageAudioBlocks;
  private RecyclerView recyclerViewTimeLineAudioBlocks;
  private TextView textTitleAudioBlocks;
  private SeekBar seekBarAudioBlock;
  private Switch switchMuteAudio;
  private Switch switchSoloAudio;
  private RelativeLayout relativeLayoutAudioVolume;

  private CardViewAudioTrackListener listener;
  private int id;

  public CardViewAudioTrack(Context context){
    super(context);
    this.cardViewTrack = ((Activity) getContext()).getLayoutInflater()
        .inflate(R.layout.custom_sound_cardview_track, this, true);
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
    initLayoutsComponents();
  }

  private void initLayoutsComponents() {
    imageAudioBlocks = (ImageView) findViewById(R.id.image_audio_blocks);
    imageAudioBlocks.setOnClickListener(onClickImageTrackListener());
    recyclerViewTimeLineAudioBlocks = (RecyclerView) findViewById(R.id.recyclerview_editor_timeline_audio_blocks);
    textTitleAudioBlocks = (TextView) findViewById(R.id.volume_view_title);
    seekBarAudioBlock = (SeekBar) findViewById(R.id.seekBar_volume_sound);
    seekBarAudioBlock.setOnSeekBarChangeListener(onClickSeekBarProgressListener());
    switchMuteAudio = (Switch) findViewById(R.id.switchMute);
    switchMuteAudio.setOnCheckedChangeListener(onClickMuteAudioListener());
    switchSoloAudio = (Switch) findViewById(R.id.switchSolo);
    switchSoloAudio.setOnCheckedChangeListener(onClickSoloAudioListener());
    relativeLayoutAudioVolume = (RelativeLayout) findViewById(R.id.relative_layout_audio_volume);
  }

  private SeekBar.OnSeekBarChangeListener onClickSeekBarProgressListener() {
    return new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
       listener.setSeekBarProgress(progress, id);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    };
  }

  private CompoundButton.OnCheckedChangeListener onClickSoloAudioListener() {
    return new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.setSwitchSoloAudio(isChecked, id);
      }
    };
  }

  private CompoundButton.OnCheckedChangeListener onClickMuteAudioListener() {
    return new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.setSwitchMuteAudio(isChecked, id);
      }
    };
  }


  @NonNull
  private OnClickListener onClickImageTrackListener() {
    return new OnClickListener() {
      @Override
      public void onClick(View v) {
        if(relativeLayoutAudioVolume.getVisibility() == View.VISIBLE){
          relativeLayoutAudioVolume.setVisibility(View.GONE);
          return;
        }
        if(relativeLayoutAudioVolume.getVisibility() == View.GONE){
          relativeLayoutAudioVolume.setVisibility(View.VISIBLE);
          return;
        }
      }
    };
  }


  @Override
  public void setSeekBar(int progress) {
    seekBarAudioBlock.setProgress(progress);
  }

  @Override
  public void setSwitchMuteAudio(boolean state) {
    switchMuteAudio.setChecked(state);
  }

  @Override
  public void setSwitchSoloAudio(boolean state) {
    switchSoloAudio.setChecked(state);
  }

  @Override
  public void setTitleTrack(String title) {
    textTitleAudioBlocks.setText(title);
  }

  @Override
  public void setImageTrack(int resourceId) {
    imageAudioBlocks.setImageResource(resourceId);
  }

  @Override
  public RecyclerView getRecyclerView() {
    return recyclerViewTimeLineAudioBlocks;
  }

  @Override
  public void setListener(CardViewAudioTrackListener listener, int id) {
    this.listener = listener;
    this.id = id;
  }
}
