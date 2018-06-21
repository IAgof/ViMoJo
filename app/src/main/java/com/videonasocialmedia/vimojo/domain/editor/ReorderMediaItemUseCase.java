package com.videonasocialmedia.vimojo.domain.editor;

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnReorderMediaListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by jca on 7/7/15.
 */
public class ReorderMediaItemUseCase {
    private final static String TAG = ReorderMediaItemUseCase.class.getCanonicalName();
    protected ProjectRepository projectRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public ReorderMediaItemUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void moveMediaItem(Project currentProject, int fromPosition, int toPosition,
                              OnReorderMediaListener listener){
      Track videoTrack = currentProject.getMediaTrack();
        try {
          Media media = videoTrack.getItems().get(fromPosition);
          videoTrack.moveItemTo(toPosition, media);
          new ReorderProjectVideoListUseCase().reorderVideoList(currentProject);
          projectRepository.update(currentProject);
          logTrack(videoTrack);
          listener.onSuccessMediaReordered();
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            listener.onErrorReorderingMedia();
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            illegalOrphanTransitionOnTrack.printStackTrace();
            listener.onErrorReorderingMedia();
        }
    }

  private void logTrack(Track videoTrack) {
    String logstr = "Video track order: ";
    for (Media item: videoTrack.getItems()) {
      logstr += item.getIdentifier() + " pos: " + item.getPosition() + ", ";
    }
    Log.d(TAG, "timeline: " + logstr);
  }

}
