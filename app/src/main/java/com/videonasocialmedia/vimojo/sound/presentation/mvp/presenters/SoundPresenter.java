package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback,
    VideoTranscodingErrorNotifier {

  private SoundView soundView;
  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
  private ModifyTrackUseCase modifyTrackUseCase;
  private final Project currentProject;
  public VideoListErrorCheckerDelegate videoListErrorCheckerDelegate;
  public static final float VOLUME_MUTE = 0f;

   @Inject
    public SoundPresenter(SoundView soundView,
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
        GetMusicFromProjectUseCase getMusicFromProjectUseCase,
        GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
        ModifyTrackUseCase modifyTrackUseCase, VideoListErrorCheckerDelegate
                                 videoListErrorCheckerDelegate) {
        this.soundView = soundView;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getMusicFromProjectUseCase = getMusicFromProjectUseCase;
        this.currentProject = loadCurrentProject();
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.modifyTrackUseCase = modifyTrackUseCase;
        this.videoListErrorCheckerDelegate = videoListErrorCheckerDelegate;
    }

    public Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init() {
      checkVoiceOverFeatureToggle(BuildConfig.FEATURE_VOICE_OVER);
      // TODO:(alvaro.martinez) 22/03/17 Player should be in charge of these checks from VMComposition 
      checkAVTransitionsActivated();
      retrieveTracks();
    }

  private void retrieveTracks() {
    if(currentProject.getVMComposition().hasVideos()){
      Track videoTrack = currentProject.getVMComposition().getMediaTrack();
      setupTrack(videoTrack);
      obtainVideos();
      soundView.showTrackVideo();
    }
    if(currentProject.getVMComposition().hasMusic()){
      Track musicTrack = currentProject.getVMComposition().getAudioTracks()
          .get(Constants.INDEX_AUDIO_TRACK_MUSIC);
      setupTrack(musicTrack);
      obtainMusic();
      if(musicTrack.getPosition()==1){
        soundView.showTrackAudioFirst();
      } else {
        soundView.showTrackAudioSecond();
      }
    }
    if(currentProject.getVMComposition().hasVoiceOver()){
      Track voiceOverTrack = currentProject.getVMComposition().getAudioTracks()
          .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
      setupTrack(voiceOverTrack);
      obtainVoiceOver();
      if(voiceOverTrack.getPosition()==1){
        soundView.showTrackAudioFirst();
      } else {
        soundView.showTrackAudioSecond();
      }
    }
  }

  private void obtainVideos() {
    getMediaListFromProjectUseCase.getMediaListFromProject(this);
  }

  private void obtainVoiceOver() {
    getMusicFromProjectUseCase.getVoiceOverFromProject(this);
  }

  private void obtainMusic() {
    getMusicFromProjectUseCase.getMusicFromProject(this);
  }

  private void setupTrack(Track track) {
    soundView.bindTrack(track);
    updatePlayerMute(track.getId(), track.isMute());
  }

  private void checkAVTransitionsActivated() {
    if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
      soundView.setVideoFadeTransitionAmongVideos();
    }
    if(getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
        !currentProject.getVMComposition().hasMusic()){
      soundView.setAudioFadeTransitionAmongVideos();
    }
  }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        soundView.bindVideoList(videoList);
        videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(videoList, this);
    }

  @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
        soundView.resetPreview();
    }

    @Override
    public void onMusicRetrieved(Music music) {
        // TODO:(alvaro.martinez) 7/03/17 Get from project use case list<Music> instead music
        List<Music> musicList = new ArrayList<>();
        musicList.add(music);
        if (isMusicAVoiceOver(music)) {
          soundView.bindVoiceOverList(musicList);
        } else {
          soundView.bindMusicList(musicList);
        }
    }

  protected void checkVoiceOverFeatureToggle(boolean featureVoiceOver) {
    if(featureVoiceOver){
      soundView.addVoiceOverOptionToFab();
    } else {
      soundView.hideVoiceOverCardView();
    }
  }

  private boolean isMusicAVoiceOver(Music music) {
    return music.getMusicTitle()
        .compareTo(com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE) == 0;
  }

  public void setTrackVolume(int id, int seekBarProgress){
    Track track = getTrackById(id);
    float volume = (float) (seekBarProgress * 0.01);
    modifyTrackUseCase.setTrackVolume(track, volume);
    updatePlayerVolume(id, volume);
  }

  private void updatePlayerVolume(int id, float volume) {
    switch (id) {
      case Constants.INDEX_MEDIA_TRACK:
        soundView.setVideoVolume(volume);
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        soundView.setMusicVolume(volume);
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        soundView.setVoiceOverVolume(volume);
        break;
    }
  }

  private Track getTrackById(int id) {
    switch (id){
      case Constants.INDEX_MEDIA_TRACK:
        return currentProject.getVMComposition().getMediaTrack();
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        return currentProject.getVMComposition().getAudioTracks().get(id);
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        return currentProject.getVMComposition().getAudioTracks().get(id);
      default:
        return null;
    }
  }

  public void setTrackMute(int id, boolean isMute){
    Track track = getTrackById(id);
    modifyTrackUseCase.setTrackMute(track, isMute);
    updatePlayerMute(id, isMute);
  }

  private void updatePlayerMute(int id, boolean isMute) {
    switch (id) {
      case Constants.INDEX_MEDIA_TRACK:
        if(isMute){
          soundView.setVideoVolume(VOLUME_MUTE);
        } else {
          soundView.setVideoVolume(getTrackById(id).getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        if(isMute){
          soundView.setMusicVolume(VOLUME_MUTE);
        } else {
          soundView.setMusicVolume(getTrackById(id).getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if(isMute){
          soundView.setVoiceOverVolume(VOLUME_MUTE);
        } else {
          soundView.setVoiceOverVolume(getTrackById(id).getVolume());
        }
        break;
    }
  }

  @Override
  public void showWarningTempFile() {
    soundView.showWarningTempFile();
  }

  @Override
  public void setWarningMessageTempFile(String messageTempFile) {
    soundView.setWarningMessageTempFile(messageTempFile);
  }
}
