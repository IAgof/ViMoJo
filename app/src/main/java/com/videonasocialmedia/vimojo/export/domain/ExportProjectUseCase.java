/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;

import java.util.LinkedList;
import java.util.NoSuchElementException;


public class ExportProjectUseCase implements OnExportEndedListener {

  private OnExportFinishedListener onExportFinishedListener;
  private Exporter exporter;
  private Project project;

  private static final int MAX_SECONDS_WAITING_FOR_TEMP_FILES = 20;

  /**
   * Project exporter use case.
   *
   * @param onExportFinishedListener listener for the use case to send callbacks
   */
  public ExportProjectUseCase(OnExportFinishedListener onExportFinishedListener) {
    this.onExportFinishedListener = onExportFinishedListener;
    project = Project.getInstance(null, null, null);
    exporter = new ExporterImpl(project, this);
  }

  /**
   * Main use case method.
   */
  public void export() {
    waitForOutputFilesFinished();
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

  private void waitForOutputFilesFinished() {
    LinkedList<Media> medias = getMediasFromProject();
    int countWaiting = 0;
    for (Media media : medias) {
      Video video = (Video) media;
      if (video.isEdited()) {
        while (!video.outputVideoIsFinished()) {
          try {
            if (countWaiting > MAX_SECONDS_WAITING_FOR_TEMP_FILES) {
              break;
            }
            countWaiting++;
            Thread.sleep(1000);
          } catch (InterruptedException exception) {
            exception.printStackTrace();
          }
        }
      }
    }
  }

  private LinkedList<Media> getMediasFromProject() {
    LinkedList<Media> medias = project.getMediaTrack().getItems();
    return medias;
  }
}
