/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.model.entities.editor.utils;

public class VideoQuality {

    private final int videoBitRate;

    public VideoQuality(Quality quality) {
        switch (quality) {
            case LOW:
                this.videoBitRate = 2000000;
                break;
            case GOOD:
                this.videoBitRate = 10000000;
                break;
            case HIGH:
            default:
                this.videoBitRate = 5000000;
        }
    }

    public int getVideoBitRate() {
        return videoBitRate;
    }

    public enum Quality {
        LOW, HIGH, GOOD
    }
}
