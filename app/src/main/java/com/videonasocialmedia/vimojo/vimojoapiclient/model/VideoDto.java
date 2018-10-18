/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 2/2/18.
 */

/**
 * Model class for video API calls.
 * // TODO(jliarte): 13/07/18 rename to VideoDto
 */
public class VideoDto {
    String owner;
    String video;
    String poster;
    String description;
    String _id;

    public VideoDto(String owner, String video, String poster, String description, String _id) {
        this.owner = owner;
        this.video = video;
        this.poster = poster;
        this.description = description;
        this._id = _id;
    }

    public String getOwner() {
        return owner;
    }

    public String getVideo() {
        return video;
    }

    public String getPoster() {
        return poster;
    }

    public String getDescription() {
        return description;
    }

    public String get_id() {
        return _id;
    }

}
