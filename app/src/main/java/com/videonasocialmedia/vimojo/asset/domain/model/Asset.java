package com.videonasocialmedia.vimojo.asset.domain.model;

/**
 * Created by jliarte on 20/07/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.domain.helper.HashCountGenerator;

import java.util.Date;

/**
 * Class for representing assets in a project
 */
public class Asset { // TODO(jliarte): 20/07/18 put this class in the Media hierarchy?
  public final static int MAX_NUM_TRIES_UPLOAD = 3;

  public String id;
  public String mediaId;
  public String name;
  public String type;
  public String hash;
  public String filename;
  public String mimetype;
  public String uri;
  public String projectId;
  public String date;
  public String creationDate;
  public String modificationDate;
  public String createdBy;
  public String mediaPath;
  public int numTries;
  public boolean acceptedUploadMobileNetwork = false;
  private HashCountGenerator hashCountGenerator = new HashCountGenerator(); // TODO(jliarte): 14/08/18 inject this?

  public Asset(String projectId, Media media) {
    this.projectId = projectId;
    this.mediaPath = media.getMediaPath();
    this.name = media.getTitle();
//    this.id = media.getUuid();
    this.mediaId = media.getUuid();
    // TODO(jliarte): 23/07/18 field/inject? :m
    this.hash = new HashCountGenerator().getHash(media.getMediaPath());
    if (media instanceof Video) {
      // TODO(jliarte): 23/07/18 MediaDto.MEDIA_TYPE_VIDEO?
      this.type = "video";
    } else {
      if (media instanceof Music) {
        if (media.getTitle().equals(com.videonasocialmedia.vimojo.utils.Constants
                .MUSIC_AUDIO_VOICEOVER_TITLE)) {
          this.type = "voiceOver";
        } else {
          this.type = "music";
        }
      }
    }
    this.numTries = 0;
  }

  public Asset() {

  }

  public String getId() {
    return id;
  }

  public String getMediaId() {
    return mediaId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    // TODO: 22/6/18 Handle error
    if (type == null) {
      return "video";
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getHash() {
    // TODO: 21/6/18 Generate hash
    return this.hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getMimetype() {
    return mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getDate() {
    // TODO: 21/6/18 Get date from system
    return new Date().toString();
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(String modificationDate) {
    this.modificationDate = modificationDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getMediaPath() {
    return mediaPath;
  }

  public int getNumTries() {
    return numTries;
  }

  public boolean isAcceptedUploadMobileNetwork() {
    // TODO: 21/6/18 Implement mobile network upload policy
    return acceptedUploadMobileNetwork;
  }

  public void incrementNumTries() {
    numTries++;
  }

}
