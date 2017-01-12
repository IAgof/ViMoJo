package com.videonasocialmedia.vimojo.sources;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.model.sources.MusicProvider;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MusicSource {

    protected List<Music> localMusic = new ArrayList();

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
        for(Music music: new MusicProvider().getMusicAppsInstalled()){
            localMusic.add(music);
        }
    }

    public Music getMusicByTitle(String musicTitle) {
        // TODO(jliarte): 23/10/16 workarround for voice over persistence
        if (musicTitle.equals(Constants.MUSIC_AUDIO_MIXED_TITLE)) {
            Music music = new Music(Constants.OUTPUT_FILE_MIXED_AUDIO);
            music.setMusicTitle(musicTitle);
            return music;
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
