package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by jliarte on 11/07/18.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class for Composition vimojo API calls.
 */
public class CompositionDto {
  @SerializedName("_id") public String id;
  @SerializedName("uuid") public String uuid;
  @SerializedName("title") public String title;
  @SerializedName("description") public String description;
  @SerializedName("remoteProjectPath") public String projectPath;
  @SerializedName("quality") public String quality;
  @SerializedName("resolution") public String resolution;
  @SerializedName("frameRate") public String frameRate;
  @SerializedName("duration") public int duration;
  @SerializedName("isAudioFadeTransitionActivated") public boolean isAudioFadeTransitionActivated;
  @SerializedName("isVideoFadeTransitionActivated") public boolean isVideoFadeTransitionActivated;
  @SerializedName("isWatermarkActivated") public boolean isWatermarkActivated;
  @SerializedName("productType") public String productType;
  @SerializedName("poster") public String poster;
  @SerializedName("project") public String projectId;
  @SerializedName("date") public Date date;
  @SerializedName("tracks") public List<TrackDto> tracks;
  @SerializedName("creation_date") public Date creation_date;
  @SerializedName("modification_date") public Date modification_date;
  @SerializedName("created_by") public String ownerId;

  public CompositionDto() {
    this.tracks = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getProjectPath() {
    return projectPath;
  }

  public String getQuality() {
    return quality;
  }

  public String getResolution() {
    return resolution;
  }

  public String getFrameRate() {
    return frameRate;
  }

  public int getDuration() {
    return duration;
  }

  public boolean isAudioFadeTransitionActivated() {
    return isAudioFadeTransitionActivated;
  }

  public boolean isVideoFadeTransitionActivated() {
    return isVideoFadeTransitionActivated;
  }

  public boolean isWatermarkActivated() {
    return isWatermarkActivated;
  }

  public String getProductType() {
    return productType;
  }

  public String getPoster() {
    return poster;
  }

  public String getProjectId() {
    return projectId;
  }

  public Date getDate() {
    return date;
  }

  public Date getCreation_date() {
    return creation_date;
  }

  public Date getModification_date() {
    return modification_date;
  }

  public String getOwnerId() {
    return ownerId;
  }

  @Override
  public String toString() {
    return "CompositionDto{"
            + "id='"
            + id
            + '\''
            + ", uuid='"
            + uuid
            + '\''
            + ", title='"
            + title
            + '\''
            + ", description='"
            + description
            + '\''
            + ", projectPath='"
            + projectPath
            + '\''
            + ", quality='"
            + quality
            + '\''
            + ", resolution='"
            + resolution
            + '\''
            + ", frameRate='"
            + frameRate
            + '\''
            + ", duration='"
            + duration
            + '\''
            + ", audioTransitions='"
            + isAudioFadeTransitionActivated
            + '\''
            + ", videoTransitions='"
            + isVideoFadeTransitionActivated
            + '\''
            + ", watermark='"
            + isWatermarkActivated
            + '\''
            + ", productTypes='"
            + productType
            + '\''
            + ", poster='"
            + poster
            + '\''
            + ", date='"
            + date
            + '\''
            + ", creation_date='"
            + creation_date
            + '\''
            + ", modification_date='"
            + modification_date
            + '\''
            + ", ownerId='"
            + ownerId
            +
            '}';
  }

}
