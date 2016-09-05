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

import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

/**
 * Project profile. Define some characteristics and limitations of the current video editing project
 * based on User subscription and options.
 */
public class Profile {

    private static Profile INSTANCE;

    /**
     * possible profileTypes
     */
    public static enum ProfileType {
        free, pro
    }

    /**
     * Resolution of the Video objects in a project
     */
    private VideoResolution.Resolution resolution;

    /**
     * Video bit rate
     */
    private VideoQuality.Quality quality;

    /**
     * Maximum length of the project in millseconds;
     * if the value is negative the project duration has no limitation
     */
    private long maxDuration;

    /**
     * type of profile
     */
    private ProfileType profileType;

    /**
     * Constructor of minimum number of parameters. In this case coincides with parametrized
     * constructor and therefore is the default constructor. It has all possible atributes for the
     * profile object.
     * <p/>
     * There can be only a single instance of a profile, and therefore this constructor can only be
     * accessed through the factory.
     *
     * @param resolution  - Maximum resolution allowed for the profile.
     * @param maxDuration - Maximum video duration allowed for the profile.
     * @param type        - Profile type.
     */
    private Profile(VideoResolution.Resolution resolution, VideoQuality.Quality quality, long maxDuration, ProfileType type) {
        this.resolution = resolution;
        this.maxDuration = maxDuration;
        this.profileType = type;
        this.quality = quality;
    }

    /**
     * Profile factory.
     *
     * @param profileType
     * @return - profile instance.
     */
    public static Profile getInstance(ProfileType profileType) {
        if (INSTANCE == null) {
            if (profileType == ProfileType.free) {
                INSTANCE = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.VERY_GOOD, 1000, profileType);
            } else {
                INSTANCE = new Profile(VideoResolution.Resolution.HD1080, VideoQuality.Quality.EXCELLENT, -1, profileType);
            }
        }
        return INSTANCE;
    }

    //getter and setter enum resolution.
    public VideoResolution.Resolution getResolution() {
        return resolution;
    }

    public void setResolution(VideoResolution.Resolution resolution) {
        if (profileType == ProfileType.pro)
            this.resolution = resolution;
    }

    //getter resolution, width, height values.
    public VideoResolution getVideoResolution(){
        VideoResolution videoResolution = new VideoResolution(resolution);
        return videoResolution;
    }

    //getter and setter enum quality
    public VideoQuality.Quality getQuality() {
        return quality;
    }

    public void setQuality(VideoQuality.Quality quality) {
        this.quality = quality;
    }

    // getter videoBitRate;
    public VideoQuality getVideoQuality() {
        VideoQuality videoQuality = new VideoQuality(quality);
        return videoQuality;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        if (profileType == ProfileType.pro)
            this.maxDuration = maxDuration;
    }

    public ProfileType getProfileType() {
        return profileType;
    }
}
