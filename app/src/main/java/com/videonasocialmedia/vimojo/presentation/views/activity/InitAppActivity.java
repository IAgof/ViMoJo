package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.mixpanel.android.mpmetrics.InAppNotification;
import com.videonasocialmedia.camera.utils.Camera2Settings;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.InitAppPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.AppStart;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    @Bind(R.id.videona_version)
    TextView versionName;
    @Bind(R.id.init_root_view)
    ViewGroup initRootView;
    @Bind(R.id.splash_screen)
    ImageView splashScreen;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long startTime;
    private String androidId = null;
    private String initState;
    private CompositeMultiplePermissionsListener compositePermissionsListener;

    // Camera 1 deprecated, RecordActivity
    private Camera camera;
    private int numSupportedCameras;

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
        splashScreen.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
            R.drawable.splash_screen, 1280, 720));
        setVersionCode();
        createPermissionListeners();
        Dexter.continuePendingRequestsIfPossible(compositePermissionsListener);

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

    /**
     * Releases the camera object
     */
    private void releaseCamera() {
        if (camera != null) {
            //camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setup() throws CameraAccessException {
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setupStartApp();
        setupPathsApp(this);
        trackUserProfileGeneralTraits();
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
        }
    }

    private void setupStartApp() throws CameraAccessException {
        AppStart appStart = new AppStart();
        switch (appStart.checkAppStart(this, sharedPreferences)) {
            case NORMAL:
                Log.d(LOG_TAG, " AppStart State NORMAL");
                initState = AnalyticsConstants.INIT_STATE_RETURNING;
                trackAppStartupProperties(false);
                initSettings();
                break;
            case FIRST_TIME_VERSION:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME_VERSION");
                initState = AnalyticsConstants.INIT_STATE_UPGRADE;
                trackAppStartupProperties(false);
                // Repeat this method for security, if user delete app data miss this configs.
                setupCameraSettings();
                trackUserProfile();
                initSettings();
                break;
            case FIRST_TIME:
                Log.d(LOG_TAG, " AppStart State FIRST_TIME");
                initState = AnalyticsConstants.INIT_STATE_FIRST_TIME;
                trackAppStartupProperties(true);
                setupCameraSettings();
                trackUserProfile();
                trackCreatedSuperProperty();
                initSettings();
                initThemeAndWatermark();
                break;
            default:
                break;
        }
    }

    private void trackUserProfileGeneralTraits() {
        mixpanel.getPeople().increment(AnalyticsConstants.APP_USE_COUNT, 1);
        JSONObject userProfileProperties = new JSONObject();
        String userType = AnalyticsConstants.USER_TYPE_FREE;
        if (BuildConfig.FLAVOR.equals("alpha")) {
            userType = AnalyticsConstants.USER_TYPE_BETA;
        }
        try {
            userProfileProperties.put(AnalyticsConstants.TYPE, userType);
            userProfileProperties.put(AnalyticsConstants.LOCALE,
                    Locale.getDefault().toString());
            userProfileProperties.put(AnalyticsConstants.LANG, Locale.getDefault().getISO3Language());
            mixpanel.getPeople().set(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void trackAppStartupProperties(boolean state) {
        JSONObject appStartupSuperProperties = new JSONObject();
        int appUseCount;
        try {
            appUseCount = mixpanel.getSuperProperties().getInt(AnalyticsConstants.APP_USE_COUNT);
        } catch (JSONException e) {
            appUseCount = 0;
        }
        try {
            appStartupSuperProperties.put(AnalyticsConstants.APP_USE_COUNT, ++appUseCount);
            appStartupSuperProperties.put(AnalyticsConstants.FIRST_TIME, state);
            appStartupSuperProperties.put(AnalyticsConstants.APP, "ViMoJo");
            appStartupSuperProperties.put(AnalyticsConstants.FLAVOR, BuildConfig.FLAVOR);
            mixpanel.registerSuperProperties(appStartupSuperProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the camera id parameter in shared preferences to back camera
     */
    private void initSettings() {
        editor.putInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA).commit();
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
       presenter.checkCamera2ResolutionSupported();
       presenter.checkCamera2FrameRateSupported();
        // Camera 1 deprecated, RecordActivity
        // checkAvailableCameras();
      //  checkFlashMode();
      //  checkCameraVideoSize();
       // checkCameraFrameRate();
    }

    private void trackUserProfile() {
        mixpanel.identify(androidId);
        mixpanel.getPeople().identify(androidId);
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            mixpanel.getPeople().setOnce(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trackCreatedSuperProperty() {
        JSONObject createdSuperProperty = new JSONObject();
        try {
            createdSuperProperty.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            mixpanel.registerSuperPropertiesOnce(createdSuperProperty);
        } catch (JSONException e) {
            Log.e("ANALYTICS", "Error sending created super property");
        }
    }

    private void checkAndInitPath(String pathApp) {
        File fEdited = new File(pathApp);
        if (!fEdited.exists()) {
            fEdited.mkdirs();
        }
    }

    /**
     * Checks the available cameras on the device (back/front)
     */
    private void checkAvailableCameras() {
        if (camera != null) {
            releaseCamera();
        }
        camera = getCameraInstance(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID,
            ConfigPreferences.BACK_CAMERA));
        editor.putBoolean(ConfigPreferences.BACK_CAMERA_SUPPORTED, true).commit();

        numSupportedCameras = Camera.getNumberOfCameras();
        if (numSupportedCameras > 1) {
            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_SUPPORTED, true).commit();
        }
        releaseCamera();
    }

    /**
     * Checks if the device supports the flash mode
     */
    private void checkFlashMode() {
        if (camera != null) {
            releaseCamera();
        }
        if (numSupportedCameras > 1) {
            camera = getCameraInstance(ConfigPreferences.FRONT_CAMERA);
            if (camera.getParameters().getSupportedFlashModes() != null) {
                editor.putBoolean(ConfigPreferences.FRONT_CAMERA_FLASH_SUPPORTED, true).commit();
            } else {
                editor.putBoolean(ConfigPreferences.FRONT_CAMERA_FLASH_SUPPORTED, false).commit();
            }
            releaseCamera();
        }
        camera = getCameraInstance(ConfigPreferences.BACK_CAMERA);
        if (camera.getParameters().getSupportedFlashModes() != null) {
            editor.putBoolean(ConfigPreferences.BACK_CAMERA_FLASH_SUPPORTED, true).commit();
        } else {
            editor.putBoolean(ConfigPreferences.BACK_CAMERA_FLASH_SUPPORTED, false).commit();
        }
        releaseCamera();
    }

    /**
     * Checks the supported resolutions by the device
     */
    private void checkCameraVideoSize() {
        List<Camera.Size> supportedVideoSizes;
        if (camera != null) {
            releaseCamera();
        }
        if (numSupportedCameras > 1) {
            camera = getCameraInstance(ConfigPreferences.FRONT_CAMERA);
            supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
            boolean frontCameraResolutionSupported = false;
            if (supportedVideoSizes != null) {
                for (Camera.Size size : supportedVideoSizes) {
                    if (size.width == 1280 && size.height == 720) {
                        editor.putBoolean(ConfigPreferences.FRONT_CAMERA_720P_SUPPORTED, true)
                                .commit();
                        frontCameraResolutionSupported = true;
                        Log.d(LOG_TAG, "FRONT_CAMERA_720P_SUPPORTED");
                    }
                    if (size.width == 1920 && size.height == 1080) {
                        editor.putBoolean(ConfigPreferences.FRONT_CAMERA_1080P_SUPPORTED, true)
                                .commit();
                        frontCameraResolutionSupported = true;
                        Log.d(LOG_TAG, "FRONT_CAMERA_1080P_SUPPORTED");
                    }
                    if (size.width == 3840 && size.height == 2160) {
                        editor.putBoolean(ConfigPreferences.FRONT_CAMERA_2160P_SUPPORTED, true)
                                .commit();
                        frontCameraResolutionSupported = true;
                        Log.d(LOG_TAG, "FRONT_CAMERA_2160P_SUPPORTED");
                    }
                }
            } else {
                supportedVideoSizes = camera.getParameters().getSupportedPreviewSizes();
                if (supportedVideoSizes != null) {
                    for (Camera.Size size : supportedVideoSizes) {
                        if (size.width == 1280 && size.height == 720) {
                            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_720P_SUPPORTED, true)
                                    .commit();
                            frontCameraResolutionSupported = true;
                        }
                        if (size.width == 1920 && size.height == 1080) {
                            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_1080P_SUPPORTED, true)
                                    .commit();
                            frontCameraResolutionSupported = true;
                        }
                        if (size.width == 3840 && size.height == 2160) {
                            editor.putBoolean(ConfigPreferences.FRONT_CAMERA_2160P_SUPPORTED, true)
                                    .commit();
                            frontCameraResolutionSupported = true;
                        }
                    }
                } else {
                    editor.putBoolean(ConfigPreferences.FRONT_CAMERA_720P_SUPPORTED, false)
                            .commit();
                    editor.putBoolean(ConfigPreferences.FRONT_CAMERA_1080P_SUPPORTED, false)
                            .commit();
                    editor.putBoolean(ConfigPreferences.FRONT_CAMERA_2160P_SUPPORTED, false)
                            .commit();
                }
            }
            if (!frontCameraResolutionSupported) {
                editor.putBoolean(ConfigPreferences.FRONT_CAMERA_SUPPORTED, false).commit();
                Log.d(LOG_TAG, "FRONT_CAMERA_SUPPORTED");
            }
            releaseCamera();
        }
        camera = getCameraInstance(ConfigPreferences.BACK_CAMERA);
        supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
        if (supportedVideoSizes != null) {
            for (Camera.Size size : camera.getParameters().getSupportedVideoSizes()) {
                if (size.width == 1280 && size.height == 720) {
                    editor.putBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, true).commit();
                }
                if (size.width == 1920 && size.height == 1080) {
                    editor.putBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, true).commit();
                }
                if (size.width == 3840 && size.height == 2160) {
                    editor.putBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, true).commit();
                }
            }
        } else {
            supportedVideoSizes = camera.getParameters().getSupportedPreviewSizes();
            if (supportedVideoSizes != null) {
                for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes()) {
                    if (size.width == 1280 && size.height == 720) {
                        editor.putBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, true)
                                .commit();
                    }
                    if (size.width == 1920 && size.height == 1080) {
                        editor.putBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, true)
                                .commit();
                    }
                    if (size.width == 3840 && size.height == 2160) {
                        editor.putBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, true)
                                .commit();
                    }
                }
            } else {
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false).commit();
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false).commit();
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false).commit();
                editor.putBoolean(ConfigPreferences.BACK_CAMERA_SUPPORTED, false).commit();
            }
        }
        releaseCamera();
    }

    private void checkCameraFrameRate(){
        List<Integer> supportedFrameRates;
        if (camera != null) {
            releaseCamera();
        }
        camera = getCameraInstance(ConfigPreferences.BACK_CAMERA);
        supportedFrameRates = camera.getParameters().getSupportedPreviewFrameRates();
        if (supportedFrameRates != null) {
            editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_SUPPORTED, true).commit();
            for (int  frameRate : supportedFrameRates) {
                if(frameRate == 24) {
                    editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, true)
                            .commit();
                }
                if(frameRate == 25) {
                    editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, true)
                            .commit();
                }
                if(frameRate == 30) {
                    editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_30FPS_SUPPORTED, true)
                            .commit();
                }
            }
        } else {
            editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, false).commit();
            editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, false).commit();
            editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_30FPS_SUPPORTED, false).commit();
            editor.putBoolean(ConfigPreferences.CAMERA_FRAME_RATE_SUPPORTED, false).commit();
        }
        releaseCamera();
    }

    /**
     * Gets an instance of the camera object
     *
     * @param cameraId
     * @return
     */
    public Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            Log.e("DEBUG", "Camera did not open", e);
        }
        return c;
    }


    private void trackAppStartup() {
        JSONObject initAppProperties = new JSONObject();
        try {
            initAppProperties.put(AnalyticsConstants.TYPE, AnalyticsConstants.TYPE_ORGANIC);
            initAppProperties.put(AnalyticsConstants.INIT_STATE, initState);
            mixpanel.track(AnalyticsConstants.APP_STARTED, initAppProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckPathsAppSuccess() {
        presenter.startLoadingProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID,
            BuildConfig.FEATURE_WATERMARK);
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
        //navigate(RecordActivity.class);
        navigate(RecordCamera2Activity.class);
        if (notification != null) {
            Log.d("INAPP", "in-app notification received");
            mixpanel.getPeople().showGivenNotification(notification, this);
            mixpanel.getPeople().trackNotificationSeen(notification);
        }
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
                if(isBetaAppOutOfDate() && !BuildConfig.DEBUG) {
                    showDialogOutOfDate();
                } else {
                    activity.startSplashThread();
                }
            }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
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
        trackAppStartup();
    }
}
