package com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.helper;

/**
 * Created by jliarte on 24/04/17.
 */

public interface VideoTimeLineTouchHelperCallbackAdapterListener {
  boolean onItemMove(int fromPosition, int toPosition);

  void finishMovement();
}
