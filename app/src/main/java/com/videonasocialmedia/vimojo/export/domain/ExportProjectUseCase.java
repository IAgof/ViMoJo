/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession.ExportListener;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSessionImpl;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.NoSuchElementException;

public class ExportProjectUseCase implements ExportListener {
  private OnExportFinishedListener onExportFinishedListener;
  private VMCompositionExportSession VMCompositionExportSession;
  private Project project;

  /**
   * Project VMCompositionExportSession use case.
   */
  public ExportProjectUseCase() {
    project = Project.getInstance(null, null, null, null);

    // TODO(jliarte): 28/04/17 move to export method?
    String tempPathIntermediateAudioFilesDirectory =
            project.getProjectPathIntermediateAudioMixedFiles();
    String outputFilesDirectory = Constants.PATH_APP;
    VMCompositionExportSession = new VMCompositionExportSessionImpl(project.getVMComposition(),
        outputFilesDirectory, tempPathIntermediateAudioFilesDirectory, this);
  }

  /**
   * Main use case method.
   */
  public void export(OnExportFinishedListener onExportFinishedListener) {
    this.onExportFinishedListener = onExportFinishedListener;
    try {
      VMCompositionExportSession.exportAsyncronously();
    } catch (NoSuchElementException exception) {
      onExportError(String.valueOf(exception));
    }
  }

  @Override
  public void onExportSuccess(Video video) {
    onExportFinishedListener.onExportSuccess(video);
  }

  @Override
  public void onExportProgress(String progressMsg, int exportStage) {
    onExportFinishedListener.onExportProgress(progressMsg, exportStage);
  }

  @Override
  public void onExportError(String error) {
    onExportFinishedListener.onExportError(error);
  }
}
