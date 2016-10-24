package com.videonasocialmedia.vimojo.model;

import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by jliarte on 24/10/16.
 */
public class VimojoMigration implements RealmMigration {
  @Override
  public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
    // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
    // with the same object creation and query capabilities.
    // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
    // renamed.

    // Access the Realm schema in order to create, modify or delete classes and their fields.
    RealmSchema schema = realm.getSchema();
    /************************************************
        // Version 1
        class RealmProject extends RealmObject {
          @PrimaryKey
          String title;
          String projectPath;
          String quality;
          String resolution;
          String musicTitle;
          float musicVolume = Music.DEFAULT_MUSIC_VOLUME;
          RealmList<RealmVideo> videos;

        class RealmVideo extends RealmObject {
          @PrimaryKey
          String uuid;
          int position;
          String mediaPath;
          String tempPath;
          boolean isTempPathFinished;
          String clipText;
          String clipTextPosition;
          boolean isTextToVideoAdded = false;
          boolean isTrimmedVideo = false;
          int startTime;
          int stopTime;

          // Version 2
          class RealmProject extends RealmObject {
            @PrimaryKey
            String title;
            String projectPath;
            String quality;
            String resolution;
            String frameRate; // Added frameRate field, set default value to
                                 VideoFrameRate.FrameRate.FPS25
            String musicTitle;
            float musicVolume = Music.DEFAULT_MUSIC_VOLUME;
            RealmList<RealmVideo> videos;

          class RealmVideo extends RealmObject {
            @PrimaryKey
            String uuid;
            int position;
            String mediaPath;
            String tempPath;
            boolean isTempPathFinished;
            String clipText;
            String clipTextPosition;
            boolean isTextToVideoAdded = false;
            boolean isTrimmedVideo = false;
            int startTime;
            int stopTime;
    ************************************************/
    // Migrate from version 1 to version 2
    if (oldVersion == 1) {
      schema.get("RealmProject").addField("frameRate", String.class)
              .transform(new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                  obj.setString("frameRate", VideoFrameRate.FrameRate.FPS25.name());
                }
              });
      oldVersion++;

    }
  }
}
