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

import com.videonasocialmedia.videonamediaframework.model.media.Audio;
import com.videonasocialmedia.videonamediaframework.model.media.Image;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.vimojo.sound.model.VoiceOver;

import java.io.File;
import java.util.ArrayList;

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

    private VoiceOver voiceOver;

  private VMComposition vmComposition;


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
        this.projectPath = rootPath + "/projects/" + title; //todo probablemente necesitemos un slugify de ese title.
        this.checkPathSetup(rootPath);
        this.vmComposition = new VMComposition();
        this.profile = profile;
        this.duration = 0;

    }

  public VMComposition getVMComposition() {
    return vmComposition;
  }

  /**
     * @param rootPath
     */
    private void checkPathSetup(String rootPath) {

        Project.VIDEONA_PATH = rootPath;
        File projectPath = new File(this.projectPath);
        projectPath.mkdirs();

        Audio.AUDIO_PATH = rootPath + "/audios";
        File audioPath = new File(Audio.AUDIO_PATH + "/thumbs");
        audioPath.mkdirs();

        Image.IMAGE_PATH = rootPath + "/images";
        File imagePath = new File(Image.IMAGE_PATH + "thumbs");
        imagePath.mkdirs();

        Video.VIDEO_FOLDER_PATH = rootPath + "/videos";
        File videoPath = new File(Video.VIDEO_FOLDER_PATH + "/thumbs");
        videoPath.mkdirs();

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

    public void setVoiceOver(VoiceOver voiceOver){
      this.voiceOver = voiceOver;
    }

    public boolean hasVoiceOver(){
      return voiceOver != null;
    }

    public String getVoiceOverPath(){
      if(voiceOver!=null){
        return voiceOver.getPath();
      }
        return "";
    }

    public float getVoiceOverVolume(){
      if(voiceOver!=null){
        return voiceOver.getVolume();
      }
      return 0;
    }

}