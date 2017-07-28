/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import android.util.Log;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession.ExportListener;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSessionImpl;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

public class ExportProjectUseCase implements ExportListener {
  private static final String TAG = ExportProjectUseCase.class.getCanonicalName();
  private OnExportFinishedListener onExportFinishedListener;
  private VMCompositionExportSession VMCompositionExportSession;
  private Project project;
  private final VideoToAdaptRepository videoToAdaptRepository;

  /**
   * Project VMCompositionExportSession use case.
   */
  public ExportProjectUseCase() {
    project = Project.getInstance(null, null, null);

    // TODO(jliarte): 28/04/17 move to export method?
    String tempPathIntermediateAudioFilesDirectory =
            project.getProjectPathIntermediateAudioMixedFiles();
    String outputFilesDirectory = Constants.PATH_APP;
    String tempAudioPath = project.getProjectPathIntermediateFileAudioFade();
    VMCompositionExportSession = new VMCompositionExportSessionImpl(project.getVMComposition(),
        outputFilesDirectory, tempPathIntermediateAudioFilesDirectory, tempAudioPath, this);
    // TODO(jliarte): 28/07/17 inject this field?
    videoToAdaptRepository = new VideoToAdaptRealmRepository();
  }

  /**
   * Main use case method.
   */
  public void export(OnExportFinishedListener onExportFinishedListener) {
    this.onExportFinishedListener = onExportFinishedListener;
    try {
      waitForAdaptJobsToFinish();
      VMCompositionExportSession.exportAsyncronously();
    } catch (NoSuchElementException exception) {
      onExportError(String.valueOf(exception));
    } catch (InterruptedException | ExecutionException e) {
      Log.e(TAG, "Error waiting for adapting jobs to finish before exporting");
      e.printStackTrace();
      onExportError(String.valueOf(e));
    }
  }

  private void waitForAdaptJobsToFinish() throws ExecutionException, InterruptedException {
    List<VideoToAdapt> videosBeingAdapted = videoToAdaptRepository.getAllVideos();
    if (videosBeingAdapted.size() > 0) {
      ArrayList<ListenableFuture<Void>> adaptTasks = new ArrayList<>();
      for (VideoToAdapt videoToAdapt : videosBeingAdapted) {
        if ((videoToAdapt.getVideo() != null)
                && (videoToAdapt.getVideo().getTranscodingTask() != null)) {
          adaptTasks.add(videoToAdapt.getVideo().getTranscodingTask());
        }
      }
      ListenableFuture<List<Void>> adapCombinedTask = Futures.allAsList(adaptTasks);
      adapCombinedTask.get();
    }
  }

  @Override
  public void onExportSuccess(Video video) {
    onExportFinishedListener.onExportSuccess(video);
  }

  @Override
  public void onExportProgress(String progressMsg, int exportStage) {
    Log.d(TAG, progressMsg);
    onExportFinishedListener.onExportProgress(progressMsg, exportStage);
  }

  @Override
  public void onExportError(String error) {
    onExportFinishedListener.onExportError(error);
  }
}
