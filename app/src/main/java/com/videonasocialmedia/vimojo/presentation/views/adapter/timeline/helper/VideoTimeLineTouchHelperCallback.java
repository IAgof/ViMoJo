package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.VideoTimeLineTouchHelperCallbackAdapter;

/**
 * Created by jliarte on 24/04/17.
 */

public class VideoTimeLineTouchHelperCallback extends ItemTouchHelper.Callback {
  private final VideoTimeLineTouchHelperCallbackAdapter timeLineAdapter;

  public VideoTimeLineTouchHelperCallback(VideoTimeLineTouchHelperCallbackAdapter timeLineAdapter) {
    super();
    this.timeLineAdapter = timeLineAdapter;
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
    timeLineAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

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
      itemViewHolder.onItemClear();
      timeLineAdapter.finishMovement(viewHolder.getAdapterPosition());
    }
  }
}
