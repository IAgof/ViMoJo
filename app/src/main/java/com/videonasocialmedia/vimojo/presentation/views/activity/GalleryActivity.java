package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.presentation.views.fragment.VideoGalleryFragment;
import com.videonasocialmedia.vimojo.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jca on 20/5/15.
 */
public class GalleryActivity extends VimojoActivity implements ViewPager.OnPageChangeListener,
        GalleryPagerView, OnSelectionModeListener {
    public final String TAG = getClass().getCanonicalName();
    public static final int REQUEST_CODE_IMPORT_VIDEO = 1;
    private final String MASTERS_FRAGMENT_TAG="MASTERS";
    private final String EDITED_FRAGMENT_TAG="EDITED";

    @Inject GalleryPagerPresenter galleryPagerPresenter;

    @BindView(R.id.button_ok_gallery)
    ImageButton okButton;
    @BindView(R.id.gallery_count_selected_videos)
    TextView videoCounter;
    @BindView(R.id.gallery_image_view_clips)
    ImageView galleryImageViewClips;
    @BindView(R.id.selection_mode)
    LinearLayout selectionMode;

    private MyPagerAdapter adapterViewPager;
    private int selectedPage = 0;
    private int countVideosSelected = 0;
    private AlertDialog dialog;
    private Dialog exportDialog;
    private AdView adView;
    private View exportDialogView;
    private TextView exportDialogMessage;
    private Uri videoImportedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorSecondary));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.colorSecondary));
        Log.d(TAG, "Creating Activity");
        setupViewPager(savedInstanceState);
        setupPagerTabStrip();
        Log.d(TAG, "....done!!");
        initImportProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        galleryPagerPresenter.updatePresenter();
        if (videoImportedUri != null) {
            galleryPagerPresenter.importVideo(Utils.getPath(this, videoImportedUri));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        countVideosSelected = getSelectedVideos().size();
    }

    private void setupPagerTabStrip() {
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorSecondary));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.colorSecondary));
    }

    private void setupViewPager(Bundle savedInstanceState) {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager(), savedInstanceState);
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOnPageChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, MASTERS_FRAGMENT_TAG, adapterViewPager.getItem(0));
        getFragmentManager().putFragment(outState, EDITED_FRAGMENT_TAG, adapterViewPager.getItem(1));
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
    protected void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        setupViewPager(inState);
        setupPagerTabStrip();
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

    @Override
    public void showAdsView() {
        adView = exportDialogView.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        exportDialog.setContentView(exportDialogView);
    }

    @Override
    public void hideAdsView() {
        CardView adsCardView = exportDialogView.findViewById(R.id.adsCardView);
        adsCardView.setVisibility(View.GONE);
        exportDialog.setContentView(exportDialogView);
    }

    @Override
    public void showImportingError(String message) {
        if (exportDialog.isShowing()) {
            exportDialog.dismiss();
        }
        Snackbar snackbar = Snackbar.make(videoCounter, message,
            Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void showImportingDialog() {
        runOnUiThread(() -> {
            exportDialog.show();
        });
    }

    @OnClick(R.id.button_ok_gallery)
    public void onClick() {
        List<Video> videoList;
        videoList = getSelectedVideos();
        if (videoList.size() > 0) {
            galleryPagerPresenter.addVideoListToProject(videoList);
        }
    }

    @OnClick(R.id.fab_gallery)
    public void onClickFabGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_IMPORT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Video is picked
        if (requestCode == REQUEST_CODE_IMPORT_VIDEO && resultCode == RESULT_OK
            && null != data) {
            videoImportedUri = data.getData();
            Log.e(TAG, "-----------------------import video------------------");
            Log.e(TAG, videoImportedUri.toString());
        }
    }

    private void initImportProgressDialog() {
        LayoutInflater dialogLayout = LayoutInflater.from(GalleryActivity.this);
        exportDialogView = dialogLayout.inflate(R.layout.dialog_progress_export, null);

        exportDialog = new Dialog(GalleryActivity.this, R.style.VideonaDialog);
        //exportDialog.setContentView(DialogView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(exportDialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
        exportDialog.getWindow().setAttributes(lp);

        exportDialogMessage = (TextView) exportDialogView.findViewById(R.id.exportDialogMessage);

        Button cancel = (Button) exportDialogView.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(v -> {
            exportDialog.dismiss();
        });
        exportDialog.setCancelable(false);
        exportDialog.setCanceledOnTouchOutside(false);

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

            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.VideonaDialog);

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
        if (selectionMode.getVisibility() != View.VISIBLE) {
            selectionMode.setVisibility(View.VISIBLE);
        }
        videoCounter.setText(Integer.toString(countVideosSelected));
        if (countVideosSelected == 0) {
            selectionMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void navigate() {
        if (exportDialog.isShowing()) {
            exportDialog.dismiss();
        }
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
            } else {
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


