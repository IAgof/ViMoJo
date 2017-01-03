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
import com.videonasocialmedia.vimojo.utils.Constants;

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
    // TODO(jliarte): 2/01/17 this has to be project path
    String tempFilesDirectory = Constants.PATH_APP_TEMP;
    String outputFilesDirectory = Constants.PATH_APP;
    VMCompositionExportSession = new VMCompositionExportSessionImpl(
            project.getVMComposition(), project.getProfile(),
            outputFilesDirectory, tempFilesDirectory, this);
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
  public void onExportSuccess(Video video) {
    onExportFinishedListener.onExportSuccess(video);
  }

  @Override
  public void onExportProgress(String progressMsg) {
    // TODO(jliarte): 23/12/16 process progress messages
  }

  @Override
  public void onExportError(String error) {
    onExportFinishedListener.onExportError(error);
  }
}
