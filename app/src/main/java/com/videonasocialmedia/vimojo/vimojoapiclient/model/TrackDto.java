package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by jliarte on 12/07/18.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model class for Track vimojo API calls.
 */
public class TrackDto {
  @SerializedName("_id") public String id;
  @SerializedName("uuid") public String uuid;
  @SerializedName("trackIndex") public int trackIndex;
  @SerializedName("volume") public float volume;
  @SerializedName("muted") public boolean muted;
  @SerializedName("position") public int position;
  @SerializedName("compositionId") public String compositionId;
  @SerializedName("medias") public List<MediaDto> mediaItems;

  public TrackDto() {
    this.mediaItems = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public int getTrackIndex() {
    return trackIndex;
  }

  public float getVolume() {
    return volume;
  }

  public boolean isMuted() {
    return muted;
  }

  public int getPosition() {
    return position;
  }

  public String getCompositionId() {
    return compositionId;
  }

  public List<MediaDto> getMediaItems() {
    if (mediaItems != null) {
      return mediaItems;
    }
    return Collections.emptyList();
  }

  @Override
  public String toString() {
    return "TrackDto{"
            + "id='"
            + id
            + '\''
            + ", uuid='"
            + uuid
            + '\''
            + ", trackIndex='"
            + trackIndex
            + '\''
            + ", volume='"
            + volume
            + '\''
            + ", muted='"
            + muted
            + '\''
            + ", position='"
            + position
            + '\''
            + ", compositionId='"
            + compositionId
            + '\''
            + ", mediaItems='"
            + mediaItems
            +
            '}';
  }
}
