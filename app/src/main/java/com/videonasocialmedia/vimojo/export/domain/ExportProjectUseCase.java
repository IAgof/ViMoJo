/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.export.domain;

import android.util.Log;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession.ExportListener;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSessionImpl;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import static com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession.EXPORT_STAGE_APPLY_WATERMARK_RESOURCE_ERROR;
import static com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession.EXPORT_STAGE_WAIT_FOR_TRANSCODING_ERROR;

public class ExportProjectUseCase implements ExportListener {
  private static final String TAG = ExportProjectUseCase.class.getCanonicalName();
  private OnExportFinishedListener onExportFinishedListener;
  private VMCompositionExportSession vmCompositionExportSession;
  private Project project;
  private final VideoToAdaptDataSource videoToAdaptRepository;
  private boolean isExportCanceled = false;

  /**
   * Project VMCompositionExportSession use case.
   */
  public ExportProjectUseCase(
          VideoToAdaptDataSource videoToAdaptRepository) {
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  /**
   * Main use case method.
   */
  public void export(Project currentProject, String pathWatermark,
                     String nativeLibPath, OnExportFinishedListener onExportFinishedListener) {
    this.project = currentProject;
    String tempPathIntermediateAudioFilesDirectory =
            project.getProjectPathIntermediateAudioMixedFiles();
    String tempAudioPath = project.getProjectPathIntermediateFileAudioFade();
    // TODO(jliarte): 23/04/18 remove this android dependency!!
    String outputFilesDirectory = Constants.PATH_APP;
    vmCompositionExportSession = new VMCompositionExportSessionImpl(project.getVMComposition(),
            outputFilesDirectory, tempPathIntermediateAudioFilesDirectory, tempAudioPath, this);

    this.onExportFinishedListener = onExportFinishedListener;
    isExportCanceled = false;
    checkWatermarkResource(pathWatermark);
    try {
      ListenableFuture<List<Video>> adaptVideoTasks = getAdaptingVideoTasks();
      Futures.transform(adaptVideoTasks, (Function<List<Video>, Object>) input -> {
        vmCompositionExportSession.exportAsyncronously(nativeLibPath);
        return null;
      });
    } catch (NoSuchElementException exception) {
      Log.e(TAG, "Caught " +  exception.getClass().getName()
          + "Error waiting for adapting jobs to finish before exporting" + exception.getMessage());
      onExportError(EXPORT_STAGE_WAIT_FOR_TRANSCODING_ERROR, exception);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      Log.e(TAG, "Caught " +  e.getClass().getName()
          + "Error waiting for adapting jobs to finish before exporting" + e.getMessage());
      onExportError(EXPORT_STAGE_WAIT_FOR_TRANSCODING_ERROR, e);
    }
  }

  public void removeCallbacks() {
    this.onExportFinishedListener = null;
  }

  private void checkWatermarkResource(String pathWatermark) {
    File watermarkResource = new File(pathWatermark);
    if (!watermarkResource.exists()) {
      if (!Utils.copyWatermarkResourceToDevice()) {
        Log.e(TAG, "Error applying watermark, resource not found");
        onExportError(EXPORT_STAGE_APPLY_WATERMARK_RESOURCE_ERROR,
            new Exception("Error applying watermark, resource not found"));
      }
    }
  }

  private ListenableFuture<List<Video>> getAdaptingVideoTasks() throws ExecutionException,
          InterruptedException {
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
    if (!isExportCanceled && onExportFinishedListener != null) {
      onExportFinishedListener.onExportSuccess(video);
    }
  }

  @Override
  public void onExportProgress(int exportStage) {
    if (!isExportCanceled && onExportFinishedListener != null) {
      onExportFinishedListener.onExportProgress(exportStage);
    }
  }

  @Override
  public void onExportError(int exportErrorStage, Exception exception) {
    if (!isExportCanceled && onExportFinishedListener != null) {
      onExportFinishedListener.onExportError(exportErrorStage, exception);
    }
  }

  @Override
  public void onCancelExport() {
    if (onExportFinishedListener != null) {
      onExportFinishedListener.onExportCanceled();
    }
  }

  public void cancelExport() {
    isExportCanceled = true;
    vmCompositionExportSession.cancel();
  }

}
