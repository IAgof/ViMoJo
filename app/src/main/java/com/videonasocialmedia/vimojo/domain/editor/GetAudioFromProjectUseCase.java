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

    public void getMusicFromProject(Project project, GetMusicFromProjectCallback listener) {
        getItemsOnAudioTrack(project, listener, Constants.INDEX_AUDIO_TRACK_MUSIC);
    }

    public void getVoiceOverFromProject(Project project, GetMusicFromProjectCallback listener) {
        getItemsOnAudioTrack(project, listener, Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    }

    private void getItemsOnAudioTrack(Project project, GetMusicFromProjectCallback listener,
                                      int indexAudioTrack) {
        Music music = null;
        try {
            music = (Music) project.getAudioTracks()
                .get(indexAudioTrack).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onMusicRetrieved(music);
    }

    public boolean hasBeenMusicSelected(Project project){
      return (project.getVMComposition().hasMusic());
    }
}
