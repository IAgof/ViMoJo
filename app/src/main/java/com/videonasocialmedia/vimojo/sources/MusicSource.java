package com.videonasocialmedia.vimojo.sources;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;
import com.videonasocialmedia.vimojo.utils.Utils;
import com.videonasocialmedia.vimojo.model.sources.MusicProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MusicSource {
    protected List<Music> localMusic = new ArrayList();
    private Context context;

    public MusicSource(Context context) {
        this.context = context;
    }

    public List<Music> retrieveLocalMusic() {
        if (localMusic.size() == 0)
            populateLocalMusic();
            addPathToMusic(localMusic);
        return localMusic;
    }

    protected void addPathToMusic(List<Music> localMusic) {
        for(Music music: localMusic){
            File musicFile = Utils.getMusicFileByName(music.getMusicTitle(),music.getMusicResourceId());
            music.setMediaPath(musicFile.getAbsolutePath());
        }
        this.localMusic = localMusic;
    }


    protected void populateLocalMusic() {
        for (Music music: new MusicProvider(context).getMusicAppsInstalled()) {
            localMusic.add(music);
        }
    }

    public Music getMusicByTitle(String projectPathIntermediateFile, String musicTitle) {
        // TODO(jliarte): 23/10/16 workarround for voice over persistence
        if (musicTitle.equals(Constants.MUSIC_AUDIO_VOICEOVER_TITLE)) {
            String musicPath = projectPathIntermediateFile + File.separator +
                    Constants.AUDIO_TEMP_RECORD_VOICE_OVER_FILENAME;
            Music voiceOver = new Music(musicPath, FileUtils.getDuration(musicPath));
            voiceOver.setMusicTitle(musicTitle);
            voiceOver.setMusicAuthor(" ");
            voiceOver.setIconResourceId(R.drawable.activity_edit_audio_voice_over_icon);
            return voiceOver;
        }
        populateLocalMusic();
        addPathToMusic(localMusic);
        for (Music musicItem: localMusic) {
            if (musicItem.getMusicTitle().equals(musicTitle)) {
                return musicItem;
            }
        }
        return null;
    }

//    public Music getAudioByTitle(String title){
//        for (Music music:localMusic
//             ) {
//            if(music.getTitle() == title){
//                return music;
//            }
//        }
//        return null;
//    }
}
