package com.videonasocialmedia.vimojo.eventbus.events.video;

import com.videonasocialmedia.vimojo.eventbus.events.error.ErrorEvent;

/**
 * Created by Veronica Lago Fominaya on 26/08/2015.
 */
public class VideoInsertedErrorEvent extends ErrorEvent {
    public VideoInsertedErrorEvent(Throwable exception, String message) {
        super(exception, message);
    }
}
