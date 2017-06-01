package com.videonasocialmedia.vimojo.sound.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videonamediaframework.model.media.Audio;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MediaListTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 7/03/17.
 */

public class MediaListTimeLineAdapter extends RecyclerView.Adapter<MediaListTimeLineAdapter.MediaViewHolder> {

  private final int trackId;
  private MediaListTimeLineRecyclerViewClickListener mediaListTimeLineListener;
  private Context context;
  private List<Media> mediaList;

  private int selectedVideoPosition = 0;

  public MediaListTimeLineAdapter(MediaListTimeLineRecyclerViewClickListener mediaTimeLineListener,
                                  int trackId){
    this.mediaListTimeLineListener = mediaTimeLineListener;
    this.trackId = trackId;
    this.mediaList = new ArrayList<>();
  }

  @Override
  public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    this.context = parent.getContext();
    View view = LayoutInflater.from(context)
        .inflate(R.layout.sound_mediatimeline_media_item, parent, false);
    return new MediaViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MediaViewHolder holder, int position) {
    Media mediaCurrent = mediaList.get(position);
    drawMediaThumbnail(holder.audioThumb, mediaCurrent);
    holder.audioThumb.setSelected(position == selectedVideoPosition);
    String duration = getTimeForVideoInPosition(position);
    holder.textDurationClip.setText(duration);
  }

  private String getTimeForVideoInPosition(int position) {
    int startTime = 0;
    for(int i=0; i<position; i++){
     startTime += mediaList.get(i).getDuration();
    }
    int stopTime = startTime + mediaList.get(position).getDuration();

    return TimeUtils.toFormattedTimeWithMinutesAndSeconds(startTime) + " - "
        + TimeUtils.toFormattedTimeWithMinutesAndSeconds(stopTime);

  }

  private void drawMediaThumbnail(ImageView thumbnailView, Media currentMedia) {

    if(currentMedia instanceof Video) {
      String path = currentMedia.getIconPath() != null
          ? currentMedia.getIconPath() : currentMedia.getMediaPath();
      Glide.with(context)
          .load(path)
          .centerCrop()
          .error(R.drawable.fragment_gallery_no_image)
          .into(thumbnailView);
      return;
    }
    if(currentMedia instanceof Audio) {
      Glide.with(context)
          .load(currentMedia.getIconResourceId())
          .centerCrop()
          .error(R.drawable.fragment_gallery_no_image)
          .into(thumbnailView);
      return;
    }
  }

  public void updateSelection(int positionSelected) {
    notifyItemChanged(selectedVideoPosition);
    selectedVideoPosition = positionSelected;
    notifyItemChanged(selectedVideoPosition);

  }

  public int getSelectedVideoPosition() {
    return selectedVideoPosition;
  }

  @Override
  public int getItemCount() {
    return mediaList.size();
  }

  public void setMediaList(List<Media> mediaList) {
    this.mediaList = mediaList;
  }

  class MediaViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    @Bind(R.id.text_duration_clip)
    TextView textDurationClip;
    @Bind(R.id.timeline_audio_thumb)
    ImageView audioThumb;

    public MediaViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.timeline_audio_thumb)
    public void onClipClick() {
      int position = getAdapterPosition();
      mediaListTimeLineListener.onClipClicked(position, trackId);

    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {

    }
  }
}