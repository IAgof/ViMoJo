package com.videonasocialmedia.vimojo.main;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.main.modules.UploadToPlatformModule;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.upload.UploadRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
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
  ProjectRepository getProjectRepository();
  VideoRepository getVideoRepository();
  VideoToAdaptRepository getVideoToAdaptRepository();
  TrackRepository getTrackRepository();
  MusicRepository getMusicRepository();
  UploadRepository getUploadRepository();
  UserEventTracker getUserEventTracker();
  UploadToPlatform getUploadToPlatform();
  SharedPreferences getSharedPreferences();
  CameraSettingsRepository getCameraRepository();
  void inject(VimojoActivity activity);
  void inject(SyncService syncService);
  void inject(UploadBroadcastReceiver uploadBroadcastReceiver);
}
