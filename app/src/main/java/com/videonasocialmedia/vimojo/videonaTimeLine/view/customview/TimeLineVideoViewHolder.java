package com.videonasocialmedia.vimojo.videonaTimeLine.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.signature.StringSignature;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.VideoTimeLineAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.videonasocialmedia.videonamediaframework.utils.TimeUtils.toFormattedTimeHoursMinutesSecond;

/**
 * Created by jliarte on 28/04/17.
 */
public class TimeLineVideoViewHolder extends RecyclerView.ViewHolder
        implements ItemTouchHelperViewHolder {
  private static final String LOG_TAG = TimeLineVideoViewHolder.class.getSimpleName();
  private VideoTimeLineAdapter videoTimeLineAdapter;
  private final VideoTimeLineRecyclerViewClickListener videoTimeLineListener;
  @BindView(R.id.timeline_video_thumb)
  public ImageView thumb;
  @BindView(R.id.text_clip_order)
  TextView thumbOrder;
  @BindView(R.id.text_duration_clip)
  TextView textDurationClip;
  @BindView(R.id.image_remove_video)
  ImageView removeVideo;
  @BindView(R.id.image_video_warning)
  ImageView videoWarning;
  private int selectedColor;
  public static final int VIEWHOLDER_RADIUS = 70;
  public static final int VIEWHOLDER_MARGIN = 70;

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
    //    loadThumbnailWithMMR(thumbnailView, currentVideo);
    loadThumbnailWithGlide(thumbnailView, currentVideo);
  }

  private void loadThumbnailWithGlide(ImageView thumbnailView, Video currentVideo) {
    Context context = thumbnailView.getContext();
    BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
    FileDescriptorBitmapDecoder decoder = new FileDescriptorBitmapDecoder(
            new VideoBitmapDecoder(currentVideo.getStartTime() * 1000),
            bitmapPool,
            DecodeFormat.PREFER_ARGB_8888);
    String path = currentVideo.getIconPath() != null
            ? currentVideo.getIconPath() : currentVideo.getMediaPath();
    Glide.with(context)
            .load(path)
            .asBitmap()
            .override(thumbnailView.getMaxWidth(), thumbnailView.getMaxHeight())
            .videoDecoder(decoder)
            .transform(new RoundedCornersTransformation(context, VIEWHOLDER_RADIUS,
                    VIEWHOLDER_MARGIN))
            .signature(new StringSignature(currentVideo.getUuid() + currentVideo.getStartTime()))
            .error(R.drawable.fragment_gallery_no_image)
            .into(thumbnailView);
  }

  private void loadThumbnailWithMMR(ImageView thumbnailView, Video currentVideo) {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(currentVideo.getMediaPath());
    Bitmap thumbnail = retriever.getFrameAtTime(currentVideo.getStartTime() * 1000,
            MediaMetadataRetriever.OPTION_CLOSEST);
    thumbnailView.setImageBitmap(thumbnail);
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

  public void disableWarningIcon() {
    videoWarning.setVisibility(View.GONE);
  }

  @OnClick(R.id.timeline_video_thumb)
  public void onClipClick() {
    int adapterPosition = getAdapterPosition();
    videoTimeLineAdapter.updateSelection(adapterPosition);
    enableDeleteIcon();
    videoTimeLineListener.onClipClicked(adapterPosition);
  }

  @OnLongClick(R.id.timeline_video_thumb)
  public boolean onLongClick() {
    int adapterPosition = getAdapterPosition();
    videoTimeLineListener.onClipLongClicked(adapterPosition);
    return true;
  }

  @OnTouch(R.id.timeline_video_thumb)
  public boolean onTouch(View viewHolder, MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      int adapterPosition = getAdapterPosition();
      videoTimeLineAdapter.initMovement(adapterPosition);
      videoTimeLineListener.onClipClicked(adapterPosition);
      Log.d(LOG_TAG, "timeline: onTouch - start drag from " + adapterPosition);
    }
    return false; // Let the event to be processed further
  }


  @Override
  public void onItemSelected() {
    itemView.setSelected(true);
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
