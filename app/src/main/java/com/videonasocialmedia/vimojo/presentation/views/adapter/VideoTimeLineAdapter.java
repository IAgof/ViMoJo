package com.videonasocialmedia.vimojo.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.VideoTimeLineTouchHelperCallbackAdapter;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.videonamediaframework.utils.TimeUtils.toFormattedTimeHoursMinutesSecond;

/**
 * Created by jliarte on 24/04/17.
 */

public class VideoTimeLineAdapter
        extends RecyclerView.Adapter<VideoTimeLineAdapter.TimeLineVideoViewHolder>
        implements VideoTimeLineTouchHelperCallbackAdapter {
  private final String TAG = VideoTimeLineAdapter.class.getCanonicalName();
  private List<Video> videoList;
  private int selectedVideoPosition;
  private VideoTimeLineRecyclerViewClickListener videoTimeLineListener;

  public VideoTimeLineAdapter(VideoTimeLineRecyclerViewClickListener listener) {
    this.videoList = new ArrayList<>();
    this.setVideoTimeLineListener(listener);
  }

  private void setVideoTimeLineListener(VideoTimeLineRecyclerViewClickListener videoTimeLineListener) {
    this.videoTimeLineListener = videoTimeLineListener;
  }

  @Override
  public TimeLineVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context)
            .inflate(R.layout.edit_videotimeline_video_item, parent, false);
    return new TimeLineVideoViewHolder(view, videoTimeLineListener);
  }

  @Override
  public void onBindViewHolder(TimeLineVideoViewHolder holder, int position) {
    Video currentVideo = videoList.get(position);
    holder.bindData(currentVideo, position, selectedVideoPosition);
  }

  public void updateSelection(int position) {
    notifyItemChanged(selectedVideoPosition);
    this.selectedVideoPosition = position;
    notifyItemChanged(position);
  }

  public void remove(int selectedVideoRemovePosition) {
    videoTimeLineListener.onClipRemoveClicked(selectedVideoRemovePosition);
  }

  public void updateVideoList(List<Video> videoList) {
    this.videoList = videoList;
    notifyDataSetChanged();
    Log.d(TAG, "timeline: videoList updated!");
//    updateSelection(0);
  }

  @Override
  public int getItemCount() {
    return videoList.size();
  }

  @Override
  public boolean onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(videoList, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(videoList, i, i - 1);
      }
    }
    Log.d(TAG, "timeline: adapter move from " + fromPosition + " to: " + toPosition);
    notifyItemMoved(fromPosition, toPosition);
    videoTimeLineListener.onClipMoved(fromPosition, toPosition);
    return true;
  }

  @Override
  public void onItemDismiss(int position) {
    // (jliarte): 27/04/17 swipe to dismiss not configured
  }

  @Override
  public void finishMovement(int adapterPosition) {
    videoTimeLineListener.onClipReordered(adapterPosition);
  }

  protected int getSelectedVideoPosition() {
    return selectedVideoPosition;
  }

  public class TimeLineVideoViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
    private final VideoTimeLineRecyclerViewClickListener videoTimeLineListener;
    @Bind(R.id.timeline_video_thumb)
    public ImageView thumb;
    @Bind(R.id.text_clip_order)
    TextView thumbOrder;
    @Bind(R.id.text_duration_clip)
    TextView textDurationClip;
    @Bind(R.id.image_remove_video)
    ImageView removeVideo;
    private int selectedColor;

    public TimeLineVideoViewHolder(View videoItem,
                                   VideoTimeLineRecyclerViewClickListener videoTimeLineListener) {
      super(videoItem);
      ButterKnife.bind(this, videoItem);
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

    @OnClick(R.id.timeline_video_thumb)
    public void onClipClick() {
      int adapterPosition = getAdapterPosition();
      updateSelection(adapterPosition);
      enableDeleteIcon();
      videoTimeLineListener.onClipClicked(adapterPosition);
    }

    @Override
    public void onItemSelected() {
//      lastSelectedPosition = getSelectedVideoPosition();
//      thumb.setRotation(20);
//      videoTimeLineListener.onClipLongClicked(getAdapterPosition());
      itemView.setBackgroundColor(selectedColor);
    }

    @Override
    public void onItemClear() {
      updateSelection(getAdapterPosition());
      itemView.setBackgroundColor(0);
      // (jliarte): 26/04/17 workarround to clear selected items forcing to refresh all
      notifyDataSetChanged();
    }

    @OnClick(R.id.image_remove_video)
    public void onDeleteIconClick() {
      remove(getAdapterPosition());
    }
  }
}
