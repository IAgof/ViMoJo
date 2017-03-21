package com.videonasocialmedia.vimojo.sound.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.AudioTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 7/03/17.
 */

public class MusicTimeLineAdapter extends RecyclerView.Adapter<MusicTimeLineAdapter.AudioViewHolder> {

  private AudioTimeLineRecyclerViewClickListener audioTimeLineListener;
  private Context context;
  private List<Music> musicList;
  private int selectedVideoPosition = 0;

  public MusicTimeLineAdapter(AudioTimeLineRecyclerViewClickListener audioTimeLineListener){
    this.audioTimeLineListener = audioTimeLineListener;
    this.musicList = new ArrayList<>();
  }

  @Override
  public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    this.context = parent.getContext();
    View view = LayoutInflater.from(context)
        .inflate(R.layout.edit_audiotimeline_audio_item, parent, false);
    return new AudioViewHolder(view);
  }

  @Override
  public void onBindViewHolder(AudioViewHolder holder, int position) {
    Music musicCurrent = musicList.get(position);
    if(musicCurrent.getMusicTitle().compareTo(com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE) == 0) {
      // TODO:(alvaro.martinez) 21/03/17 Implements thumb voice over with respect Video in project
      //drawAudioThumb(holder.audioThumb, musicCurrent);
      holder.audioThumb.setImageResource(R.drawable.activity_edit_audio_voice_over_icon);
    } else {
      holder.audioThumb.setImageResource(musicCurrent.getIconResourceId());
    }
    holder.audioThumb.setSelected(position == selectedVideoPosition);
    String duration = TimeUtils.toFormattedTimeWithMinutesAndSeconds(musicCurrent.getStartTime()) + " - "
        + TimeUtils.toFormattedTimeWithMinutesAndSeconds(musicCurrent.getDuration());
    holder.textDurationClip.setText(duration);
  }

  private void drawAudioThumb(ImageView audioThumb, Music musicCurrent) {
    Glide.with(context)
        .load(musicCurrent.getIconPath()) //getVideoPathLinkedToVoiceOver())
        .centerCrop()
        .error(R.drawable.fragment_gallery_no_image)
        .into(audioThumb);
  }

  @Override
  public int getItemCount() {
    return musicList.size();
  }

  public void setMusicList(List<Music> audioList) {
    this.musicList = audioList;
  }

  class AudioViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    @Bind(R.id.text_duration_clip)
    TextView textDurationClip;
    @Bind(R.id.timeline_audio_thumb)
    ImageView audioThumb;

    public AudioViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.timeline_audio_thumb)
    public void onClipClick() {
      if(musicList.get(getAdapterPosition()).getMusicTitle().
          compareTo(com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE) == 0){
        audioTimeLineListener.onVoiceOverClipClicked(getAdapterPosition());
      } else {
        audioTimeLineListener.onMusicClipClicked(getAdapterPosition());
      }
    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {

    }
  }
}
