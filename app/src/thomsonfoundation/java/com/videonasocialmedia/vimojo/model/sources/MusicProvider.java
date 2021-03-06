package com.videonasocialmedia.vimojo.model.sources;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 12/01/17.
 */

public class MusicProvider {
  Context context;

  public MusicProvider(Context context) {
    this.context = context;
  }

  public List<Music> getMusicAppsInstalled() {
    List<Music> musicList = new ArrayList<>();
    musicList.add(new Music(R.drawable.ic_going_higher, "Going higher", R.raw.goinghigher,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_going_higher), "04:04", 244000));
    musicList.add(new Music(R.drawable.ic_ukulele, "Ukulele",
        R.raw.ukulele, R.color.colorPrimary,
        context.getString(R.string.activity_music_title_ukulele),"02:26", 146000));
    musicList.add(new Music(R.drawable.ic_jazzy_frenchy,
        "Jazzy-Frenchy", R.raw.jazzyfrenchy, R.color.colorPrimary,
        context.getString(R.string.activity_music_title_jazzy_frenchy), "01:44", 104000));
    musicList.add(new Music(R.drawable.ic_acoustic_breeze, "Acoustic Breeze",
        R.raw.acousticbreeze, R.color.colorPrimary,
        context.getString(R.string.activity_music_title_acoustic_breeze), "02:37", 157000));
    return  musicList;
  }
}
