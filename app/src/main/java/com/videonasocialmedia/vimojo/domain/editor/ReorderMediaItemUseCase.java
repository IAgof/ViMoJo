package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnReorderMediaListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by jca on 7/7/15.
 */
public class ReorderMediaItemUseCase {
    protected ProjectRepository projectRepository = new ProjectRealmRepository();

    public void moveMediaItem(Media media, int toPositon, OnReorderMediaListener listener){
        Project project = Project.getInstance(null,null,null);
        MediaTrack videoTrack = project.getMediaTrack();
        try {
            videoTrack.moveItemTo(toPositon,media);
            new ReorderProjectVideoListUseCase().reorderVideoList();
            projectRepository.update(project);
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
