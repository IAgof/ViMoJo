package com.videonasocialmedia.vimojo.eventbus.events.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by Veronica Lago Fominaya on 26/08/2015.
 */
public class VideoInsertedEvent {
    public final Video video;
    public final int position;

    public VideoInsertedEvent(Video video, int position) {
        this.video = video;
        this.position = position;
    }
}
