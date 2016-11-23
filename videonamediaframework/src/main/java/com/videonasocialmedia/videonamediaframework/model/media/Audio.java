/*
 * Copyright (c) 2015. Videona Socialmedia SL
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

import android.media.MediaMetadata;

import com.videonasocialmedia.videonamediaframework.model.media.transitions.Transition;
import com.videonasocialmedia.videonamediaframework.model.licensing.License;

/**
 * An audio media item that represents a file  (or part of a file) that can be used in project audio
 * track.
 *
 * @see com.videonasocialmedia.videonamediaframework.model.media.Media
 */
public class Audio extends Media {

    public static String AUDIO_PATH = "";

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videonamediaframework.model.media.Media
     */
    public Audio(int identifier, String iconPath, String mediaPath, int fileStartTime, int duration,
                 License license) {
        super(identifier, iconPath, mediaPath, fileStartTime, duration, license);
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videonamediaframework.model.media.Media
     */
    public Audio(int identifier, String iconPath, String selectedIconPath, String title,
                 String mediaPath, int fileStartTime, int duration, Transition opening,
                 Transition ending, MediaMetadata metadata, License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, fileStartTime, duration,
                opening, ending, metadata, license);
    }

    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public void createIdentifier() {

    }
}