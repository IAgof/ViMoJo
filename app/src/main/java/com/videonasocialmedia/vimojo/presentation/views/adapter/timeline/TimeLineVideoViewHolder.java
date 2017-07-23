package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.VideoTimeLineAdapter;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.videonamediaframework.utils.TimeUtils.toFormattedTimeHoursMinutesSecond;

/**
 * Created by jliarte on 28/04/17.
 */
public class TimeLineVideoViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
  private VideoTimeLineAdapter videoTimeLineAdapter;
  private final VideoTimeLineRecyclerViewClickListener videoTimeLineListener;
  @Bind(R.id.timeline_video_thumb)
  public ImageView thumb;
  @Bind(R.id.text_clip_order)
  TextView thumbOrder;
  @Bind(R.id.text_duration_clip)
  TextView textDurationClip;
  @Bind(R.id.image_remove_video)
  ImageView removeVideo;
  @Bind(R.id.image_video_warning)
  ImageView videoWarning;
  private int selectedColor;

  public TimeLineVideoViewHolder(VideoTimeLineAdapter videoTimeLineAdapter, View videoItem,
                                 VideoTimeLineRecyclerViewClickListener videoTimeLineListener) {
    super(videoItem);
    ButterKnife.bind(this, videoItem);
    this.videoTimeLineAdapter = videoTimeLineAdapter;
    this.videoTimeLineListener = videoTimeLineListener;
    this.selectedColor = VimojoApplication.getAppContext()
            .getResources().getColor(R.color.colorPrimary);
  }

  public void bindData(Video video, int position, int selectedVideoPosition) {
    this.thumb.setSelected(position == selectedVideoPosition);
    this.thumbOrder.setText(String.valueOf(position + 1));
//      this.thumbOrder.setText(String.valueOf(video.getPosition()));
    drawVideoThumbnail(this.thumb, video);
    String duration = toFormattedTimeHoursMinutesSecond(video.getDuration());
    this.textDurationClip.setText(duration);
    if (position == selectedVideoPosition) {
      enableDeleteIcon();
    } else {
      disableDeleteIcon();
    }
  }

  public void drawVideoThumbnail(ImageView thumbnailView, Video currentVideo) {
    int microSecond = currentVideo.getStartTime() * 1000;
    Context context = thumbnailView.getContext();
    BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
    //TODO, review Glide and how to manage cache thumbs
    FileDescriptorBitmapDecoder decoder = new FileDescriptorBitmapDecoder(
            new VideoBitmapDecoder(microSecond),
            bitmapPool,
            DecodeFormat.PREFER_ARGB_8888);
    String path = currentVideo.getIconPath() != null
            ? currentVideo.getIconPath() : currentVideo.getMediaPath();

    Glide.with(context)
            .load(path)
            .asBitmap()
            .override(thumbnailView.getMaxWidth(), thumbnailView.getMaxHeight())
            // TODO(jliarte): 24/04/17 this seems not to work
            .override(100, 100)
//                .override(thumbnailView.getMeasuredWidth(), thumbnailView.getMeasuredHeight())
            .videoDecoder(decoder)
            .centerCrop()
            .error(R.drawable.fragment_gallery_no_image)
            .into(thumbnailView);
  }

  public void enableDeleteIcon() {
    removeVideo.setVisibility(View.VISIBLE);
    removeVideo.setClickable(true);
  }

  public void disableDeleteIcon() {
    removeVideo.setVisibility(View.GONE);
    removeVideo.setClickable(false);
  }

  public void enableWarningIcon() {
    videoWarning.setVisibility(View.VISIBLE);
  }

  public void diableWarningIcon() {
    videoWarning.setVisibility(View.GONE);
  }

  @OnClick(R.id.timeline_video_thumb)
  public void onClipClick() {
    int adapterPosition = getAdapterPosition();
    videoTimeLineAdapter.updateSelection(adapterPosition);
    enableDeleteIcon();
    videoTimeLineListener.onClipClicked(adapterPosition);
  }

  @Override
  public void onItemSelected() {
    itemView.setBackgroundColor(selectedColor);
  }

  @Override
  public void onItemClear() {
    videoTimeLineAdapter.updateSelection(getAdapterPosition());
    itemView.setBackgroundColor(0);
    // (jliarte): 26/04/17 workarround to clear selected items forcing to refresh all
    videoTimeLineAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.image_remove_video)
  public void onDeleteIconClick() {
    videoTimeLineAdapter.remove(getAdapterPosition());
  }
}
