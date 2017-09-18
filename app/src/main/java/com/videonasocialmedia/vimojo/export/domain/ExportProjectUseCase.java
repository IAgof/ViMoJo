/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession.ExportListener;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSessionImpl;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

public class ExportProjectUseCase implements ExportListener {
  private static final String TAG = ExportProjectUseCase.class.getCanonicalName();
  private OnExportFinishedListener onExportFinishedListener;
  private VMCompositionExportSession vmCompositionExportSession;
  private Project project;
  private final VideoToAdaptRepository videoToAdaptRepository;

  /**
   * Project VMCompositionExportSession use case.
   */
  public ExportProjectUseCase(VideoToAdaptRepository videoToAdaptRepository) {
    project = Project.getInstance(null, null, null, null);

    // TODO(jliarte): 28/04/17 move to export method?
    String tempPathIntermediateAudioFilesDirectory =
            project.getProjectPathIntermediateAudioMixedFiles();
    String outputFilesDirectory = Constants.PATH_APP;
    String tempAudioPath = project.getProjectPathIntermediateFileAudioFade();
    vmCompositionExportSession = new VMCompositionExportSessionImpl(project.getVMComposition(),
        outputFilesDirectory, tempPathIntermediateAudioFilesDirectory, tempAudioPath, this);
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  /**
   * Main use case method.
   */
  public void export(String pathWatermark, OnExportFinishedListener onExportFinishedListener) {
    this.onExportFinishedListener = onExportFinishedListener;
    checkWatermarkResource(pathWatermark);
    try {
      ListenableFuture<List<Video>> adaptVideoTasks = getAdaptingVideoTasks();
      Futures.transform(adaptVideoTasks, new Function<List<Video>, Object>() {
        @Nullable
        @Override
        public Object apply(List<Video> input) {
          vmCompositionExportSession.exportAsyncronously();
          return null;
        }
      });
    } catch (NoSuchElementException exception) {
      onExportError(String.valueOf(exception));
    } catch (InterruptedException | ExecutionException e) {
      Log.e(TAG, "Error waiting for adapting jobs to finish before exporting");
      e.printStackTrace();
      onExportError(String.valueOf(e));
    }
  }

  private void checkWatermarkResource(String pathWatermark) {
    File watermarkResource = new File(pathWatermark);
    if (!watermarkResource.exists()) {
      if (!Utils.copyWatermarkResourceToDevice()) {
        onExportError(VimojoApplication.getAppContext().getString(R.string.export_error_watermark));
      }
    }
  }

  private ListenableFuture<List<Video>> getAdaptingVideoTasks() throws ExecutionException, InterruptedException {
    ArrayList<ListenableFuture<Video>> adaptTasks = new ArrayList<>();
    List<VideoToAdapt> videosBeingAdapted = videoToAdaptRepository.getAllVideos();
    if (videosBeingAdapted.size() > 0) {
      for (VideoToAdapt videoToAdapt : videosBeingAdapted) {
        if ((videoToAdapt.getVideo() != null)
                && (videoToAdapt.getVideo().getTranscodingTask() != null)
                && (!videoToAdapt.getVideo().getTranscodingTask().isDone())) {
          adaptTasks.add(videoToAdapt.getVideo().getTranscodingTask());
        }
      }
    }
    ListenableFuture<List<Video>> adaptCombinedTask = Futures.allAsList(adaptTasks);
    return adaptCombinedTask;
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
