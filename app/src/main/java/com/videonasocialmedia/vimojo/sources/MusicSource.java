package com.videonasocialmedia.vimojo.sources;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
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
    }


    protected void populateLocalMusic() {
        if (BuildConfig.FLAVOR.compareTo(Constants.FLAVOR_RTVE) == 0){
            localMusic.add(new Music(R.drawable.ic_free_the_cold_wind_george_stephenson_bradford_lawrence_ellis, "Free the cold wind",
                R.raw.free_the_gold_wind, R.color.colorPrimary, "George Stephenson, Bradford Lawrence Ellis", "02:45"));
            localMusic.add(new Music(R.drawable.ic_galloping_a_stuart_roslyn_matt_foundling, "Galloping",
                R.raw.galloping, R.color.colorPrimary, "Stuart Roslyn, Matt Foundling", "02:01"));
            localMusic.add(new Music(R.drawable.ic_sorrow_and_sadness_b_david_john_vanacore_ehren_ebbage, "Sorrow and sadness",
                R.raw.sorrow_and_sadness_b, R.color.colorPrimary, "David Jhon, Vanacore Ehrenebbage", "01:33"));
            localMusic.add(new Music(R.drawable.ic_we_beat_as_one_b_harlin_james_paul_lewis1, "We beat as one",
                R.raw.we_beat_as_one_b, R.color.colorPrimary, "Harlin James, Paul Lewis", "03:30"));
        } else{
            localMusic.add(new Music(R.drawable.ic_free_the_cold_wind_george_stephenson_bradford_lawrence_ellis, "Update song 1",
                R.raw.free_the_gold_wind, R.color.colorPrimary, "George Stephenson, Bradford Lawrence Ellis", "02:45"));
            localMusic.add(new Music(R.drawable.ic_galloping_a_stuart_roslyn_matt_foundling, "Update song 2",
                R.raw.galloping, R.color.colorPrimary, "Stuart Roslyn, Matt Foundling", "02:01"));
            localMusic.add(new Music(R.drawable.ic_sorrow_and_sadness_b_david_john_vanacore_ehren_ebbage, "Update song 3",
                R.raw.sorrow_and_sadness_b, R.color.colorPrimary, "David Jhon, Vanacore Ehrenebbage", "01:33"));
            localMusic.add(new Music(R.drawable.ic_we_beat_as_one_b_harlin_james_paul_lewis1, "Update song 4",
                R.raw.we_beat_as_one_b, R.color.colorPrimary, "Harlin James, Paul Lewis", "03:30"));
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
