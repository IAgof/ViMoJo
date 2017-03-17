package com.videonasocialmedia.vimojo.sound.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
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

public class AudioTimeLineAdapter extends RecyclerView.Adapter<AudioTimeLineAdapter.AudioViewHolder> {

  private AudioTimeLineRecyclerViewClickListener audioTimeLineListener;
  private Context context;
  private List<Video> audioList;
  private int selectedVideoPosition = 0;

  public AudioTimeLineAdapter(AudioTimeLineRecyclerViewClickListener audioTimeLineListener){
    this.audioTimeLineListener = audioTimeLineListener;
    this.audioList = new ArrayList<>();
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
    Video videoCurrent = audioList.get(position);
    drawVideoThumbnail(holder.audioThumb, videoCurrent);
    holder.audioThumb.setSelected(position == selectedVideoPosition);
    String duration = TimeUtils.toFormattedTimeWithMinutesAndSeconds(videoCurrent.getStartTime()) + " - "
        + TimeUtils.toFormattedTimeWithMinutesAndSeconds(videoCurrent.getStopTime());
    holder.textDurationClip.setText(duration);
  }

  private void drawVideoThumbnail(ImageView thumbnailView, Video currentVideo) {
    String path = currentVideo.getIconPath() != null
        ? currentVideo.getIconPath() : currentVideo.getMediaPath();
    Glide.with(context)
        .load(path)
        .centerCrop()
        .error(R.drawable.fragment_gallery_no_image)
        .into(thumbnailView);
  }

  public void updateSelection(int positionSelected) {
    notifyItemChanged(selectedVideoPosition);
    selectedVideoPosition = positionSelected;
    notifyItemChanged(selectedVideoPosition);
  }

  @Override
  public int getItemCount() {
    return audioList.size();
  }

  public void setAudioList(List<Video> audioList) {
    this.audioList = audioList;
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
      int position = getAdapterPosition();
      //updateSelection(position);
      audioTimeLineListener.onAudioClipClicked(position);
    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {

    }
  }
}
