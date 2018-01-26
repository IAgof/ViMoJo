package com.videonasocialmedia.vimojo.share.presentation.mvp.views;

import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public interface ShareVideoView {
  void playPreview();

  void pausePreview();

  void showError(String message);

  void showOptionsShareList(List<OptionsToShareList> optionsShareList);

  void hideShareNetworks();

  void showMoreNetworks(List<SocialNetwork> networks);

  void hideExtraNetworks();

  void loadExportedVideoPreview(String mediaPath);

  void showVideoExportError(int cause);

  void showExportProgress(String progressMsg);

  void startVideoExport();
}
