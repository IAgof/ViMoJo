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
package com.videonasocialmedia.videonamediaframework.model.media;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

/**
 * Composition profile. Define some characteristics and limitations of the current video
 * composition.
 */
public class Profile {
    private static Profile INSTANCE;

    public void clear() {
        if (INSTANCE != null) {
            INSTANCE = null;
        }
    }

    /**
     * Resolution of the Video objects in a composition
     */
    private VideoResolution videoResolution;
    private VideoResolution.Resolution resolution;

    /**
     * Video bit rate
     */

    private VideoQuality videoQuality;
    private VideoQuality.Quality quality;

    /**
     * Video frame rate
     */
    private VideoFrameRate videoFrameRate;
    private VideoFrameRate.FrameRate frameRate;


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
    public Profile(VideoResolution.Resolution resolution, VideoQuality.Quality quality,
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
     * (jliarte): since 21/10/16 Profile stops being a singleton :P
     * @return - profile instance.
     */
    @Deprecated
    public static Profile getInstance(VideoResolution.Resolution resolution, VideoQuality.Quality quality,
                                      VideoFrameRate.FrameRate frameRate) {
        if (INSTANCE == null) {
            INSTANCE = new Profile(resolution, quality, frameRate);
        }
        return INSTANCE;
    }

    //getter resolution, width, height values.
    public VideoResolution getVideoResolution(){
        return videoResolution;
    }

    public VideoResolution.Resolution getResolution(){
        return resolution;
    }

    public void setResolution(VideoResolution.Resolution resolution){
        this.resolution = resolution;
        videoResolution = new VideoResolution(resolution);
    }

    // getter videoBitRate;
    public VideoQuality getVideoQuality() {
        return videoQuality;
    }

    public VideoQuality.Quality getQuality(){
        return quality;
    }

    public void setQuality(VideoQuality.Quality quality){
        this.quality = quality;
        videoQuality = new VideoQuality(quality);
    }

    public VideoFrameRate getVideoFrameRate(){
        return videoFrameRate;
    }

    public VideoFrameRate.FrameRate getFrameRate(){
        return frameRate;
    }

    public void setFrameRate(VideoFrameRate.FrameRate frameRate){
        this.frameRate = frameRate;
        videoFrameRate = new VideoFrameRate(frameRate);
    }
}
