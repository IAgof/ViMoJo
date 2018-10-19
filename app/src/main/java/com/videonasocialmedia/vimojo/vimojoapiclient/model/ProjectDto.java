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
 * Model class for project vimojo API calls.
 */
public class ProjectDto {
  @SerializedName("_id") private String id;
  @SerializedName("name") private String name;
  @SerializedName("location") public String location;
  @SerializedName("date") public String date;
  @SerializedName("poster") public String poster;
  @SerializedName("creation_date") public String creationDate;
  @SerializedName("modification_date") public String modificationDate;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }

  public String getDate() {
    return date;
  }

  public String getPoster() {
    return poster;
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

  @SerializedName("created_by") public String createdBy;

  @Override
  public String toString() {
    return "MediaDto{"
            + "id='"
            + id
            + '\''
            + ", name='"
            + name
            + '\''
            + ", location='"
            + location
            + '\''
            + ", date='"
            + date
            + '\''
            + ", poster='"
            + poster
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
