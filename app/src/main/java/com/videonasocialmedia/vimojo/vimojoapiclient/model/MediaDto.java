package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by jliarte on 13/07/18.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Media vimojo API calls.
 */
public class MediaDto {
  public static final String MEDIA_TYPE_VIDEO = "video";
  public static final String MEDIA_TYPE_MUSIC = "music";
  @SerializedName("_id") public String id;
  @SerializedName("uuid") public String uuid;
  @SerializedName("trackId") public String trackId;
  @SerializedName("mediaType") public String mediaType;
  @SerializedName("position") public int position;
  @SerializedName("mediaPath") public String mediaPath;
  @SerializedName("volume") public float volume;
  @SerializedName("remoteTempPath") public String tempPath;
  @SerializedName("clipText") public String clipText;
  @SerializedName("clipTextPosition") public String clipTextPosition;
  @SerializedName("hasText") public boolean hasText = false;
  @SerializedName("trimmed") public boolean trimmed = false;
  @SerializedName("startTime") public int startTime;
  @SerializedName("stopTime") public int stopTime;
  @SerializedName("videoError") public String videoError;
  @SerializedName("transcodeFinished") public boolean transcodeFinished = true;
  @SerializedName("assetId") public String assetId;
  @SerializedName("asset") public AssetDto asset;
  @SerializedName("creation_date") public String creationDate;
  @SerializedName("modification_date") public String modificationDate;
  @SerializedName("created_by") public String createdBy;
  @SerializedName("duration") public int duration;
  @SerializedName("title") public String title;
  @SerializedName("author") public String author;
  @SerializedName("iconResorceId") public int iconResourceId;

  public String getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public String getTrackId() {
    return trackId;
  }

  public String getMediaType() {
    return mediaType;
  }

  public int getPosition() {
    return position;
  }

  public String getMediaPath() {
    return mediaPath;
  }

  public float getVolume() {
    return volume;
  }

  public String getTempPath() {
    return tempPath;
  }

  public String getClipText() {
    return clipText;
  }

  public String getClipTextPosition() {
    return clipTextPosition;
  }

  public boolean isHasText() {
    return hasText;
  }

  public boolean isTrimmed() {
    return trimmed;
  }

  public int getStartTime() {
    return startTime;
  }

  public int getStopTime() {
    return stopTime;
  }

  public String getVideoError() {
    return videoError;
  }

  public boolean isTranscodeFinished() {
    return transcodeFinished;
  }

  public String getAssetId() {
    return assetId;
  }

  public AssetDto getAsset() {
    return asset;
  }

  public int getDuration() {
    return duration;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public int getIconResourceId() {
    return iconResourceId;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public String getModificationDate() {
    return modificationDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  @Override
  public String toString() {
    return "MediaDto{"
            + "id='"
            + id
            + '\''
            + ", uuid='"
            + uuid
            + '\''
            + ", trackId='"
            + trackId
            + '\''
            + ", mediaType='"
            + mediaType
            + '\''
            + ", position='"
            + position
            + '\''
            + ", mediaPath='"
            + mediaPath
            + '\''
            + ", volume='"
            + volume
            + '\''
            + ", tempPath='"
            + tempPath
            + '\''
            + ", clipText='"
            + clipText
            + '\''
            + ", clipTextPosition='"
            + clipTextPosition
            + '\''
            + ", clipTextPosition='"
            + hasText
            + '\''
            + ", trimmed='"
            + trimmed
            + '\''
            + ", startTime='"
            + startTime
            + '\''
            + ", stopTime='"
            + stopTime
            + '\''
            + ", videoError='"
            + videoError
            + '\''
            + ", transcodeFinished='"
            + transcodeFinished
            + '\''
            + ", assetId='"
            + assetId
            + '\''
            + ", asset='"
            + asset
            + '\''
            + ", creationDate='"
            + creationDate
            + '\''
            + ", modificationDate='"
            + modificationDate
            + '\''
            + ", createdBy='"
            + createdBy
            +
            '}';
  }

}
