package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditNavigatorView;

/**
 *
 */
public class EditNavigatorPresenter {

    EditNavigatorView navigatorView;
    private Project project;

    public EditNavigatorPresenter(EditNavigatorView navigatorView) {
        this.navigatorView = navigatorView;
        project = Project.getInstance(null, null, null, null);
        areThereVideosInProject();
    }

    public void areThereVideosInProject() {
        if (project.getMediaTrack().getNumVideosInProject() > 0)
            navigatorView.enableNavigatorActions();
        else
            navigatorView.disableNavigatorActions();
    }

    public void checkMusicAndNavigate() {
        GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        getMusicFromProjectUseCase.getMusicFromProject(new GetMusicFromProjectCallback() {
            @Override
            public void onMusicRetrieved(Music music) {
                navigatorView.goToMusic(music);
            }

        });
    }

}
