package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;


import javax.inject.Inject;

/**
 * Created by jliarte on 31/05/16.
 */
public class GetMusicFromProjectUseCase {
    public Project project;

    @Inject
    public GetMusicFromProjectUseCase() {
    }

    public void getMusicFromProject(GetMusicFromProjectCallback listener) {
        Music music = null;
        try {
            project = Project.getInstance(null, null, null);
            music = (Music) project.getAudioTracks()
                .get(Constants.INDEX_AUDIO_TRACKS_MUSIC).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
          listener.onMusicRetrieved(music);
    }

    public void getVoiceOverFromProject(GetMusicFromProjectCallback listener) {
        Music voiceOver = null;
        try {
            project = Project.getInstance(null, null, null);
            voiceOver = (Music) project.getAudioTracks()
                .get(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onMusicRetrieved(voiceOver);
    }

    public boolean hasBeenMusicSelected(){
      return (project.getVMComposition().hasMusic());
    }
}
