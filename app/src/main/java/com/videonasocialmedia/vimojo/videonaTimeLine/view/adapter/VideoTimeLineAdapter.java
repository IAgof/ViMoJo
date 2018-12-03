package com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.helper.VideoTimeLineTouchHelperCallbackAdapterListener;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.customview.TimeLineVideoViewHolder;
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
        implements VideoTimeLineTouchHelperCallbackAdapterListener {
  private final String LOG_TAG = VideoTimeLineAdapter.class.getCanonicalName();
  private List<Video> videoList;
  private int selectedVideoPosition;
  private VideoTimeLineRecyclerViewClickListener videoTimeLineListener;
  private final HashMap<Video, TimeLineVideoViewHolder> videoViewHolders = new HashMap<>();
  private ArrayList<Video> failedVideos = new ArrayList<>();
  private int initPosition;
  private int endPosition = -1;

  public VideoTimeLineAdapter(VideoTimeLineRecyclerViewClickListener listener) {
    this.videoList = new ArrayList<>();
    this.setVideoTimeLineListener(listener);
  }

  private void setVideoTimeLineListener(
          VideoTimeLineRecyclerViewClickListener videoTimeLineListener) {
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
    if (failedVideos.indexOf(currentVideo) >= 0) {
      holder.enableWarningIcon();
    } else {
      holder.disableWarningIcon();
    }
  }

  public void updateSelection(int position) {
    if (selectedVideoPosition == position) {
      Log.d(LOG_TAG, "timeline: updateSelection same position");
      return;
    }
    notifyItemChanged(selectedVideoPosition);
    this.selectedVideoPosition = position;
    notifyItemChanged(position);
    Log.d(LOG_TAG, "timeline: updateSelection position " + position);
  }

  public void remove(int selectedVideoRemovePosition) {
    Log.d(LOG_TAG, "timeline: remove position " + selectedVideoRemovePosition);
    videoTimeLineListener.onClipRemoveClicked(selectedVideoRemovePosition);
  }

  public void updateVideoList(List<Video> videoList) {
    this.videoList = videoList;
    notifyDataSetChanged();
    Log.d(LOG_TAG, "timeline: videoList updated!, videolist size " + videoList.size());
  }

  @Override
  public int getItemCount() {
    return videoList.size();
  }

  @Override
  public boolean onItemMove(int fromPosition, int toPosition) {
    Log.d(LOG_TAG, "timeline: onItemMove adapter move from " + fromPosition + " to: " + toPosition);
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(videoList, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(videoList, i, i - 1);
      }
    }
    notifyItemMoved(fromPosition, toPosition);
    videoTimeLineListener.onClipMoving(fromPosition, toPosition);
    Log.d(LOG_TAG, "timeline.onItemMove: setting end position to " + toPosition);
    endPosition = toPosition;
    return true;
  }

  @Override
  public void finishMovement() {
    Log.d(LOG_TAG, "timeline: finishMovement initPosition " + initPosition + " to " + endPosition);
    if (endPosition >= 0) {
      videoTimeLineListener.onClipReordered(initPosition, endPosition);
      resetEndPosition();
    }
  }

  private void resetEndPosition() {
    endPosition = -1;
    Log.d(LOG_TAG, "timeline.resetEndPosition: setting end position to " + endPosition);
  }

  protected int getSelectedVideoPosition() {
    return selectedVideoPosition;
  }

  public void setFailedVideos(ArrayList<Video> failedVideos) {
    this.failedVideos = failedVideos;
    notifyDataSetChanged();
  }

  public void initMovement(int position) {
    initPosition = position;
    endPosition = position;
    Log.d(LOG_TAG, "timeline.initMovement: setting end position to " + endPosition);
  }
}
