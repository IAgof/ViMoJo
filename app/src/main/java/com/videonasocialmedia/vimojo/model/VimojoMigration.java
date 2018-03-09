package com.videonasocialmedia.vimojo.model;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;
import com.videonasocialmedia.vimojo.sources.MusicSource;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.io.File;
import java.util.UUID;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static com.videonasocialmedia.vimojo.model.entities.editor.Project.INTERMEDIATE_FILES;
import static com.videonasocialmedia.vimojo.utils.Constants.*;

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
                if (title.compareTo(
                    MUSIC_AUDIO_VOICEOVER_TITLE) == 0) {
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

    // Migrate from version 8 to 9, new RealmCameraSettings
    if (oldVersion == 8) {
        RealmObjectSchema realmCameraSettingsTable = schema.get("RealmCameraSettings");
        if(schema.get("RealmCameraSettings") == null) {
            RealmObjectSchema cameraSettingsSchema = schema.create("RealmCameraSettings")
                    .addField("cameraSettingsId", String.class, FieldAttribute.PRIMARY_KEY,
                            FieldAttribute.REQUIRED).transform(new RealmObjectSchema.Function() {
                  @Override
                  public void apply(DynamicRealmObject obj) {
                    obj.setString("cameraSettingsId", "RealmCameraSettings");
                  }
                })
                    .addField("interfaceSelected", String.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setString("interfaceSelected",
                                  DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED);
                        }
                      })
                    .addField("resolution", String.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setString("resolution", DEFAULT_CAMERA_SETTING_RESOLUTION);
                        }
                      })
                    .addField("quality", String.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setString("quality", DEFAULT_CAMERA_SETTING_QUALITY);
                        }
                      })
                    .addField("frameRate", String.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setString("frameRate", DEFAULT_CAMERA_SETTING_FRAME_RATE);
                        }
                      })
                    .addField("resolutionBack720pSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("resolutionBack720pSupported", true);
                        }
                      })
                    .addField("resolutionBack1080pSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("resolutionBack1080pSupported", false);
                        }
                      })
                    .addField("resolutionBack2160pSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("resolutionBack2160pSupported", false);
                        }
                      })
                    .addField("resolutionFront720pSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("resolutionFront720pSupported", false);
                        }
                      })
                    .addField("resolutionFront1080pSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("resolutionFront1080pSupported", false);
                        }
                      })
                    .addField("resolutionFront2160pSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("resolutionFront2160pSupported", false);
                        }
                      })
                    .addField("frameRate24FpsSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("frameRate24FpsSupported", false);
                        }
                      })
                    .addField("frameRate25FpsSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("frameRate25FpsSupported", false);
                        }
                      })
                    .addField("frameRate30FpsSupported", Boolean.class, FieldAttribute.REQUIRED)
                      .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                          obj.setBoolean("frameRate30FpsSupported", true);
                        }
                      });
        }
        oldVersion++;
    }

    // Migrate from version 9 to 10 Added new CameraIdSelected field to RealmCameraSettings
    if(oldVersion == 9) {
      RealmObjectSchema realmCameraSettingsTable = schema.get("RealmCameraSettings");
      if (!realmCameraSettingsTable.hasField("cameraIdSelected")) {
        realmCameraSettingsTable.addField("cameraIdSelected", int.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                int DEFAULT_CAMERA_ID_SELECTED = 0; // Back camera
                obj.setInt("cameraIdSelected", DEFAULT_CAMERA_ID_SELECTED);
              }
            });
      }
      if(realmCameraSettingsTable.hasField("resolutionBack1080pSupported")){
        realmCameraSettingsTable
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("resolutionBack1080pSupported", false);
              }
            });
      }

      oldVersion++;
    }

    // Migrate from version 9 to 10, 20180208. Added new Project fields, Project info, description, product types.
    if(oldVersion == 10) {
      RealmObjectSchema realmProject = schema.get("RealmProject");
      if(!realmProject.hasField("description")) {
        realmProject.addField("description", String.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setString("description", "");
              }
            });
      }
      if (!realmProject.hasField("directFalseTypeSelected")) {
        realmProject.addField("directFalseTypeSelected", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("directFalseTypeSelected", false);
              }
            });
      }
      if (!realmProject.hasField("rawVideoTypeSelected")) {
        realmProject.addField("rawVideoTypeSelected", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("rawVideoTypeSelected", false);
              }
            });
      }
      if (!realmProject.hasField("spoolTypeSelected")) {
        realmProject.addField("spoolTypeSelected", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("spoolTypeSelected", false);
              }
            });
      }
      if (!realmProject.hasField("totalTypeSelected")) {
        realmProject.addField("totalTypeSelected", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("totalTypeSelected", false);
              }
            });
      }
      if (!realmProject.hasField("graphicTypeSelected")) {
        realmProject.addField("graphicTypeSelected", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("graphicTypeSelected", false);
              }
            });
      }

      if (!realmProject.hasField("pieceTypeSelected")) {
        realmProject.addField("pieceTypeSelected", boolean.class)
            .transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                obj.setBoolean("pieceTypeSelected", false);
              }
            });
      }

      oldVersion++;
    }

    // Migrate from version 10 to 11, 20180308. Added product types as RealmList<String>, remove boolean product types supported
    if(oldVersion == 11) {
      final boolean[] directFalseTypeSelected = {false};
      final boolean[] rawVideoTypeSelected = {false};
      final boolean[] spoolTypeSelected = {false};
      final boolean[] totalTypeSelected = {false};
      final boolean[] graphicTypeSelected = {false};
      final boolean[] pieceTypeSelected = {false};
      RealmObjectSchema realmProject = schema.get("RealmProject");
      if (realmProject.hasField("directFalseTypeSelected")) {
        realmProject.transform(new RealmObjectSchema.Function() {
          @Override
          public void apply(DynamicRealmObject obj) {
            directFalseTypeSelected[0] = obj.getBoolean("directFalseTypeSelected");
          }
        });
        realmProject.removeField("directFalseTypeSelected");
      }
      if (realmProject.hasField("rawVideoTypeSelected")) {
        realmProject.transform(new RealmObjectSchema.Function() {
          @Override
          public void apply(DynamicRealmObject obj) {
            rawVideoTypeSelected[0] = obj.getBoolean("rawVideoTypeSelected");
          }
        });
        realmProject.removeField("rawVideoTypeSelected");
      }
      if (realmProject.hasField("spoolTypeSelected")) {
        realmProject.transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                spoolTypeSelected[0] = obj.getBoolean("spoolTypeSelected");
              }
            });
        realmProject.removeField("spoolTypeSelected");
      }
      if (realmProject.hasField("totalTypeSelected")) {
        realmProject.transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                totalTypeSelected[0] = obj.getBoolean("totalTypeSelected");
              }
            });
        realmProject.removeField("totalTypeSelected");
      }
      if (realmProject.hasField("graphicTypeSelected")) {
        realmProject.transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                graphicTypeSelected[0] = obj.getBoolean("graphicTypeSelected");
              }
            });
        realmProject.removeField("graphicTypeSelected");
      }
      if (realmProject.hasField("pieceTypeSelected")) {
        realmProject.transform(new RealmObjectSchema.Function() {
              @Override
              public void apply(DynamicRealmObject obj) {
                pieceTypeSelected[0] = obj.getBoolean("pieceTypeSelected");
              }
            });
        realmProject.removeField("pieceTypeSelected");
      }

      realmProject.addRealmListField("productTypeList", String.class).transform(new RealmObjectSchema.Function() {
        @Override
        public void apply(DynamicRealmObject obj) {
          RealmList<String> productTypeList = new RealmList<>();
          if (directFalseTypeSelected[0]) {
            productTypeList.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
          }
          if (rawVideoTypeSelected[0]) {
            productTypeList.add(ProductTypeProvider.Types.B_ROLL.name());
          }
          if (spoolTypeSelected[0]) {
            productTypeList.add(ProductTypeProvider.Types.NAT_VO.name());
          }
          if (totalTypeSelected[0]) {
            productTypeList.add(ProductTypeProvider.Types.INTERVIEW.name());
          }
          if (graphicTypeSelected[0]) {
            productTypeList.add(ProductTypeProvider.Types.GRAPHICS.name());
          }
          if (pieceTypeSelected[0]) {
            productTypeList.add(ProductTypeProvider.Types.PIECE.name());
          }
          obj.setList("productTypeList", productTypeList);
        }
      });

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
