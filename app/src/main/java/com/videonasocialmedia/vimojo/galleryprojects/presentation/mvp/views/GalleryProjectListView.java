package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views;


import android.content.BroadcastReceiver;

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;

import java.util.List;

/**
 * Interface of communication of {@link GalleryProjectListPresenter} with {@link GalleryActivity}
 */
public interface GalleryProjectListView {
  void showProjectList(List<Project> projectList);

  void createDefaultProject();

  void navigateTo(Class cls);

  void navigateTo(Class cls, String path);

  void showLoading();

  void hideLoading();

  void registerFileUploadReceiver(BroadcastReceiver completionReceiver);

  void unregisterFileUploadReceiver();

  void showUpdateAssetsProgressDialog();

  void hideUpdateAssetsProgressDialog();

  void updateUpdateAssetsProgressDialog(int remaining);
}
