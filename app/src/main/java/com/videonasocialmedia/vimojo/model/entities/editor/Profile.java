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

import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

/**
 * Project profile. Define some characteristics and limitations of the current video editing project
 * based on User subscription and options.
 */
public class Profile {

    private static Profile INSTANCE;

    public void clear() {
        if (INSTANCE != null) {
            INSTANCE = null;
        }
    }

    /**
     * Resolution of the Video objects in a project
     */
    private VideoResolution.Resolution resolution;

    private VideoResolution videoResolution;

    /**
     * Video bit rate
     */
    private VideoQuality.Quality quality;

    private VideoQuality videoQuality;

    /**
     * Video frame rate
     */
    private VideoFrameRate.FrameRate frameRate;

    private VideoFrameRate videoFrameRate;


    /**
     * Constructor of minimum number of parameters. In this case coincides with parametrized
     * constructor and therefore is the default constructor. It has all possible atributes for the
     * profile object.
     * <p/>
     * There can be only a single instance of a profile, and therefore this constructor can only be
     * accessed through the factory.
     *
     * @param resolution
     * @param quality
     * @param frameRate
     */
    private Profile(VideoResolution.Resolution resolution, VideoQuality.Quality quality,
                    VideoFrameRate.FrameRate frameRate) {
        this.resolution = resolution;
        this.videoResolution = new VideoResolution(resolution);
        this.quality = quality;
        this.videoQuality = new VideoQuality(quality);
        this.frameRate = frameRate;
        this.videoFrameRate = new VideoFrameRate(frameRate);
    }

    /**
     * Profile factory.
     *
     * @return - profile instance.
     */
    public static Profile getInstance(VideoResolution.Resolution resolution, VideoQuality.Quality quality,
                                      VideoFrameRate.FrameRate frameRate) {
        if (INSTANCE == null) {
            INSTANCE = new Profile(resolution, quality, frameRate);
        }
        return INSTANCE;
    }

    //getter and setter enum resolution.
    public VideoResolution.Resolution getResolution() {
        return resolution;
    }

    public void setResolution(VideoResolution.Resolution resolution) {
            this.resolution = resolution;
    }

    //getter resolution, width, height values.
    public VideoResolution getVideoResolution(){
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
        return videoQuality;
    }

    public VideoFrameRate.FrameRate getFrameRate(){
        return frameRate;
    }

    public void setFrameRate(VideoFrameRate.FrameRate frameRate){
        this.frameRate = frameRate;
    }

    public VideoFrameRate getVideoFrameRate(){
        return videoFrameRate;
    }
}
