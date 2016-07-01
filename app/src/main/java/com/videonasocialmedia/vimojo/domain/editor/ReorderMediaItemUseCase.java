package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnReorderMediaListener;

/**
 * Created by jca on 7/7/15.
 */
public class ReorderMediaItemUseCase {

    public void moveMediaItem(Media media, int toPositon, OnReorderMediaListener listener){
        Project project= Project.getInstance(null,null,null);
        MediaTrack videoTrack= project.getMediaTrack();
        try {
            videoTrack.moveItemTo(toPositon,media);
            listener.onMediaReordered(media, toPositon);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            listener.onErrorReorderingMedia();
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            illegalOrphanTransitionOnTrack.printStackTrace();
            listener.onErrorReorderingMedia();
        }
    }
}
