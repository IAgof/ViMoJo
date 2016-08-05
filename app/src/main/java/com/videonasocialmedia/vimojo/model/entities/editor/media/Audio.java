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
package com.videonasocialmedia.vimojo.model.entities.editor.media;

import android.media.MediaMetadata;

import com.videonasocialmedia.vimojo.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.vimojo.model.entities.licensing.License;
import com.videonasocialmedia.vimojo.model.entities.social.User;

import java.util.ArrayList;

/**
 * An audio media item that represents a file  (or part of a file) that can be used in project audio
 * track.
 *
 * @see com.videonasocialmedia.vimojo.model.entities.editor.media.Media
 */
public class Audio extends Media {

    public static String AUDIO_PATH = "";

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.vimojo.model.entities.editor.media.Media
     */
    public Audio(int identifier, String iconPath, String mediaPath, int fileStartTime, int duration, ArrayList<User> authors, License license) {
        super(identifier, iconPath, mediaPath, fileStartTime, duration, authors, license);
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.vimojo.model.entities.editor.media.Media
     */
    public Audio(int identifier, String iconPath, String selectedIconPath, String title, String mediaPath, int fileStartTime, int duration, Transition opening, Transition ending, MediaMetadata metadata, ArrayList<User> authors, License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, fileStartTime, duration, opening, ending, metadata, authors, license);
    }

    @Override
    public void setIdentifier() {

    }
}