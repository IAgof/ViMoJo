package com.videonasocialmedia.vimojo.init.presentation.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.mixpanel.android.mpmetrics.InAppNotification;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.init.presentation.mvp.presenters.InitAppPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.vimojo.init.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.AppStart;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.videonasocialmedia.vimojo.sync.presentation.SyncAdapter.SYNC_INTERVAL;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_THEME_DARK_STATE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK_STATE;

/**
 * InitAppActivity.
 * <p/>
 * According to clean code and model, use InitAppView, InitAppPresenter for future use.
 * <p/>
 * Main Activity of the app, launch from manifest.
 * <p/>
 * First activity when the user open the app.
 * <p/>
 * Show a dummy splash screen and initialize all data needed to start
 */

public class InitAppActivity extends VimojoActivity implements InitAppView, OnInitAppEventListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private long MINIMUN_WAIT_TIME = 900;

    @Inject InitAppPresenter presenter;

    protected Handler handler = new Handler();
    @BindView(R.id.videona_version)
    TextView versionName;
    @BindView(R.id.init_root_view)
    ViewGroup initRootView;
    @BindView(R.id.splash_screen)
    ImageView splashScreen;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long startTime;
    private String androidId = null;
    private String initState;
    private CompositeMultiplePermissionsListener compositePermissionsListener;

    private static final long SYNC_FLEX_TIME =  SYNC_INTERVAL/3;
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver contentResolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getActivityPresentersComponent().inject(this);

        //remove title, mode fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_init_app);
        ButterKnife.bind(this);
        setVersionCode();
        createPermissionListeners();
        Dexter.continuePendingRequestsIfPossible(compositePermissionsListener);
        if (BuildConfig.FEATURE_SHOW_ADS) {
            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
            MobileAds.initialize(this, getString(R.string.admob_app_id));
        }
    }

    private boolean isBetaAppOutOfDate() {
        Calendar endOfBeta = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        // TODO:(alvaro.martinez) 8/11/16 get this date from flavor config
        String str= getResources().getString(R.string.app_out_of_date);
        Date dateBeta = null;
        try {
            dateBeta = new SimpleDateFormat("yyyy-MM-dd").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        endOfBeta.setTime(dateBeta);
        today.setTime(new Date());

        return today.after(endOfBeta);
    }

    private void showDialogOutOfDate() {
        android.support.v7.app.AlertDialog.Builder dialog = new
            android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(R.string.app_out_of_date_message);
        dialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private void requestPermissionsAndPerformSetup() {
        Dexter.checkPermissions(compositePermissionsListener, Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void startSplashThread() {
        SplashScreenTask splashScreenTask = new SplashScreenTask(this);
        splashScreenTask.execute();
    }

    private void createPermissionListeners() {
        SnackbarOnAnyDeniedMultiplePermissionsListener
                snackBarPermissionsDeniedListenerWithSettings =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                        .with(initRootView, R.string.permissions_denied_snackbar_message)
                        .withOpenSettingsButton(R.string.permissions_denied_go_to_settings_button)
                        .build();
        MultiplePermissionsListener corePermissionsListener = new CorePermissionListener(this);
        compositePermissionsListener =
                new CompositeMultiplePermissionsListener(corePermissionsListener,
                snackBarPermissionsDeniedListenerWithSettings);
    }

    private void setVersionCode() {
        String version = "v " + BuildConfig.VERSION_NAME;
        versionName.setText(version);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTime = System.currentTimeMillis();
        sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        requestPermissionsAndPerformSetup();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setup() {
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setupPathsApp(this);
        presenter.trackUserProfileGeneralTraits();
    }

    /**
     * Checks the paths of the app
     *
     * @param listener
     */
    private void setupPathsApp(OnInitAppEventListener listener) {
        try {
            initPaths();
            listener.onCheckPathsAppSuccess();
        } catch (IOException e) {
            Log.e("CHECK PATH", "error", e);
        } catch (CameraAccessException e) {
          e.printStackTrace();
          Log.e(LOG_TAG, " CameraAccessException " + e.getMessage());
        }
    }

    private void setupStartApp() throws CameraAccessException {
        AppStart appStart = new AppStart();
        switch (appStart.checkAppStart(this, sharedPreferences)) {
            case NORMAL:
                Log.d(LOG_TAG, " AppStart State NORMAL");
                initState = AnalyticsConstants.INIT_STATE_RETURNING;
                presenter.trackAppStartupProperties(false);
                break;
            case FIRST_TIME_VERSION:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME_VERSION");
                initState = AnalyticsConstants.INIT_STATE_UPGRADE;
                presenter.onAppUpgraded(this.androidId);
                break;
            case FIRST_TIME:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME");
                initState = AnalyticsConstants.INIT_STATE_FIRST_TIME;
                presenter.onFirstTimeRun(this.androidId);
                initThemeAndWatermark();
                break;
        }
    }

    /**
     * Check Videona app paths, PATH_APP, pathVideoTrim, pathVideoMusic, ...
     *
     * @throws IOException
     */
    private void initPaths() throws IOException {
        checkAndInitPath(Constants.PATH_APP);
        checkAndInitPath(Constants.PATH_APP_TEMP);
        checkAndInitPath(Constants.PATH_APP_PROJECTS);
        checkAndInitPath(Constants.PATH_APP_MASTERS);

        File privateDataFolderModel = getDir(Constants.FOLDER_VIDEONA_PRIVATE_MODEL,
                Context.MODE_PRIVATE);
        String privatePath = privateDataFolderModel.getAbsolutePath();
        editor.putString(ConfigPreferences.PRIVATE_PATH, privatePath).commit();
        Utils.copyWatermarkResourceToDevice();
    }

    private void initThemeAndWatermark() {
        editor.putBoolean(ConfigPreferences.THEME_APP_DARK, DEFAULT_THEME_DARK_STATE).commit();
        editor.putBoolean(ConfigPreferences.WATERMARK, DEFAULT_WATERMARK_STATE).commit();
    }

    /**
     * Checks the available cameras on the device (back/front), supported flash mode and the
     * supported resolutions
     */
    private void setupCameraSettings() {
       presenter.checkCamera2FrameRateAndResolutionSupported();
    }

    private void checkAndInitPath(String pathApp) {
        File fEdited = new File(pathApp);
        if (!fEdited.exists()) {
            fEdited.mkdirs();
        }
    }

    @Override
    public void onCheckPathsAppSuccess() throws CameraAccessException {
        // TODO(jliarte): 20/04/18 generic transition drawable to allow change in build phase?
        Drawable drawableFadeTransitionVideo = getDrawable(R.drawable.alpha_transition_white);
        presenter.onAppPathsCheckSuccess(Constants.PATH_APP, Constants.PATH_APP_ANDROID, drawableFadeTransitionVideo);
        setupStartApp();
    }

    @Override
    public void onCheckPathsAppError() {

    }

    @Override
    public void onLoadingProjectSuccess() {

    }

    @Override
    public void onLoadingProjectError() {

    }

    @Override
    public void navigate(Class cls) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        startActivity(intent);
    }

    private void exitSplashScreen() {
        InAppNotification notification = mixpanel.getPeople().getNotificationIfAvailable();
        if (notification != null) {
            Log.d("INAPP", "in-app notification received");
            mixpanel.getPeople().showGivenNotification(notification, this);
            mixpanel.getPeople().trackNotificationSeen(notification);
        }
        presenter.setNavigation();
    }

    @Override
    public void screenOrientationPortrait() {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void screenOrientationLandscape() {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * Shows the splash screen
     */
    class SplashScreenTask extends AsyncTask<Void, Void, Boolean> {

        private InitAppActivity initActivity;

        public SplashScreenTask(InitAppActivity activity) {
            this.initActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                waitForCriticalPermissions();
                initActivity.setupAndTrackInit();
            } catch (Exception e) {
                Log.e("SETUP", "setup failed", e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {
            long currentTimeEnd = System.currentTimeMillis();
            final long timePassed = currentTimeEnd - startTime;
            if (timePassed < MINIMUN_WAIT_TIME) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitSplashScreen();
                    }
                }, MINIMUN_WAIT_TIME - timePassed);
            } else {
                exitSplashScreen();
            }
        }

        private void waitForCriticalPermissions() {
            while (!areCriticalPermissionsGranted()) {
                //just wait
                //TODO reimplement using handlers and semaphores
            }
        }

        private boolean areCriticalPermissionsGranted() {
            boolean granted = ContextCompat.checkSelfPermission(InitAppActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(InitAppActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(InitAppActivity.this,
                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            return granted;
        }
    }

    private class CorePermissionListener implements MultiplePermissionsListener {
        private final InitAppActivity activity;

        public CorePermissionListener(InitAppActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
            if (report.areAllPermissionsGranted()) {
                if (isBetaAppOutOfDate() && !BuildConfig.DEBUG && BuildConfig.FEATURE_OUT_OF_DATE) {
                    showDialogOutOfDate();
                } else {
                    activity.startSplashThread();
                }
            }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                       PermissionToken token) {
            activity.showPermissionRationale(token);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle(R.string.permissionsDeniedTitle)
                .setMessage(R.string.permissionsDeniedMessage)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    private void setupAndTrackInit() throws CameraAccessException {
        setup();
        presenter.trackAppStartup(this.initState);
    }
}
