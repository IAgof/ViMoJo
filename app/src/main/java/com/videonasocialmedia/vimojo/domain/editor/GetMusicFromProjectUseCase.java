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
        project = Project.getInstance(null, null, null, null);
    }

    public void getMusicFromProject(GetMusicFromProjectCallback listener) {
        getItemsOnTrack(listener, Constants.INDEX_AUDIO_TRACK_MUSIC);
    }

    public void getVoiceOverFromProject(GetMusicFromProjectCallback listener) {
        getItemsOnTrack(listener, Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    }

    private void getItemsOnTrack(GetMusicFromProjectCallback listener, int indexAudioTrackVoiceOver) {
        Music voiceOver = null;
        try {
            voiceOver = (Music) project.getAudioTracks()
                .get(indexAudioTrackVoiceOver).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onMusicRetrieved(voiceOver);
    }

    public boolean hasBeenMusicSelected(){
      return (project.getVMComposition().hasMusic());
    }
}
