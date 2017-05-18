package com.videonasocialmedia.vimojo.presentation.mvp.views;

/**
 * Created by jliarte on 2/05/17.
 */

public interface VideoTranscodingErrorNotifier {
  void showWarningTempFile();
  void setWarningMessageTempFile(String messageTempFile);
}
