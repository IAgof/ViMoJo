package com.videonasocialmedia.vimojo.model.entities.editor.media;

import android.os.Environment;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Created by alvaro on 29/09/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class})
public class VideoDuplicateTest {

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        mockStatic(Environment.class);
        when(Environment.getExternalStorageState()).thenReturn("mounted");
        setInternalState(Environment.class, "DIRECTORY_DCIM", "DCIM");

        when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
                .thenReturn(new File("DCIM/Vimojo/.temp"));

    }

    @Test
    public void shouldSaveStateIfVideoIsDuplicatedAfterTrimming(){

        Video fakeVideo = getFakeVideoTrimmed();

        Video copyVideo = new Video(fakeVideo);

        assertThat("copy save start time", copyVideo.getStartTime(), CoreMatchers.is(fakeVideo.getStartTime()));
        assertThat("copy save stop time", copyVideo.getStopTime(), CoreMatchers.is(fakeVideo.getStopTime()));
        assertThat("copy save duration time", copyVideo.getDuration(), CoreMatchers.is(fakeVideo.getDuration()));
        assertThat("copy save fileDuration time", copyVideo.getFileDuration(), CoreMatchers.is(fakeVideo.getFileDuration()));
        assertThat("copy save is video trimmed", copyVideo.isTrimmedVideo(), CoreMatchers.is(fakeVideo.isTrimmedVideo()));
        assertThat("copy save is temp path finished", copyVideo.outputVideoIsFinished(), CoreMatchers.is(fakeVideo.outputVideoIsFinished()));
        assertThat("copy save is tempPath, video edited", copyVideo.isEdited(), CoreMatchers.is(fakeVideo.isEdited()));
    }

    public void shouldSaveStateIfVideoIsDuplicatedAfterText(){

        Video fakeVideo = getFakeVideoTextAdded();

        Video copyVideo = new Video(fakeVideo);

        assertThat("copy save text added", copyVideo.getClipText(), CoreMatchers.is(fakeVideo.getClipText()));
        assertThat("copy save position selected", copyVideo.getClipTextPosition(), CoreMatchers.is(fakeVideo.getClipTextPosition()));
        assertThat("copy save is video text added", copyVideo.hasText(), CoreMatchers.is(fakeVideo.hasText()));
        assertThat("copy save is temp path finished", copyVideo.outputVideoIsFinished(), CoreMatchers.is(fakeVideo.outputVideoIsFinished()));
        assertThat("copy save is tempPath, video edited", copyVideo.isEdited(), CoreMatchers.is(fakeVideo.isEdited()));
    }


    private Video getFakeVideoTrimmed() {

        Video video = new Video("somePath");
        // simulate operation, trimm video
        video.setTempPath();
        video.setStartTime(0);
        video.setStopTime(10);
        video.setDuration(10);
        video.setTrimmedVideo(true);
        video.setTempPathFinished(true);

        return video;

    }

    private Video getFakeVideoTextAdded() {

        Video video = new Video("somePath");
        // simulate operation, add text to video
        video.setTempPath();
        video.setClipText("blablabla");
        video.setClipTextPosition("CENTER");
        video.setTextToVideoAdded(true);
        video.setTempPathFinished(true);

        return video;

    }
}
