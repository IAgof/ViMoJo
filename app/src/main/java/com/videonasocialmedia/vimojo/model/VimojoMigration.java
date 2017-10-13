package com.videonasocialmedia.vimojo.model;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.sources.MusicSource;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.io.File;
import java.util.UUID;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static com.videonasocialmedia.vimojo.model.entities.editor.Project.INTERMEDIATE_FILES;

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
    if (oldVersion == 4) {
      RealmObjectSchema realmProject = schema.get("RealmProject");
      if (!realmProject.hasField("isWatermarkActivated")) {
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

    if (oldVersion == 5) {
      RealmObjectSchema realmVideo = schema.get("RealmVideo");
      if (!realmVideo.hasField("videoError")) {
        realmVideo.addField("videoError", String.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setString("videoError", null);
              }
            });
      }
      if (!realmVideo.hasField("isTranscodingTempFileFinished")) {
        realmVideo.addField("isTranscodingTempFileFinished", boolean.class)
            .transform(new RealmObjectSchema.Function() {
          @Override
          public void apply(DynamicRealmObject obj) {
            obj.setBoolean("isTranscodingTempFileFinished", true);
          }
        });
      }
      if (realmVideo.hasField("isTempPathFinished")) {
        realmVideo.removeField("isTempPathFinished");
      }
      oldVersion++;
    }

    // Migrate from version 6 to version 7, update RealmVideo, update RealmProject, add RealmTrack, add RealmMusic
    if (oldVersion == 6) {
      RealmObjectSchema trackSchema = schema.create("RealmTrack")
          .addField("uuid", String.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
          .addField("id", Integer.class, FieldAttribute.REQUIRED)
          .addField("volume", Float.class, FieldAttribute.REQUIRED)
          .addField("mute", Boolean.class, FieldAttribute.REQUIRED)
          .addField("position", Integer.class, FieldAttribute.REQUIRED);

      final RealmObjectSchema musicSchema = schema.create("RealmMusic")
          .addField("uuid", String.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
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

      realmProject.addRealmListField("tracks", trackSchema)
          .transform(new RealmObjectSchema.Function() {
        @Override
        public void apply(DynamicRealmObject obj) {
          DynamicRealmObject mediaTrack = realm.createObject("RealmTrack");
          mediaTrack.setString("uuid", UUID.randomUUID().toString());
          mediaTrack.setInt("id", Constants.INDEX_MEDIA_TRACK);
          mediaTrack.setFloat("volume", 1f);
          mediaTrack.setBoolean("mute", false);
          mediaTrack.setInt("position", 0);

          obj.getList("tracks").add(mediaTrack);

          final DynamicRealmObject musicTrack = realm.createObject("RealmTrack");
          musicTrack.setString("uuid", UUID.randomUUID().toString());
          musicTrack.setInt("id", Constants.INDEX_AUDIO_TRACK_MUSIC);
          musicTrack.setBoolean("mute", false);
          musicTrack.setFloat("volume", 0.5f);
          musicTrack.setInt("position", 1);

          realmProject.transform(new RealmObjectSchema.Function() {
            @Override
            public void apply(DynamicRealmObject obj) {
              String title = obj.getString("musicTitle");
              float volume = obj.getFloat("musicVolume");
              if (title != null) {
                if (title.compareTo(com.videonasocialmedia.vimojo.utils.Constants
                    .MUSIC_AUDIO_VOICEOVER_TITLE) == 0) {
                  DynamicRealmObject voiceOverTrack = realm.createObject("RealmTrack");
                  voiceOverTrack.setString("uuid", UUID.randomUUID().toString());
                  voiceOverTrack.setInt("id", Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
                  voiceOverTrack.setFloat("volume", volume);
                  voiceOverTrack.setBoolean("mute", false);
                  voiceOverTrack.setInt("position", 1);
                  musicTrack.setInt("position", 0);
                  obj.getList("tracks").add(musicTrack);
                  obj.getList("tracks").add(voiceOverTrack);
                } else {
                  musicTrack.setFloat("volume", volume);
                  obj.getList("tracks").add(musicTrack);
                }
              } else {
                obj.getList("tracks").add(musicTrack);
              }
            }
          });
        }
      });

      realmProject.addRealmListField("musics", musicSchema)
          .transform(new RealmObjectSchema.Function() {
            @Override
            public void apply(DynamicRealmObject obj) {
              String title = obj.getString("musicTitle");
              if(title != null) {
                DynamicRealmObject music = realm.createObject("RealmMusic");
                String projectPath = obj.getString("projectPath");
                float volume = obj.getFloat("musicVolume");
                String projectPathIntermediate = projectPath + File.separator + INTERMEDIATE_FILES;
                Music musicFromSource = new MusicSource(VimojoApplication.getAppContext())
                    .getMusicByTitle(projectPathIntermediate, title);
                music.setString("uuid", musicFromSource.getUuid());
                music.setString("musicPath", musicFromSource.getMediaPath());
                music.setString("title", musicFromSource.getMusicTitle());
                music.setString("author", musicFromSource.getAuthor());
                music.setInt("iconResourceId", musicFromSource.getIconResourceId());
                music.setInt("duration", musicFromSource.getDuration());
                music.setFloat("volume", volume);
                obj.getList("musics").add(music);
              }
            }
          });

      if (realmProject.hasField("musicTitle")) {
        realmProject.removeField("musicTitle");
      }
      if (realmProject.hasField("musicVolume")) {
        realmProject.removeField("musicVolume");
      }

      oldVersion++;
    }

    /**
     * // Version 8, new RealmVideoToAdapt table
     * public class RealmVideoToAdapt extends RealmObject {
         private int position;
         private String video_uuid;
         @PrimaryKey
         private String mediaPath;
         private int rotation;
         private String destVideoPath;
         private int numTriesAdaptingVideo = 0;
       }
     }

     */
    // Migrate from version 7 to version 8, new RealmVideoToAdapt
    if (oldVersion == 7) {
      RealmObjectSchema realmVideoToAdaptTable = schema.get("RealmVideoToAdapt");
      if (schema.get("RealmVideoToAdapt") == null) {
        RealmObjectSchema videoToAdaptSchema = schema.create("RealmVideoToAdapt")
                .addField("video_uuid", String.class, FieldAttribute.REQUIRED)
                .addField("position", Integer.class, FieldAttribute.REQUIRED)
                .addField("rotation", Integer.class, FieldAttribute.REQUIRED)
                .addField("mediaPath", String.class, FieldAttribute.PRIMARY_KEY,
                        FieldAttribute.REQUIRED)
                .addField("destVideoPath", String.class, FieldAttribute.REQUIRED)
                .addField("numTriesAdaptingVideo", Integer.class, FieldAttribute.REQUIRED);
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
