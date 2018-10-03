package com.videonasocialmedia.vimojo.share.presentation.mvp.views;

import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public interface ShareVideoView {

  void showError(String message);

  void showOptionsShareList(List<OptionsToShareList> optionsShareList);

  void loadExportedVideoPreview(String mediaPath);

  void showVideoExportError(int cause);

  void showExportProgress(int progressMsg);

  void showProgressDialogVideoExporting();

  void showMessage(int stringId);

  void showDialogUploadVideoWithMobileNetwork();

  void showDialogNeedToRegisterLoginToUploadVideo();

  void showDialogNeedToCompleteDetailProjectFields();

  void showProgressDialogCheckingUserAuth();

  void hideProgressDialogCheckingUserAuth();

  void createDialogToInsertNameProject(final FtpNetwork ftpSelected, String videoPath);

  void showIntentOtherNetwork(String videoPath);

  void shareVideo(String videoPath, SocialNetwork socialNetworkSelected);

  void showDialogNotNetworkUploadVideoOnConnection();

  void pauseVideoPlayerPreview();

  void hideExportProgressDialogCanceled();

  void hideShowMoreSocialNetworks();

  void showDialogVideoIsBeingSendingToPlatform();

  void successLoginAuth0();

  void showDialogInstagramStoriesDuration();
}
