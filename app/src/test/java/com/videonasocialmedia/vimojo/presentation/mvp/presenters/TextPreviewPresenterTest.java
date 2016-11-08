package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by alvaro on 6/09/16.
 */
public class TextPreviewPresenterTest {

    @Mock
    private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(null, null, null));
    }
}
