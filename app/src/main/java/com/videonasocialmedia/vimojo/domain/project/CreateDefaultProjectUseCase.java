package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRealmRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {

  protected ProfileRepository profileRepository;
  protected ProjectRepository projectRepository;
  protected TrackRepository trackRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository, ProfileRepository
                                             profileRepository) {
    this.projectRepository = projectRepository;
    this.profileRepository = profileRepository;
  }

  public void loadOrCreateProject(String rootPath) {

    // By default project title,
    String projectTitle = DateUtils.getDateRightNow();
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
    }

    Project currentProject = Project.getInstance(projectTitle, rootPath, profileRepository.getCurrentProfile(),
        getProjectDefaultTrackList());
    projectRepository.update(currentProject);
  }

  public void createProject(String rootPath){
    String projectTitle = DateUtils.getDateRightNow();
    Project currentProject = new Project(projectTitle,rootPath,
        profileRepository.getCurrentProfile(), getProjectDefaultTrackList());
    Project.INSTANCE = currentProject;
    projectRepository.update(currentProject);
  }

  private List<Track> getProjectDefaultTrackList(){
    List<Track> trackList = new ArrayList<Track>();
    Track mediaTrack = new MediaTrack();
    Track musicTrack = new AudioTrack(Constants.INDEX_AUDIO_TRACK_MUSIC);
    Track voiceOverTrack = new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    trackList.add(mediaTrack);
    trackList.add(musicTrack);
    trackList.add(voiceOverTrack);
    return trackList;
  }

}
