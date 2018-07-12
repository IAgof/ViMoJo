package com.videonasocialmedia.vimojo.videonaTimeLine.view.customview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.VideoTimeLineAdapter;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.helper.VideoTimeLineTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jliarte on 8/07/18.
 */

public class VideonaTimeLine extends RelativeLayout implements VideoTimeLineRecyclerViewClickListener {
  // TODO(jliarte): 8/07/18 move package to SDK?
  private static final String LOG_TAG = VideonaTimeLine.class.getSimpleName();

  // TODO(jliarte): 8/07/18 make parametrizable?
  private final int NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL = 3;
  private final int NUM_COLUMNS_GRID_TIMELINE_VERTICAL = 4;

  // UI Components
  private RecyclerView videoListRecyclerView;
  private final View videonaTimeLineView;

  // Android helper classes
  private GridLayoutManager layoutManager;
  private ItemTouchHelper touchHelper;

  // Videona helper classes
  private VideoTimeLineAdapter timeLineAdapter;
  private VideoTimeLineTouchHelperCallback videoTimeLineTouchHelperCallback;
  private Listener listener;

  public VideonaTimeLine(Context context) {
    super(context);
    this.videonaTimeLineView = ((Activity) getContext()).getLayoutInflater()
            .inflate(R.layout.videona_time_line, this, true);
    initLayoutComponents();
    initTimeLineAdapterAndTouchHelper();
  }

  public VideonaTimeLine(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.videonaTimeLineView = ((Activity) getContext()).getLayoutInflater()
            .inflate(R.layout.videona_time_line, this, true);
    initLayoutComponents();
    initTimeLineAdapterAndTouchHelper();
  }

  public VideonaTimeLine(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.videonaTimeLineView = ((Activity) getContext()).getLayoutInflater()
            .inflate(R.layout.videona_time_line, this, true);
    initLayoutComponents();
    initTimeLineAdapterAndTouchHelper();
  }

  // TODO(jliarte): 9/07/18 save instance!
//  @Override
//  protected void onSaveInstanceState(Bundle outState) {
//    Log.d(LOG_TAG, "onSaveInstanceState");
//    outState.putInt(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
//    videoListState = videoListRecyclerView.getLayoutManager().onSaveInstanceState();
//    outState.putParcelable(SAVED_LAYOUT_MANAGER, videoListState);
//    // Reset touchHelper. Created new one onRestore, key line to restore activity without problems.
//    if (touchHelper != null) {
//      touchHelper.attachToRecyclerView(null);
//    }
//    super.onSaveInstanceState(outState);
//  }

  private void initLayoutComponents() {
    videoListRecyclerView = findViewById(R.id.recyclerview_videona_timeline);
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public void initVideoListRecycler(boolean landscapeOriented) {
    int orientation = LinearLayoutManager.VERTICAL;
    int num_grid_columns = NUM_COLUMNS_GRID_TIMELINE_VERTICAL;
    if (landscapeOriented) {
      num_grid_columns = NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL;
    }
    layoutManager = new GridLayoutManager(getContext(), num_grid_columns, orientation, false);
    videoListRecyclerView.setLayoutManager(layoutManager);
  }

  private void initTimeLineAdapterAndTouchHelper() {
    videoListRecyclerView.removeAllViews();
    timeLineAdapter = new VideoTimeLineAdapter(this);
    videoListRecyclerView.setAdapter(timeLineAdapter);
    videoTimeLineTouchHelperCallback =
            new VideoTimeLineTouchHelperCallback(timeLineAdapter);
    touchHelper = new ItemTouchHelper(videoTimeLineTouchHelperCallback);
    touchHelper.attachToRecyclerView(videoListRecyclerView);
  }

  public void setVideoList(List<Video> retrievedVideoList) {
    timeLineAdapter.updateVideoList(retrievedVideoList);
  }

  public void setSelectedClip(int selectedClip) {
    timeLineAdapter.updateSelection(selectedClip);
    // TODO(jliarte): 8/07/18 maybe we should separate this call, as when its from onClipClicked or similar, could cancel user interaction
    videoListRecyclerView.scrollToPosition(selectedClip);
  }

  @Override
  public void onClipClicked(int position) {
    // (jliarte): 8/07/18 from VideoTimeLineRecyclerViewClickListener
    Log.d(LOG_TAG, "onClipClicked " + position);
    setSelectedClip(position);
    listener.onTimeLineClipSelected(position);
  }

  @Override
  public void onClipLongClicked(int adapterPosition) {
    // (jliarte): 8/07/18 from VideoTimeLineRecyclerViewClickListener
    Log.d(LOG_TAG, "onClipLongCLicked " + adapterPosition);
//    timeLineAdapter.initMovement(adapterPosition);
    setSelectedClip(adapterPosition);
    listener.onTimeLineReorderStarted();
    // (jliarte): 9/07/18 just notify on finish movement to not slow movement with player preview update
//    listener.onTimeLineClipSelected(adapterPosition);
  }

  @Override
  public void onClipRemoveClicked(int position) {
    // (jliarte): 8/07/18 from VideoTimeLineRecyclerViewClickListener
    final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          setSelectedClip(Math.max(position - 1, 0));
          listener.onTimeLineClipRemoved(position);
//          editPresenter.removeVideoFromProject(position);
          break;
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.VideonaDialog);
    builder.setMessage(R.string.dialog_edit_remove_message).setPositiveButton(R.string.dialog_edit_remove_accept, dialogClickListener)
            .setNegativeButton(R.string.dialog_edit_remove_cancel, dialogClickListener).show();
  }

  @Override
  public void onClipMoving(int fromPosition, int toPosition) {
    // (jliarte): 8/07/18 from VideoTimeLineRecyclerViewClickListener
    Log.d(LOG_TAG, "onClipMoving " + fromPosition + " to " + toPosition);
  }

  @Override
  public void onClipReordered(int fromPosition, int toPosition) {
    // (jliarte): 8/07/18 from VideoTimeLineRecyclerViewClickListener
    Log.d(LOG_TAG, "onClipReordered " + fromPosition + " to  " + toPosition);
    setSelectedClip(toPosition);
    listener.onTimeLineReorderFinished(fromPosition, toPosition);
  }

  public void scrollToPosition(int position) {
    videoListRecyclerView.scrollToPosition(position);
  }

  public void setFailedVideos(ArrayList<Video> videoList) {
    timeLineAdapter.setFailedVideos(videoList);
    for (Video failedVideo : videoList) {
      Log.e(LOG_TAG, "failed video " + videoList.indexOf(failedVideo));
    }
  }

  public interface Listener {
    void onTimeLineClipSelected(int position);

    void onTimeLineClipRemoved(int position);

    void onTimeLineReorderStarted();

    void onTimeLineReorderFinished(int fromPosition, int toPosition);
  }
}
