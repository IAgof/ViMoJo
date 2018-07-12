package com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by jliarte on 24/04/17.
 */

public class VideoTimeLineTouchHelperCallback extends ItemTouchHelper.Callback {
  private static final String LOG_TAG = VideoTimeLineTouchHelperCallback.class.getSimpleName();
  private final VideoTimeLineTouchHelperCallbackAdapterListener timeLineTouchHelperAdapterListener;

  public VideoTimeLineTouchHelperCallback(VideoTimeLineTouchHelperCallbackAdapterListener
                                              timeLineTouchHelperAdapterListener) {
    super();
    this.timeLineTouchHelperAdapterListener = timeLineTouchHelperAdapterListener;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder videoItem) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
    return makeMovementFlags(dragFlags, 0);
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return false;
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                        RecyclerView.ViewHolder target) {
    if (viewHolder.getAdapterPosition() != target.getAdapterPosition()) {
      timeLineTouchHelperAdapterListener.onItemMove(viewHolder.getAdapterPosition(),
          target.getAdapterPosition());
    }
    return true;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder target, int direction) {
    int position = target.getAdapterPosition();
    Log.d(LOG_TAG, "timeline: item swiped!!! position " + position);
  }

  @Override
  public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos,
                      RecyclerView.ViewHolder target, int toPos, int x, int y) {
    Log.d(LOG_TAG, "timeline: onMoved!!! position from " + fromPos + " to " + toPos);
    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      if (viewHolder instanceof ItemTouchHelperViewHolder) {
        ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
      }
    }
    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
    if (viewHolder instanceof ItemTouchHelperViewHolder) {
      ItemTouchHelperViewHolder itemViewHolder =
              (ItemTouchHelperViewHolder) viewHolder;
      if (!recyclerView.isComputingLayout()) {
        itemViewHolder.onItemClear();
        timeLineTouchHelperAdapterListener.finishMovement();
      }
    }
  }
}
