/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.pipeline.Exporter;
import com.videonasocialmedia.videonamediaframework.pipeline.ExporterImpl;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;

import java.util.NoSuchElementException;


public class ExportProjectUseCase implements Exporter.OnExportEndedListener {

  private OnExportFinishedListener onExportFinishedListener;
  private Exporter exporter;
  private Project project;

  /**
   * Project exporter use case.
   *
   * @param onExportFinishedListener listener for the use case to send callbacks
   */
  public ExportProjectUseCase(OnExportFinishedListener onExportFinishedListener) {
    this.onExportFinishedListener = onExportFinishedListener;
    project = Project.getInstance(null, null, null);
    exporter = new ExporterImpl(com.videonasocialmedia.vimojo.utils.Constants.PATH_APP, project.getVMComposition(),
        project.getProfile(), this);
  }

  /**
   * Main use case method.
   */
  public void export() {
    try {
      exporter.export();
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
