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
  private final Context context;

  public MusicProvider(Context context) {
    this.context = context;
  }

  public List<Music> getMusicAppsInstalled() {
    List<Music> musicList = new ArrayList<>();
    musicList.add(new Music(R.drawable.ic_free_the_cold_wind_george_stephenson_bradford_lawrence_ellis, "Free the cold wind",
        R.raw.free_the_gold_wind, R.color.colorPrimary, "George Stephenson, Bradford Lawrence Ellis", "02:45"));
    musicList.add(new Music(R.drawable.ic_galloping_a_stuart_roslyn_matt_foundling, "Galloping",
        R.raw.galloping, R.color.colorPrimary, "Stuart Roslyn, Matt Foundling", "02:01"));
    musicList.add(new Music(R.drawable.ic_sorrow_and_sadness_b_david_john_vanacore_ehren_ebbage, "Sorrow and sadness",
        R.raw.sorrow_and_sadness_b, R.color.colorPrimary, "David Jhon, Vanacore Ehrenebbage", "01:33"));
    musicList.add(new Music(R.drawable.ic_we_beat_as_one_b_harlin_james_paul_lewis1, "We beat as one",
        R.raw.we_beat_as_one_b, R.color.colorPrimary, "Harlin James, Paul Lewis", "03:30"));
    return  musicList;
  }
}
