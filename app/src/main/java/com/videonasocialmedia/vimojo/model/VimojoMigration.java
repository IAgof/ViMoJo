package com.videonasocialmedia.vimojo.model;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.repository.track.RealmTrack;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.UUID;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmList;
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
    // Migrate from version 2 to version 3
    if (oldVersion == 2) {
      RealmObjectSchema realmProject = schema.get("RealmProject");
      realmProject.addField("uuid", String.class)
              .transform(new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                  obj.setString("uuid", UUID.randomUUID().toString());
                }
              });
      updateRealmProjectPrimaryKeyToUuid(realmProject);
      realmProject.addField("lastModification", String.class)
          .transform(new RealmObjectSchema.Function() {
            @Override
            public void apply(DynamicRealmObject obj) {
              obj.setString("lastModification", DateUtils.getDateRightNow());
            }
          });
      realmProject.addField("duration", Integer.class)
          .transform(new RealmObjectSchema.Function() {
            @Override
            public void apply(DynamicRealmObject obj) {
              obj.setInt("duration", 0);
            }
          });
      realmProject.addField("pathLastVideoExported", String.class)
          .transform(new RealmObjectSchema.Function() {
            @Override
            public void apply(DynamicRealmObject obj) {
              obj.setString("pathLastVideoExported", "");
            }
          });
      realmProject.addField("dateLastVideoExported", String.class)
          .transform(new RealmObjectSchema.Function() {
            @Override
            public void apply(DynamicRealmObject obj) {
              obj.setString("dateLastVideoExported", DateUtils.getDateRightNow());
            }
          });
      oldVersion++;
    }

    // Migrate from version 3 to version 4,
    if (oldVersion == 3) {
      RealmObjectSchema realmProject = schema.get("RealmProject");
      if (!realmProject.hasField("isAudioFadeTransitionActivated")) {
        realmProject.addField("isAudioFadeTransitionActivated", boolean.class)
                .transform(new RealmObjectSchema.Function() {
                  @Override
                  public void apply(DynamicRealmObject obj) {
                    obj.setBoolean("isAudioFadeTransitionActivated", false);
                  }
                });
      }
      if (!realmProject.hasField("isVideoFadeTransitionActivated")) {
        realmProject.addField("isVideoFadeTransitionActivated", boolean.class)
                .transform(new RealmObjectSchema.Function() {
                  @Override
                  public void apply(DynamicRealmObject obj) {
                    obj.setBoolean("isVideoFadeTransitionActivated", false);
                  }
                });
      }
      oldVersion++;
    }

    //// Migrate from version 4 to version 5,
    if(oldVersion == 4){
      RealmObjectSchema realmProject = schema.get("RealmProject");
      if(!realmProject.hasField("isWatermarkActivated")){
        realmProject.addField("isWatermarkActivated", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("isWatermarkActivated", false);
              }
            });
      }
      oldVersion++;
    }

    // Migrate from version 5 to version 6, now update RealmVideo, not RealmProject
    if (oldVersion == 5) {
      RealmObjectSchema realmProject = schema.get("RealmVideo");
      if (!realmProject.hasField("volume")) {
        realmProject.addField("volume", float.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setFloat("volume", 1f);
              }
            });
      }

      oldVersion++;
    }

    //Migrate from verstion 6 to 7, added RealmTrack
    if(oldVersion == 6) {
      /*RealmObjectSchema trackSchema = schema.create("RealmTrack")
          .addField("uuid", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("id", Integer.class, FieldAttribute.REQUIRED)
          .addField("volume", Float.class, FieldAttribute.REQUIRED)
          .addField("mute", Boolean.class, FieldAttribute.REQUIRED)
          .addField("solo", Boolean.class, FieldAttribute.REQUIRED);*/
      //RealmObjectSchema trackSchema = schema.get("RealmTrack");

      RealmObjectSchema realmProject = schema.get("RealmProject");
      if(!realmProject.hasField("realmTrack")){
        realmProject.addRealmListField("realmTrack", schema.get("RealmTrack"));
      }
      if(!realmProject.hasField("realmMusic")){
        realmProject.addRealmListField("realmMusic", schema.get("RealmMusic"));
      }
      if(realmProject.hasField("musicTitle")){
        realmProject.removeField("musicTitle");
      }
      if(realmProject.hasField("musicVolume")){
        realmProject.removeField("musicVolume");
      }
      oldVersion++;
    }
  }

  private void updateRealmProjectPrimaryKeyToUuid(RealmObjectSchema realmProject) {
    if (!realmProject.getPrimaryKey().equals("uuid")) {
      realmProject.removePrimaryKey();
      realmProject.addPrimaryKey("uuid");
    }
  }
}
