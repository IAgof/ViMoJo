package com.videonasocialmedia.vimojo.asset.repository.datasource.mapper;

/**
 * Created by jliarte on 13/07/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;

/**
 * Class to provide model conversions between {@link Media} and {@link MediaDto}
 */
public class MediaToMediaDtoMapper extends KarumiMapper<Media, MediaDto> {
  private static final String LOG_TAG = MediaToMediaDtoMapper.class.getSimpleName();
  AssetToAssetDtoMapper assetMapper = new AssetToAssetDtoMapper();

  @Override
  public MediaDto map(Media media) {
    MediaDto mediaDto = new MediaDto();
    mediaDto.position = media.getPosition();
    mediaDto.mediaPath = media.getMediaPath();
    mediaDto.volume = media.getVolume();
    mediaDto.startTime = media.getStartTime();
    mediaDto.stopTime = media.getStopTime();
    mapMediaAsset(media, mediaDto);
    if (media.getClass().equals(Video.class)) {
      mapVideoFields((Video) media, mediaDto);
    } else if (media.getClass().equals(Music.class)) {
      mapMusicFields((Music) media, mediaDto);
    } else {
      Log.e(LOG_TAG, "Unsupported type of media!");
    }
    return mediaDto;
  }

  private void mapMediaAsset(Media media, MediaDto mediaDto) {
    // TODO(jliarte): 25/07/18 get project id
    mediaDto.asset = assetMapper.map(new Asset("confihack", media));
  }

  private void mapVideoFields(Video video, MediaDto mediaDto) {
    mediaDto.mediaType = MediaDto.MEDIA_TYPE_VIDEO;
    mediaDto.id = video.getUuid();
    mediaDto.uuid = video.getUuid();
    mediaDto.tempPath = video.getTempPath();
    mediaDto.clipText = video.getClipText();
    mediaDto.clipTextPosition = video.getClipTextPosition();
    mediaDto.hasText = video.hasText();
    mediaDto.trimmed = video.isTrimmedVideo();
    mediaDto.videoError = video.getVideoError();
    mediaDto.transcodeFinished = video.isTranscodingTempFileFinished();
  }

  private void mapMusicFields(Music music, MediaDto mediaDto) {
    mediaDto.mediaType = MediaDto.MEDIA_TYPE_MUSIC;
    mediaDto.id = music.getUuid();
    mediaDto.uuid = music.getUuid();
    // TODO(jliarte): 13/07/18 implement music specific mapping
  }

  @Override
  public Media reverseMap(MediaDto value) {
    Media media;
    switch (value.getMediaType()) {
      case MediaDto.MEDIA_TYPE_VIDEO:
        media = new Video(value.getMediaPath(), value.getVolume());
        media.setUuid(value.getUuid());
        ((Video) media).tempPath = value.getTempPath();
        media.setPosition(value.getPosition());
        ((Video) media).setClipText(value.getClipText());
        ((Video) media).setClipTextPosition(value.getClipTextPosition());
        ((Video) media).setTrimmedVideo(value.isTrimmed());
        media.setStartTime(value.getStartTime());
        media.setStopTime(value.getStopTime());
        ((Video) media).setVideoError(value.getVideoError());
        ((Video) media).setTranscodingTempFileFinished(value.isTranscodeFinished());
        return media;
      case MediaDto.MEDIA_TYPE_MUSIC:
        // TODO(jliarte): 23/07/18 map music items
//        media = new Music(value.getMediaPath(), value.volume, value.getDuration());
//        media.setUuid(value.getUuid());
//        music.setMusicTitle(value.title);
//        music.setMusicAuthor(value.author);
//        music.setIconResourceId(value.iconResourceId);
        return null;
      default:
        // TODO(jliarte): 23/07/18 will we have default case?
        media = new Media() {
          @Override
          public void setIdentifier(int identifier) {
            this.setUuid(String.valueOf(identifier));
          }

          @Override
          public void createIdentifier() {
            this.getUuid();
          }
        };
        media.setUuid(value.getUuid());
        return media;
    }
  }
}
