package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;


import javax.inject.Inject;

/**
 * Created by jliarte on 31/05/16.
 */
public class GetAudioFromProjectUseCase {
    public Project project;

    @Inject
    public GetAudioFromProjectUseCase() {
        project = Project.getInstance(null, null, null);
    }

    public void getMusicFromProject(GetMusicFromProjectCallback listener) {
        getItemsOnAudioTrack(listener, Constants.INDEX_AUDIO_TRACK_MUSIC);
    }

    public void getVoiceOverFromProject(GetMusicFromProjectCallback listener) {
        getItemsOnAudioTrack(listener, Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    }

    private void getItemsOnAudioTrack(GetMusicFromProjectCallback listener, int indexAudioTrack) {
        Music music = null;
        try {
            music = (Music) project.getAudioTracks()
                .get(indexAudioTrack).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onMusicRetrieved(music);
    }

    public boolean hasBeenMusicSelected(){
      return (project.getVMComposition().hasMusic());
    }
}
