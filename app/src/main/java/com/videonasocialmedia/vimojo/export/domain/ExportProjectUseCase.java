/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSessionImpl;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;

import java.util.NoSuchElementException;

public class ExportProjectUseCase implements VMCompositionExportSession.OnExportEndedListener {
  private OnExportFinishedListener onExportFinishedListener;
  private VMCompositionExportSession VMCompositionExportSession;
  private Project project;

  /**
   * Project VMCompositionExportSession use case.
   *
   * @param onExportFinishedListener listener for the use case to send callbacks
   */
  public ExportProjectUseCase(OnExportFinishedListener onExportFinishedListener) {
    this.onExportFinishedListener = onExportFinishedListener;
    project = Project.getInstance(null, null, null);
    VMCompositionExportSession = new VMCompositionExportSessionImpl(com.videonasocialmedia.vimojo.utils.Constants.PATH_APP, project.getVMComposition(),
        project.getProfile(), this);
  }

  /**
   * Main use case method.
   */
  public void export() {
    try {
      VMCompositionExportSession.export();
    } catch (NoSuchElementException exception) {
      onExportError(String.valueOf(exception));
    }
  }

  @Override
  public void onExportError(String error) {
    onExportFinishedListener.onExportError(error);
  }

  @Override
  public void onExportSuccess(Video video) {
    onExportFinishedListener.onExportSuccess(video);
  }
}
