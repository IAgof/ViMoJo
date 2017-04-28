package com.videonasocialmedia.vimojo.presentation.views.adapter.helper;

/**
 * Created by jliarte on 24/04/17.
 */

public interface VideoTimeLineTouchHelperCallbackAdapter {
  boolean onItemMove(int fromPosition, int toPosition);

  void onItemDismiss(int position);

  void finishMovement(int adapterPosition);
}
