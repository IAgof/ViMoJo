package com.videonasocialmedia.videonamediaframework.model.media.utils;

/**
 * Created by alvaro on 18/10/16.
 */

public class VideoFrameRate {

    private final int videoFrameRate;

    public VideoFrameRate(FrameRate frameRate) {
        switch (frameRate) {
            case FPS24:
                this.videoFrameRate = 24;
                break;
            case FPS30:
                this.videoFrameRate = 30;
                break;
            case FPS25:
                this.videoFrameRate = 25;
                break;
            case NOT_SUPPORTED:
            default:
                this.videoFrameRate = 0;
        }
    }

    public int getFrameRate() {
        return videoFrameRate;
    }

    public enum FrameRate {
        FPS24, FPS25, FPS30, NOT_SUPPORTED
    }
}
