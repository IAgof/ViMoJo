package com.videonasocialmedia.vimojo.asset.repository.datasource.mapper;

/**
 * Created by jliarte on 13/07/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;

/**
 * Class to provide model conversions between {@link Media} and {@link MediaDto}
 */
public class MediaToMediaDtoMapper extends KarumiMapper<Media, MediaDto> {
  private static final String LOG_TAG = MediaToMediaDtoMapper.class.getSimpleName();

  @Override
  public MediaDto map(Media media) {
    MediaDto mediaDto = new MediaDto();
    mediaDto.position = media.getPosition();
    mediaDto.mediaPath = media.getMediaPath();
    mediaDto.volume = media.getVolume();
    mediaDto.startTime = media.getStartTime();
    mediaDto.stopTime = media.getStopTime();

    if (media.getClass().equals(Video.class)) {
      mapVideoFields((Video) media, mediaDto);
    } else if (media.getClass().equals(Music.class)) {
      mapMusicFields((Music) media, mediaDto);
    } else {
      Log.e(LOG_TAG, "Unsupported type of media!");
    }
    return mediaDto;
  }

  private void mapVideoFields(Video video, MediaDto mediaDto) {
    mediaDto.mediaType = "video";
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
    mediaDto.mediaType = "music";
    mediaDto.id = music.getUuid();
    mediaDto.uuid = music.getUuid();
    // TODO(jliarte): 13/07/18 implement music specific mapping
  }

  @Override
  public Media reverseMap(MediaDto value) {
    return null;
  }
}
