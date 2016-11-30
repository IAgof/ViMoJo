package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.presentation.views.fragment.VideoGalleryFragment;
import com.videonasocialmedia.vimojo.presentation.views.listener.OnSelectionModeListener;

import com.videonasocialmedia.vimojo.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaDialogListener;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by jca on 20/5/15.
 */
public class GalleryActivity extends VimojoActivity implements ViewPager.OnPageChangeListener,
        GalleryPagerView, OnSelectionModeListener{

    private final String MASTERS_FRAGMENT_TAG="MASTERS";
    private final String EDITED_FRAGMENT_TAG="EDITED";
    private final int REQUEST_CODE_REMOVE_VIDEOS_FROM_GALLERY = 1;
    MyPagerAdapter adapterViewPager;
    int selectedPage = 0;
    GalleryPagerPresenter galleryPagerPresenter;
    @Bind(R.id.button_ok_gallery)
    ImageButton okButton;
    @Bind(R.id.gallery_count_selected_videos)
    TextView videoCounter;
    @Bind(R.id.gallery_image_view_clips)
    ImageView galleryImageViewClips;
    @Bind(R.id.selection_mode)
    LinearLayout selectionMode;
    private int countVideosSelected = 0;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        setupActivityButtons();

        Log.d("GALLERY ACTIVITY", "Creating Activity");

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager(), savedInstanceState);
        vpPager.setAdapter(adapterViewPager);

        vpPager.setOnPageChangeListener(this);
        galleryPagerPresenter = new GalleryPagerPresenter(this);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorWhite));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void setupActivityButtons() {
        tintGalleryButtons(R.color.button_color);
    }

    private void tintGalleryButtons(int tintList) {
        tintButton(okButton, tintList);
    }

    @Override
    public void onPause() {
        super.onPause();
        countVideosSelected = getSelectedVideos().size();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private List<Video> getSelectedVideos() {
        List<Video> result = new ArrayList<>();
        for (int i = 0; i < adapterViewPager.getCount(); i++) {
            VideoGalleryFragment selectedFragment = adapterViewPager.getItem(i);
            Log.d("GALLERY ACTIVITY", selectedFragment.toString());
            List<Video> videosFromFragment = selectedFragment.getSelectedVideoList();
            result.addAll(videosFromFragment);
        }
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, MASTERS_FRAGMENT_TAG, adapterViewPager.getItem(0));
        getFragmentManager().putFragment(outState, EDITED_FRAGMENT_TAG, adapterViewPager.getItem(1));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private List<Video> getSelectedVideosFromFragment(int selectedFragmentId) {
        VideoGalleryFragment selectedFragment = adapterViewPager.getItem(selectedFragmentId);
        return selectedFragment.getSelectedVideoList();

    }

    @OnClick(R.id.button_ok_gallery)
    public void onClick() {
        List<Video> videoList;
            videoList = getSelectedVideos();
            if (videoList.size() > 0)
                galleryPagerPresenter.loadVideoListToProject(videoList);
    }

    private List<Video> getSelectedVideosFromCurrentFragment() {
        VideoGalleryFragment selectedFragment = adapterViewPager.getItem(selectedPage);
        return selectedFragment.getSelectedVideoList();

    }

    private void shareVideo(Video selectedVideo) {
        String videoPath = selectedVideo.getMediaPath();
        Intent intent = new Intent(VimojoApplication.getAppContext(), ShareActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoPath);
        startActivity(intent);
    }

    @OnClick(R.id.button_cancel_gallery)
    public void goBack() {
        this.finish();
    }

    @OnClick(R.id.button_trash)
    public void deleteFiles() {
        final List<Video> videoList = getSelectedVideos();
        int numVideosSelected = videoList.size();
        if (numVideosSelected > 0) {
            String title;
            if(numVideosSelected == 1) {
                title = getResources().getString(R.string.confirmDeleteTitle) + " " +
                        String.valueOf(numVideosSelected) + " " +
                        getResources().getString(R.string.confirmDeleteTitle1);
            } else {
                title = getResources().getString(R.string.confirmDeleteTitle) + " " +
                        String.valueOf(numVideosSelected) + " " +
                        getResources().getString(R.string.confirmDeleteTitle2);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.VideonaAlertDialog);

            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            final List<Video> videoList = getSelectedVideos();
                            for (Video video : videoList) {
                                File file = new File(video.getMediaPath());
                                file.delete();
                            }
                            for (int i = 0; i < adapterViewPager.getCount(); i++) {
                                VideoGalleryFragment selectedFragment = adapterViewPager.getItem(i);
                                selectedFragment.updateView();
                            }
                            countVideosSelected = 0;
                            updateCounter();
                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            dialog = builder.setCancelable(false)
                .setTitle(title)
                .setMessage(getString(R.string.confirmDeleteMessage))
                .setPositiveButton(R.string.dialog_accept_delete_clip, dialogClickListener)
                .setNegativeButton(R.string.dialog_cancel_delete_clip, dialogClickListener).show();


        }
    }

    private void updateCounter() {
        if (selectionMode.getVisibility() != View.VISIBLE)
            selectionMode.setVisibility(View.VISIBLE);
            videoCounter.setText(Integer.toString(countVideosSelected));
            if (countVideosSelected == 0)
                selectionMode.setVisibility(View.GONE);
    }

    @Override
    public void navigate() {
            Intent intent;
            intent = new Intent(VimojoApplication.getAppContext(), EditActivity.class);
            startActivity(intent);
    }

    @Override
    public void showDialogVideosNotAddedFromGallery(ArrayList<Integer> listVideoId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.VideonaDialog);
        dialog.setTitle(R.string.error_video_format);

        String message = "Videos ";
        String videos = ".";
        int numColons = 0;
        for(int videoId: listVideoId){
            message = message.concat(String.valueOf(videoId));
            if(numColons<listVideoId.size()-1){
                message = message.concat(", ");
                numColons++;
            }
        }

        dialog.setMessage(message.concat(videos));

        dialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                navigate();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onNoItemSelected() {
        // todo: out of selection mode
    }

    @Override
    public void onItemChecked() {
        countVideosSelected++;
        updateCounter();
    }

    @Override
    public void onItemUnchecked() {
            countVideosSelected--;
            updateCounter();
    }

    @Override
    public void onExitSelection() {

    }

    @Override
    public void onConfirmSelection() {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private final int NUM_ITEMS = 2;

        private VideoGalleryFragment mastersFragment;
        private VideoGalleryFragment editedFragment;

        public MyPagerAdapter(FragmentManager fragmentManager, Bundle savedStateInstance) {
            super(fragmentManager);
            if (savedStateInstance==null) {
                createFragments();
            }else{
                restoreFragments(fragmentManager, savedStateInstance);
            }
        }

        private void createFragments(){
            int selectionMode = VideoGalleryFragment.SELECTION_MODE_MULTIPLE;

            mastersFragment = VideoGalleryFragment.newInstance
                    (VideoGalleryPresenter.MASTERS_FOLDER, selectionMode);
            editedFragment = VideoGalleryFragment.newInstance
                    (VideoGalleryPresenter.EDITED_FOLDER, selectionMode);
        }

        private void restoreFragments(FragmentManager fragmentManager, Bundle savedStateInstance) {
            mastersFragment=(VideoGalleryFragment)
                    fragmentManager.getFragment(savedStateInstance, MASTERS_FRAGMENT_TAG);
            editedFragment=(VideoGalleryFragment)
                    fragmentManager.getFragment(savedStateInstance, EDITED_FRAGMENT_TAG);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.mastersFolderTitle);
                case 1:
                    return getResources().getString(R.string.editedFolderTitle);
                default:
                    return getResources().getString(R.string.galleryActivityTitle);
            }
        }

        // Returns the fragment to display for that page
        @Override
        public VideoGalleryFragment getItem(int position) {
            VideoGalleryFragment result;
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    result = mastersFragment;
                    break;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    result = editedFragment;
                    break;
                default:
                    result = null;
            }
            return result;
        }
    }

}


