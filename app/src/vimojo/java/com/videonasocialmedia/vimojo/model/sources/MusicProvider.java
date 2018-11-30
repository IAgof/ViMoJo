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
    musicList.add(new Music(R.drawable.ic_toe_jam, "Toe Jam", R.raw.toe_jam,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_the_toe_jam), "03:44", 224000));
    musicList.add(new Music(R.drawable.ic_the_missing_link, "The Missing Link", R.raw.the_missing_link,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_the_missing_link), "02:43", 163000));
    musicList.add(new Music(R.drawable.ic_mirror_mirror, "Mirror Mirror", R.raw.mirror_mirror,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_mirror_mirror), "03:05", 185000));
    musicList.add(new Music(R.drawable.ic_lusciousness, "Lusciousness", R.raw.lusciousness,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_lusciousness), "03:24", 204000));
   /* musicList.add(new Music(R.drawable.ic_club_thump, "Club Thump", R.raw.club_thump,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_club_thump), "02:55", 175000));
    musicList.add(new Music(R.drawable.ic_blue_macaw, "Blue Macaw", R.raw.blue_macaw,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_blue_macaw), "03:51", 231000));
    musicList.add(new Music(R.drawable.ic_arp_bounce, "Arp Bounce", R.raw.arp_bounce,
        R.color.colorPrimary,
        context.getString(R.string.activity_music_title_arp_bounce), "04:07", 247000)); */

    return  musicList;
  }
}
