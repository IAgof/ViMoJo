package com.videonasocialmedia.vimojo.eventbus.events;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by jca on 16/9/15.
 */
public class AddMediaItemToTrackSuccessEvent {
    public final Video videoAdded;
    public AddMediaItemToTrackSuccessEvent(Video video) {
        videoAdded=video;
    }
}
