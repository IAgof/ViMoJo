package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.VideoTimeLineTouchHelperCallbackAdapter;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jliarte on 24/04/17.
 */

public class VideoTimeLineAdapter
        extends RecyclerView.Adapter<TimeLineVideoViewHolder>
        implements VideoTimeLineTouchHelperCallbackAdapter {
  private final String TAG = VideoTimeLineAdapter.class.getCanonicalName();
  private List<Video> videoList;
  private int selectedVideoPosition;
  private VideoTimeLineRecyclerViewClickListener videoTimeLineListener;
  private final HashMap<Video, TimeLineVideoViewHolder> videoViewHolders = new HashMap<>();
  private ArrayList<Video> failedVideos = new ArrayList<>();

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
    return new TimeLineVideoViewHolder(this, view, videoTimeLineListener);
  }

  @Override
  public void onBindViewHolder(TimeLineVideoViewHolder holder, int position) {
    Video currentVideo = videoList.get(position);
    holder.bindData(currentVideo, position, selectedVideoPosition);
    this.videoViewHolders.put(currentVideo, holder);
    if (failedVideos.indexOf(currentVideo) > 0) {
      holder.enableWarningIcon();
    }
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

  public void setFailedVideos(ArrayList<Video> failedVideod) {
    this.failedVideos = failedVideod;
  }
}
