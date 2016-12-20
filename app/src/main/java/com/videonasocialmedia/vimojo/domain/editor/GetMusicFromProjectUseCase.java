package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by jliarte on 31/05/16.
 */
public class GetMusicFromProjectUseCase {
    public Project project;
    private ProjectRepository projectRepository = new ProjectRealmRepository();

    public GetMusicFromProjectUseCase() {
        this.project = projectRepository.getCurrentProject();
    }

    public void getMusicFromProject(GetMusicFromProjectCallback listener) {

        Music music = null;
        try {
            music = (Music) project.getAudioTracks().get(0).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
          listener.onMusicRetrieved(music);
    }
}
