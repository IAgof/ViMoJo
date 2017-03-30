package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 19/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddVoiceOverToProjectUseCaseTest {
  @Mock private ProjectRepository mockedProjectRepository;
  @Mock private AddMusicToProjectUseCase mockedAddMusicToProjectUseCase;
  @InjectMocks AddVoiceOverToProjectUseCase injectedUseCase;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProject() {
    Project.INSTANCE.clear();
  }

  @Ignore // Ignore until know what to do if composition have music and voice over
  @Test
  public void setVoiceOverSetsMusicToComposition() {
    Project project = getAProject();
    assert ! project.getVMComposition().hasMusic();
    AddMusicToProjectUseCase addMusicToProjectUseCase =
            new AddMusicToProjectUseCase(mockedProjectRepository);
    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase =
        new RemoveMusicFromProjectUseCase(mockedProjectRepository);
    AddVoiceOverToProjectUseCase useCase = new AddVoiceOverToProjectUseCase(mockedProjectRepository,
            addMusicToProjectUseCase, removeMusicFromProjectUseCase);

    useCase.setVoiceOver(project, "voice/over/path", 0.7f);

    assertThat(project.getVMComposition().hasMusic(), is(true));
    assertThat(project.getVMComposition().getMusic().getMediaPath(), is("voice/over/path"));
    assertThat(project.getVMComposition().getMusic().getVolume(), is(0.7f));
  }

  @Test
  public void setVoiceOverAddAudioToComposition(){
    Project project = getAProject();
    assert ! project.getVMComposition().hasVoiceOver();
    AddMusicToProjectUseCase addMusicToProjectUseCase =
        new AddMusicToProjectUseCase(mockedProjectRepository);
    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase =
        new RemoveMusicFromProjectUseCase(mockedProjectRepository);

    AddVoiceOverToProjectUseCase useCase = new AddVoiceOverToProjectUseCase(mockedProjectRepository,
        addMusicToProjectUseCase, removeMusicFromProjectUseCase);

    useCase.setVoiceOver(project, "voice/over/path", 0.7f);

    assertThat(project.getVMComposition().hasVoiceOver(), is(true));
    assertThat(project.getVMComposition().getVoiceOver().getMediaPath(), is("voice/over/path"));
    assertThat(project.getVMComposition().getVoiceOver().getVolume(), is(0.7f));

  }

  @Test
  public void setVoiceOverCallsUpdateProject() {
    Project project = getAProject();

    injectedUseCase.setVoiceOver(project, "voice/over/path", 0.7f);

    verify(mockedProjectRepository).update(project);
  }

  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
            VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("project title", "root/path", profile);
  }
}