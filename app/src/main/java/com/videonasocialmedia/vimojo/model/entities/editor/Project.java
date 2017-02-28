/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */
package com.videonasocialmedia.vimojo.model.entities.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Watermark;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.vimojo.model.sources.WatermarkProvider;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Project representation that contains reference to media, audio, transitions and effects used in
 * the current edition job.
 * A project can be created, opened, saved, deleted and shared with other users. Every time a user
 * opens a project all previous changes must be accessible to him or her. However there can be only
 * one
 */
public class Project {
  public static final String INTERMEDIATE_FILES = "intermediate_files";
  public static final String INTERMEDIATE_FILES_TEMP_AUDIO_FADE = "tempAudioFade";
  // TODO:(alvaro.martinez) 23/12/16 Change VideonaSDK, receive path temo from app, folder name ".tempAudio";
  public static final String TEMP_FILES_AUDIO_MIXED = "tempMixedAudio";
  public static final String TEMP_FILES_AUDIO_MIXED_VOICE_OVER_RECORD = "voiceOverRecord";
  private final String TAG = getClass().getCanonicalName();
    public static String VIDEONA_PATH = "";
    /**
     * There could be just one project open at a time. So this converts Project in a Singleton.
     */
    // TODO(jliarte): 22/10/16 Would use project instance to store current project by now
    @Deprecated
    public static Project INSTANCE;
    /**
     * Project name. Also it will be the name of the exported video
     */
    private String title;
    /**
     * The folder where de temp files of the project are stored
     */
    private String projectPath;

  private VMComposition vmComposition;

  private String lastModification;
  
  private String uuid = UUID.randomUUID().toString();

  private LastVideoExported lastVideoExported;


    /**
     * Project profile. Defines some limitations and characteristic of the project based on user
     * subscription.
     */
    private Profile profile;
    /**
     * Project duration. The duration of the project in milliseconds.
     */
    private int duration;

    private String musicTitleIdentifier;

    private boolean isMusicOnProject = false;

  private boolean isAudioFadeTransitionActivated;
  private boolean isVideoFadeTransitionActivated;

  private String watermarkResource;

    /**
     * Constructor of minimum number of parameters. This is the Default constructor.
     *
     * @param title    - Project and final video name.
     * @param rootPath - Path to root folder for the current project.
     * @param profile  - Define some characteristics and limitations of the current project.
     */
    public Project(String title, String rootPath, Profile profile) {
        this.title = title;
        this.vmComposition = new VMComposition(getResourceWatermarkFilePath(rootPath), profile);
        this.profile = profile;
        this.duration = 0;
        this.isAudioFadeTransitionActivated = false;
        this.isVideoFadeTransitionActivated = false;
        this.lastModification = DateUtils.getDateRightNow();
        this.projectPath = rootPath + File.separator + Constants.FOLDER_NAME_VIMOJO_PROJECTS +
            File.separator + uuid; //todo probablemente necesitemos un slugify de ese title.
      //  createProjectFolders();
    }

  @NonNull
  public String getResourceWatermarkFilePath(String rootPath) {

    return rootPath + File.separator + Constants.FOLDER_NAME_VIMOJO_TEMP + File.separator +
        Constants.RESOURCE_WATERMARK_NAME;
  }

  public Project(Project project) throws IllegalItemOnTrack {

    title = DateUtils.getDateRightNow();
    vmComposition = new VMComposition(project.getVMComposition());
    profile = new Profile(project.getProfile());
    duration = project.getDuration();
    isAudioFadeTransitionActivated = project.isAudioFadeTransitionActivated();
    isVideoFadeTransitionActivated = project.isVideoFadeTransitionActivated();
    lastModification = project.getLastModification();
    projectPath = new File(project.getProjectPath()).getParent() + File.separator + uuid;
    createFolder(projectPath);
  }


  public VMComposition getVMComposition() {
    return vmComposition;
  }


    /**
     * Project factory.
     *
     * (jliarte): since 21/10/16 Project stops being a singleton :P
     *
     * @return - Singleton instance of the current project.
     */
    @Deprecated
    public static Project getInstance(String title, String rootPath, Profile profile) {
        if (INSTANCE == null) {
            INSTANCE = new Project(title, rootPath, profile);
        }
        return INSTANCE;
    }

