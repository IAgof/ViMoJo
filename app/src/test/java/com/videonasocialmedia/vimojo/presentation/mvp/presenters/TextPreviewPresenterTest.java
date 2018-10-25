package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 6/09/16.
 */
public class TextPreviewPresenterTest {

    @Mock
    private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;
    private Project currentProject;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        getAProject();
    }

    // TODO: 19/3/18 Add testing in this presenter

    public void getAProject() {
        Profile compositionProfile = new Profile(null, null, null);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }
}
