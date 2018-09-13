package com.videonasocialmedia.vimojo.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.birbit.android.jobqueue.JobManager;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.main.modules.UploadToPlatformModule;
import com.videonasocialmedia.vimojo.repository.datasource.BackgroundScheduler;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackDataSource;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.sync.presentation.broadcastreceiver.UploadBroadcastReceiver;
import com.videonasocialmedia.vimojo.sync.presentation.SyncService;
import com.videonasocialmedia.vimojo.sync.presentation.UploadToPlatform;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jliarte on 2/11/16.
 */
@Singleton
@Component(modules = {ApplicationModule.class, DataRepositoriesModule.class,
        TrackerModule.class, UploadToPlatformModule.class})
public interface SystemComponent {
  Context provideContext();
  ProjectRepository getProjectRepository();
  VideoDataSource getVideoRepository();
  VideoToAdaptDataSource getVideoToAdaptRepository();
  TrackDataSource getTrackRepository();
  MusicDataSource getMusicRepository();
  UploadDataSource getUploadRepository();
  UserEventTracker getUserEventTracker();
  UploadToPlatform getUploadToPlatform();
  SharedPreferences getSharedPreferences();
  CameraSettingsDataSource getCameraRepository();
  JobManager provideAPIJobManager();
  BackgroundScheduler provideBackgroundScheduler();
  void inject(VimojoActivity activity);
  void inject(SyncService syncService);
  void inject(UploadBroadcastReceiver uploadBroadcastReceiver);
}
