package com.videonasocialmedia.vimojo.sources;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MusicSource {

    Context context = VimojoApplication.getAppContext();

    private List<Music> localMusic = new ArrayList();

    public List<Music> retrieveLocalMusic() {
        if (localMusic.size() == 0)
            populateLocalMusic();
            addPathToMusic(localMusic);
        return localMusic;
    }

    private void addPathToMusic(List<Music> localMusic) {

        for(Music music: localMusic){
            File musicFile = Utils.getMusicFileByName(music.getMusicTitle(),music.getMusicResourceId());
            music.setMediaPath(musicFile.getAbsolutePath());
        }
    }

    private void populateLocalMusic() {


        localMusic.add(new Music(R.drawable.ic_free_the_cold_wind_george_stephenson_bradford_lawrence_ellis, "Free the cold wind",
                R.raw.free_the_gold_wind, R.color.folk, "George Stephenson, Bradford Lawrence Ellis", "02:45"));

        localMusic.add(new Music(R.drawable.ic_galloping_a_stuart_roslyn_matt_foundling, "Galloping",
                R.raw.galloping, R.color.folk, "Stuart Roslyn, Matt Foundling", "02:01"));


        localMusic.add(new Music(R.drawable.ic_sorrow_and_sadness_b_david_john_vanacore_ehren_ebbage, "Sorrow and sadness",
                R.raw.sorrow_and_sadness_b, R.color.folk, "David Jhon, Vanacore Ehrenebbage", "01:33"));


        localMusic.add(new Music(R.drawable.ic_we_beat_as_one_b_harlin_james_paul_lewis1, "We beat as one",
                R.raw.we_beat_as_one_b, R.color.folk, "Harlin James, Paul Lewis", "03:30"));

    }

    public Music getAudioByTitle(String title){
        for (Music music:localMusic
             ) {
            if(music.getTitle() == title){
                return music;
            }
        }
        return null;
    }
}
