package com.videonasocialmedia.vimojo.repository.project;

import io.realm.RealmObject;

/**
 * Created by jliarte on 20/10/16.
 */
public class ProjectRealm extends RealmObject {
  public String title;
  public String rootPath;
  public String quality;
  public String resolution;
}
