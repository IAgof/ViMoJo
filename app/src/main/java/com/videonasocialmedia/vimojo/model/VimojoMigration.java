package com.videonasocialmedia.vimojo.model;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.UUID;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by jliarte on 24/10/16.
 */
public class VimojoMigration implements RealmMigration {
  @Override
  public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
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

    if(oldVersion == 5){
      RealmObjectSchema realmVideo = schema.get("RealmVideo");
      if(!realmVideo.hasField("videoError")){
        realmVideo.addField("videoError", String.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setString("videoError", null);
              }
            });
      }
      if(!realmVideo.hasField("isTranscodingTempFileFinished")){
        realmVideo.addField("isTranscodingTempFileFinished", boolean.class)
            .transform(new RealmObjectSchema.Function() {
          @Override
          public void apply(DynamicRealmObject obj) {
            obj.setBoolean("isTranscodingTempFileFinished", true);
          }
        });
      }
      if(realmVideo.hasField("isTempPathFinished")){
        realmVideo.removeField("isTempPathFinished");
      }
      oldVersion++;
    }

    // Migrate from version 6 to version 7, update RealmVideo, update RealmProject, add RealmTrack, add RealmMusic
    if (oldVersion == 6) {

      RealmObjectSchema trackSchema = schema.create("RealmTrack")
          .addField("uuid", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("id", Integer.class, FieldAttribute.REQUIRED)
          .addField("volume", Float.class, FieldAttribute.REQUIRED)
          .addField("mute", Boolean.class, FieldAttribute.REQUIRED)
          .addField("position", Integer.class, FieldAttribute.REQUIRED);

      RealmObjectSchema musicSchema = schema.create("RealmMusic")
          .addField("uuid", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("musicPath", String.class, FieldAttribute.REQUIRED)
          .addField("title", String.class, FieldAttribute.REQUIRED)
          .addField("author", String.class, FieldAttribute.REQUIRED)
          .addField("iconResourceId", Integer.class, FieldAttribute.REQUIRED)
          .addField("duration", Integer.class, FieldAttribute.REQUIRED)
          .addField("volume", Float.class, FieldAttribute.REQUIRED);

      RealmObjectSchema realmVideo = schema.get("RealmVideo");
      if (!realmVideo.hasField("volume")) {
        realmVideo.addField("volume", float.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setFloat("volume", 1f);
              }
            });
      }

      final RealmObjectSchema realmProject = schema.get("RealmProject");

      if(!realmProject.hasField("tracks")){
        realmProject.addRealmListField("tracks", trackSchema).transform(new RealmObjectSchema.Function() {
          @Override
          public void apply(DynamicRealmObject obj) {
            DynamicRealmObject mediaTrack = realm.createObject("RealmTrack");
            mediaTrack.setString("uuid", UUID.randomUUID().toString());
            mediaTrack.setInt("id", Constants.INDEX_MEDIA_TRACK);
            mediaTrack.setFloat("volume", 1f);
            mediaTrack.setBoolean("mute", false);
            mediaTrack.setInt("position", 0);

            DynamicRealmObject musicTrack = realm.createObject("RealmTrack");
            musicTrack.setString("uuid", UUID.randomUUID().toString());
            musicTrack.setInt("id", Constants.INDEX_AUDIO_TRACK_MUSIC);
            musicTrack.setFloat("volume", 0.5f);
            musicTrack.setBoolean("mute", false);
            musicTrack.setInt("position", 1);

            obj.getList("tracks").add(mediaTrack);
            obj.getList("tracks").add(musicTrack);
          }
        });
      }

      if(!realmProject.hasField("musics")){
        realmProject.addRealmListField("musics", musicSchema);
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
