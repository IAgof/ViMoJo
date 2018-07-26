/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 21/6/18.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Model class for asset vimojo API calls.
 */
public class AssetDto {
  @SerializedName("_id") public String id;
  @SerializedName("mediaId") public String mediaId;
  @SerializedName("name") public String name;
  @SerializedName("type") public String type;
  @SerializedName("hash") public String hash;
  @SerializedName("filename") public String filename;
  @SerializedName("mimetype") public String mimetype;
  @SerializedName("uri") public String uri;
  @SerializedName("projectId") public String projectId;
  @SerializedName("date") public String date;
  @SerializedName("creation_date") public String creationDate;
  @SerializedName("modification_date") public String modificationDate;
  @SerializedName("created_by") public String createdBy;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getHash() {
    return hash;
  }

  public String getDate() {
    return date;
  }

  public String getFilename() {
    return filename;
  }

  public String getMimetype() {
    return mimetype;
  }

  public String getUri() {
    return uri;
  }

  public String getProjectId() {
    return projectId;
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

  public String getMediaId() {
    return mediaId;
  }

  @Override
  public String toString() {
    return "MediaDto{"
            + "id='"
            + id
            + '\''
            + "mediaId='"
            + mediaId
            + '\''
            + ", name='"
            + name
            + '\''
            + ", type='"
            + type
            + '\''
            + ", hash='"
            + hash
            + '\''
            + ", filename='"
            + filename
            + '\''
            + ", mimetype='"
            + mimetype
            + '\''
            + ", uri='"
            + uri
            + '\''
            + ", projectId='"
            + projectId
            + '\''
            + ", date='"
            + date
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
