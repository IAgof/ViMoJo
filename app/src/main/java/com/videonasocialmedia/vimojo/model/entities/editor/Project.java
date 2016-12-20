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

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
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

  private String uuid;

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

    /**
     * Constructor of minimum number of parameters. This is the Default constructor.
     *
     * @param title    - Project and final video name.
     * @param rootPath - Path to root folder for the current project.
     * @param profile  - Define some characteristics and limitations of the current project.
     */
    public Project(String title, String rootPath, Profile profile) {
        this.title = title;
        this.vmComposition = new VMComposition();
        this.profile = profile;
        this.duration = 0;
        this.lastModification = DateUtils.getDateRightNow();
        this.uuid = UUID.randomUUID().toString();
        this.projectPath = rootPath + "/.projects/" + uuid; //todo probablemente necesitemos un slugify de ese title.
        createFolder(projectPath);
        createFolder(projectPath + Constants.FOLDER_INTERMEDIATE_FILES);
        createFolder(projectPath + Constants.FOLDER_INTERMEDIATE_FILES
            + Constants.FOLDER_INTERMEDIATE_FILES_TEMPAUDIO);
    }

  public Project(Project project) {
    title = DateUtils.getDateRightNow();
    vmComposition = project.getVMComposition();
    profile = project.getProfile();
    duration = project.getDuration();
    lastModification = project.getLastModification();
    uuid = UUID.randomUUID().toString();
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

    public String getProjectPath() {
        return projectPath;
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

  public String getLastModification() {
    return lastModification;
  }

  public void setLastModification(String lastModification) {
    this.lastModification = lastModification;
  }

  public void updateLastModification(){
    this.lastModification = DateUtils.getDateRightNow();
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

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

  public void createFolder(String projectPath) {
    FileUtils.createFolder(projectPath);

  }

}