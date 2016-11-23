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
package com.videonasocialmedia.videonamediaframework.model.media.track;

import com.videonasocialmedia.videonamediaframework.model.media.effects.Effect;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Audio;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.transitions.Transition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * An audio track is a track that can only contain Audio media items. There could be many audio
 * tracks in a project.
 * <p/>
 * Created by dfa on 30/3/15.
 */
public class AudioTrack extends Track {

    /**
     * Constructor of minimum number of parameters. Default constructor. Used when a new project
     * is launched.
     *
     * @see com.videonasocialmedia.videonamediaframework.model.media.track.Track
     */
    public AudioTrack() {
        super();
    }

    /**
     * Parametrized constructor. Used when a saved project is launched.
     *
     * @see com.videonasocialmedia.videonamediaframework.model.media.track.Track
     */
    public AudioTrack(LinkedList<Media> items, HashMap<Integer, LinkedList<Effect>> effects,
                      HashMap<String, Transition> transitions) {
        super(items, effects, transitions);
        this.checkItems();
    }

    /**
     * Ensure there are only Audio items on items list.
     */
    private void checkItems() {
        for (Object item : this.getItems()) {
            if (!(item instanceof Audio)) {
                this.items.removeFirstOccurrence(item);
            }
        }
    }

    /**
     * Insert a new Audio item in the AudioTrack. Get sure it is an Audio media item.
     *
     * @throws IllegalItemOnTrack - when trying to add a Audio item on
     * @see com.videonasocialmedia.videonamediaframework.model.media.track.Track
     */
    @Override
    public boolean insertItemAt(int position, Media itemToAdd) throws IllegalItemOnTrack {
        if (!(itemToAdd instanceof Audio)) {
            throw new IllegalItemOnTrack("Audio track can only have audio media items.");
        }
        return super.insertItemAt(position, itemToAdd);
    }

    @Override
    public boolean insertItem(Media itemToAdd) throws IllegalItemOnTrack {
        if (!(itemToAdd instanceof Audio)) {
            throw new IllegalItemOnTrack("Audio track can only have audio media items.");
        }
        return super.insertItem(itemToAdd);
    }

    /**
     * Delete Media item. Get his position and deletes from the list.
     *
     * @param itemToDelete - Media item to be deleted.
     * @return TRUE if the list contained the specified element.
     */
    @Override
    public Media deleteItem(Media itemToDelete) throws IllegalOrphanTransitionOnTrack,
            NoSuchElementException, IndexOutOfBoundsException, IllegalItemOnTrack {
        return super.deleteItemAt(this.items.indexOf(itemToDelete));
    }

    /**
     * Delete Media item on the given position.
     *
     * @param position
     */
    @Override
    public Media deleteItemAt(int position) throws IllegalOrphanTransitionOnTrack,
            NoSuchElementException, IllegalItemOnTrack {
        if (!(this.items.get(position) instanceof Audio)) {
            throw new IllegalItemOnTrack("Audio track can only have audio media items.");
        }
        return super.deleteItemAt(position);
    }

    /**
     * Moves Media item to the given position.
     *
     * @param newPosition - The new position in the track for the media item.
     * @param itemToMove  - The media item to ve moved.
     */
    @Override
    public boolean moveItemTo(int newPosition, Media itemToMove) throws IllegalItemOnTrack,
            IllegalOrphanTransitionOnTrack {
        if (!(itemToMove instanceof Audio)) {
            throw new IllegalItemOnTrack("Audio track can only have audio media items.");
        }
        return super.moveItemTo(newPosition, itemToMove);
    }

    @Override
    public void setItems(LinkedList<Media> items) {
        super.setItems(items);
        this.checkItems();
    }
}
