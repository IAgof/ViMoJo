package com.videonasocialmedia.vimojo.presentation.views.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditNavigatorView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicDetailActivity;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditNavigatorPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicListActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 *
 */
public class ToolbarNavigator extends LinearLayout implements EditNavigatorView {

    private ImageButton navigateToEditButton;
    private ImageButton navigateToMusicButton;
    private ImageButton navigateToShareButton;

    private Context context;
    private EditNavigatorPresenter navigatorPresenter;

    private ToolbarNavigator.ProjectModifiedCallBack callback;

    public ToolbarNavigator(Context context) {
        super(context);
        initComponents(context, null, 0);
    }

    private void initComponents(Context context, AttributeSet attrs, int defStyleAttr) {

        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.edit_activities_navigation_buttons, this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarNavigator, defStyleAttr, 0);
        boolean editSelected = a.getBoolean(R.styleable.ToolbarNavigator_edit_selected, false);
        boolean musicSelected = a.getBoolean(R.styleable.ToolbarNavigator_music_selected, false);
        boolean shareSelected = a.getBoolean(R.styleable.ToolbarNavigator_share_selected, false);
        int tintList = a.getResourceId(R.styleable.ToolbarNavigator_tint_color, R.color.button_color_toolbar);
        a.recycle();

        navigateToEditButton = (ImageButton) findViewById(R.id.button_edit_navigator);
        navigateToMusicButton = (ImageButton) findViewById(R.id.button_music_navigator);
        navigateToShareButton = (ImageButton) findViewById(R.id.button_share_navigator);

        tintButton(navigateToEditButton, tintList);
        tintButton(navigateToMusicButton, tintList);
        tintButton(navigateToShareButton, tintList);

        navigateToEditButton.setSelected(editSelected);
        navigateToMusicButton.setSelected(musicSelected);
        navigateToShareButton.setSelected(shareSelected);

        initListeners();
        disableNavigatorActions();
        if (!isInEditMode())
            navigatorPresenter = new EditNavigatorPresenter(this);
    }

    private void initListeners() {
        navigateToMusicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToMusicButton.isEnabled()) {
                   //navigatorPresenter.checkMusicAndNavigate();
                    navigateTo(SoundActivity.class);
                }
            }
        });
        navigateToEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToEditButton.isEnabled())
                    navigateTo(EditActivity.class);
            }
        });
        navigateToShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToShareButton.isEnabled()) {
                    Intent intent = new Intent(context, ExportProjectService.class);
                    Snackbar.make(v, "Starting export", Snackbar.LENGTH_INDEFINITE).show();
                    context.startService(intent);
                }
            }
        });
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponents(context, attrs, 0);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponents(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToolbarNavigator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initComponents(context, attrs, defStyleAttr);
    }

    public ProjectModifiedCallBack getCallback() {
        if (callback != null)
            return callback;
        else
            return new ToolbarNavigator.ProjectModifiedCallBack();
    }

    @Override
    public void enableNavigatorActions() {
        enableButton(navigateToMusicButton);
        enableButton(navigateToEditButton);
        enableButton(navigateToShareButton);
    }

    private void enableButton(ImageButton button) {
        if (button.isSelected())
            button.setEnabled(false);
        else
            button.setEnabled(true);
    }

    @Override
    public void disableNavigatorActions() {
        navigateToEditButton.setEnabled(false);
        navigateToMusicButton.setEnabled(false);
        navigateToShareButton.setEnabled(false);
    }

    @Override
    public void goToMusic(Music music) {
        if (music == null) {
            navigateTo(MusicListActivity.class);
        } else {
            Intent i = new Intent(context, MusicDetailActivity.class);
            i.putExtra(IntentConstants.MUSIC_DETAIL_SELECTED, music.getMediaPath());
            context.startActivity(i);
        }
    }

    public class ProjectModifiedCallBack {

        public void onProjectModified() {
            navigatorPresenter.areThereVideosInProject();
        }
    }
}