    // getters & setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public MediaTrack getMediaTrack() {
        return vmComposition.getMediaTrack();
    }

    public void setMediaTrack(MediaTrack mediaTrack) {
        this.vmComposition.setMediaTrack(mediaTrack);
    }

    public ArrayList<AudioTrack> getAudioTracks() {
        return vmComposition.getAudioTracks();
    }

    public void setAudioTracks(ArrayList<AudioTrack> audioTracks) {
        this.vmComposition.setAudioTracks(audioTracks);
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getDuration() {
        duration = vmComposition.getDuration();
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void clear() {
//        INSTANCE = new Project(null, null, null);
        if (INSTANCE != null) {
            Profile projectProfile = INSTANCE.getProfile();
            if (projectProfile != null) {
                projectProfile.clear();
            }
            INSTANCE = null;
        }
    }

    public int numberOfClips() {
        return getMediaTrack().getItems().size();
    }

  // TODO(jliarte): 18/11/16 get rid of this method?
    public Music getMusic() {
        return vmComposition.getMusic();
    }

  // TODO(jliarte): 18/11/16 get rid of this method?
    public boolean hasMusic() {
      return vmComposition.hasMusic();
    }

    public void setMusicOnProject(boolean musicOnProject) {
        isMusicOnProject = musicOnProject;
    }

    public String getMusicTitleIdentifier() {
        return musicTitleIdentifier;
    }

    public void setMusicTitleIdentifier(String musicTitleIdentifier) {
        this.musicTitleIdentifier = musicTitleIdentifier;
    }

  public boolean isAudioFadeTransitionActivated() {
    return isAudioFadeTransitionActivated;
  }

  public void setAudioFadeTransitionActivated(boolean audioFadeTransitionActivated) {
    isAudioFadeTransitionActivated = audioFadeTransitionActivated;
  }

  public boolean isVideoFadeTransitionActivated() {
    return isVideoFadeTransitionActivated;
  }

  public void setVideoFadeTransitionActivated(boolean videoFadeTransitionActivated) {
    isVideoFadeTransitionActivated = videoFadeTransitionActivated;
  }
  public String getLastModification() {
    return lastModification;
  }

  public void setLastModification(String lastModification) {
    this.lastModification = lastModification;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  //// TODO:(alvaro.martinez) 22/12/16 Move to composition last video exported
  public void setLastVideoExported(LastVideoExported lastVideoExported) {
    this.lastVideoExported = lastVideoExported;
  }

  public boolean hasVideoExported(){
    if(lastVideoExported!=null)
      return true;
    return false;
  }

  public String getPathLastVideoExported(){
    if(lastVideoExported!= null){
      return lastVideoExported.getPathLastVideoExported();
    }
    return "";
  }

  public String getDateLastVideoExported(){
    if(lastVideoExported!= null){
      return lastVideoExported.getDateLastVideoExported();
    }
    return "";
  }

  public double getProjectSizeMbVideoToExport(){
    float scaleToMb = 0.125f;
    double durationSeconds =  getDuration()* 0.001;
    double videoBitRateMb = getProfile().getVideoQuality().getVideoBitRate()*0.000001;
    double sizeBytes = videoBitRateMb*durationSeconds;
    return sizeBytes * scaleToMb;
  }

  public void createProjectFolders() {
    createFolder(projectPath);
    createFolder(projectPath + File.separator + INTERMEDIATE_FILES);
    createFolder(projectPath + File.separator + TEMP_FILES_AUDIO_MIXED);
    createFolder(projectPath + File.separator + INTERMEDIATE_FILES + File.separator
        + INTERMEDIATE_FILES_TEMP_AUDIO_FADE);
  }

  public String getProjectPath() {
    createFolder(projectPath);
    return projectPath;
  }

  public String getProjectPathIntermediateFiles(){
    String pathIntermediateFiles = getProjectPath() + File.separator + INTERMEDIATE_FILES;
    createFolder(pathIntermediateFiles);
    return pathIntermediateFiles;
  }

  public String getProjectPathIntermediateFileAudioFade(){
    String pathIntermediateFilesTempAudioFade = projectPath + File.separator + INTERMEDIATE_FILES
        + File.separator+ INTERMEDIATE_FILES_TEMP_AUDIO_FADE;
    createFolder(pathIntermediateFilesTempAudioFade);
    return pathIntermediateFilesTempAudioFade;
  }

  public String getProjectPathIntermediateAudioMixedFiles(){
    String pathTempFilesAudioMixed = getProjectPath() + File.separator + TEMP_FILES_AUDIO_MIXED;
    createFolder(pathTempFilesAudioMixed);
    return pathTempFilesAudioMixed;
  }

  public String getProjectPathIntermediateAudioFilesVoiceOverRecord(){
    String pathTempFilesAudioMixedVoiceOverRecord = getProjectPath() + File.separator
        + TEMP_FILES_AUDIO_MIXED + File.separator + TEMP_FILES_AUDIO_MIXED_VOICE_OVER_RECORD;
    createFolder(pathTempFilesAudioMixedVoiceOverRecord);
    return pathTempFilesAudioMixedVoiceOverRecord;
  }

  public void setWatermarkActivated(boolean value){
      this.vmComposition.setWatermarkActivated(value);
  }

  public boolean hasWatermark(){
    return vmComposition.hasWatermark();
  }

  private void createFolder(String projectPath) {
    FileUtils.createFolder(projectPath);
  }

}