package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.ActivityPresentersComponent;
import com.videonasocialmedia.vimojo.main.DaggerActivityPresentersComponent;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 25/04/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class TimeLineVideoViewHolderTest {
  private EditActivity editActivity;
  @Mock private VideoTimeLineRecyclerViewClickListener mockedListener;
  @InjectMocks private VideoTimeLineAdapter injectedAdapter;
  private VideoTimeLineAdapter adapterSpy;
  @Mock VimojoTestApplication mockedVimojoTestApplication;
  private com.videonasocialmedia.vimojo.model.entities.editor.Project currentProject;
  @Mock VimojoActivity mockedVimojoActivity;

  public void setUpEditActivity() {
    editActivity = Robolectric.buildActivity(EditActivity.class).create().get();
  }

  @Before
  public void setUpTestDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    adapterSpy = spy(injectedAdapter);
    setUpEditActivity();
  }

  private ActivityPresentersComponent getActivityPresentersComponent() {
    return DaggerActivityPresentersComponent.builder()
        .activityPresentersModule(new ActivityPresentersModule(mockedVimojoActivity))
        .systemComponent(mockedVimojoTestApplication.getSystemComponent())
        .build();
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path",
        compositionProfile);
  }

  @Test
  public void testVideoViewHolderImplementsItemTouchHelperViewHolder() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    assertThat(timeLineVideoViewHolder, is(instanceOf(ItemTouchHelperViewHolder.class)));
  }

  @Test
  public void bindDataSetsOrderAndDurationTexts() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);

    timeLineVideoViewHolder.bindData(video, 0, 0);

    assertThat(timeLineVideoViewHolder.thumb.isSelected(), is(true));
    assertThat((String) timeLineVideoViewHolder.thumbOrder.getText(), is("1"));
    assertThat((String) timeLineVideoViewHolder.textDurationClip.getText(), is("00:00"));
  }

  @Test
  public void bindDataCallsDrawThumbnail() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();
    TimeLineVideoViewHolder holderSpy = spy(timeLineVideoViewHolder);
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);

    holderSpy.bindData(video, 0, 0);

    verify(holderSpy).drawVideoThumbnail(holderSpy.thumb, video);
  }

  // TODO(jliarte): 25/04/17 test draw thumbnail?

  @Test
  public void testEnableDeleteIcon() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    timeLineVideoViewHolder.enableDeleteIcon();

    assertThat(timeLineVideoViewHolder.removeVideo.getVisibility(), is(View.VISIBLE));
    assertThat(timeLineVideoViewHolder.removeVideo.isClickable(), is(true));
  }

  @Test
  public void testDisableDeleteButton() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();
    timeLineVideoViewHolder.enableDeleteIcon();

    timeLineVideoViewHolder.disableDeleteIcon();

    assertThat(timeLineVideoViewHolder.removeVideo.getVisibility(), is(View.GONE));
    assertThat(timeLineVideoViewHolder.removeVideo.isClickable(), is(false));
  }

  @Test
  public void clickItemSelectsItInAdapter() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    timeLineVideoViewHolder.onClipClick();

    verify(adapterSpy).updateSelection(timeLineVideoViewHolder.getAdapterPosition());
  }

  @Test
  public void clickItemEnablesDeleteIconJustInThisItem() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = Mockito.spy(getVideoViewHolder());

    timeLineVideoViewHolder.onClipClick();

    verify(timeLineVideoViewHolder).enableDeleteIcon();
    // (jliarte): 25/04/17 disable on others => made when finishing movement onItemClear
  }

  @Test
  public void clickItemNotifiesListener() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    timeLineVideoViewHolder.onClipClick();

    verify(mockedListener).onClipClicked(timeLineVideoViewHolder.getAdapterPosition());
  }

  @Test
  public void deleteItem() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    timeLineVideoViewHolder.onDeleteIconClick();

    verify(adapterSpy).remove(timeLineVideoViewHolder.getAdapterPosition());
  }

  @Test
  public void testOnItemClear() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    timeLineVideoViewHolder.onItemClear();

    verify(adapterSpy).updateSelection(timeLineVideoViewHolder.getAdapterPosition());
    verify(adapterSpy).notifyDataSetChanged();
  }

  @Test
  public void onDeleteIconClickCallsAdapter() {
    TimeLineVideoViewHolder timeLineVideoViewHolder = getVideoViewHolder();

    timeLineVideoViewHolder.onDeleteIconClick();

    verify(adapterSpy).remove(timeLineVideoViewHolder.getAdapterPosition());
  }

  private TimeLineVideoViewHolder getVideoViewHolder() {
    View viewRoot = editActivity.findViewById(android.R.id.content);
    View videoItem = LayoutInflater.from(editActivity)
            .inflate(R.layout.edit_videotimeline_video_item, (ViewGroup) viewRoot);
    return new TimeLineVideoViewHolder(adapterSpy, videoItem, mockedListener);
  }
}