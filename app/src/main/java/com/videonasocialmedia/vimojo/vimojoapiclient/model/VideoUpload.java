/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

public class VideoUpload {

    private String authToken;
    private String mediaPath;
    private String description;
    private int numTries;
    public final static int MAX_NUM_TRIES_UPLOAD = 3;

    public VideoUpload(String authToken, String mediaPath, String description) {
        this.authToken = authToken;
        this.mediaPath = mediaPath;
        this.description = description;
        this.numTries = 0;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public String getDescription() {
        return description;
    }

    public int getNumTries() { return numTries; }

    public void incrementNumTries() { numTries++; }
}
