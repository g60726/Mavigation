package com.example.ninasmacpro.mavigation;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;

import com.google.common.io.ByteStreams;
import com.skobbler.ngx.SKDeveloperKeyException;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitSettings;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.util.SKLogging;



public class Utils {

    /**
     * true if multiple map instances can be created
     */
    public static boolean isMultipleMapSupportEnabled;

    /**
     * Gets formatted time from a given number of seconds
     * @param timeInSec
     * @return
     */
    public static String formatTime(int timeInSec) {
        StringBuilder builder = new StringBuilder();
        int hours = timeInSec / 3600;
        int minutes = (timeInSec - hours * 3600) / 60;
        int seconds = timeInSec - hours * 3600 - minutes * 60;
        builder.insert(0, seconds + "s");
        if (minutes > 0 || hours > 0) {
            builder.insert(0, minutes + "m ");
        }
        if (hours > 0) {
            builder.insert(0, hours + "h ");
        }
        return builder.toString();
    }
    
    /**
     * Formats a given distance value (given in meters)
     * @param distInMeters
     * @return
     */
    public static String formatDistance(int distInMeters) {
        if (distInMeters < 1000) {
            return distInMeters + "m";
        } else {
            return ((float) distInMeters / 1000) + "km";
        }
    }
    
    /**
     * Copies files from assets to destination folder
     * @param assetManager
     * @param sourceFolder
     * @throws IOException
     */
    public static void copyAssetsToFolder(AssetManager assetManager, String sourceFolder, String destinationFolder)
            throws IOException {
        final String[] assets = assetManager.list(sourceFolder);
        
        final File destFolderFile = new File(destinationFolder);
        if (!destFolderFile.exists()) {
            destFolderFile.mkdirs();
        }
        copyAsset(assetManager, sourceFolder, destinationFolder, assets);
    }
    
    /**
     * Copies files from assets to destination folder
     * @param assetManager
     * @param sourceFolder
     * @param assetsNames
     * @throws IOException
     */
    public static void copyAsset(AssetManager assetManager, String sourceFolder, String destinationFolder,
            String... assetsNames) throws IOException {
        
        for (String assetName : assetsNames) {
            OutputStream destinationStream = new FileOutputStream(new File(destinationFolder + "/" + assetName));
            String[] files = assetManager.list(sourceFolder + "/" + assetName);
            if (files == null || files.length == 0) {
                
                InputStream asset = assetManager.open(sourceFolder + "/" + assetName);
                try {
                    ByteStreams.copy(asset, destinationStream);
                } finally {
                    asset.close();
                    destinationStream.close();
                }
            }
        }
    }
    
    /**
     * Tells if internet is currently available on the device
     * @param currentContext
     * @return
     */
    public static boolean isInternetAvailable(Context currentContext) {
        ConnectivityManager conectivityManager =
                (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnected()) {
                    return true;
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if the current device has a GPS module (hardware)
     * @return true if the current device has GPS
     */
    public static boolean hasGpsModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if the current device has a  NETWORK module (hardware)
     * @return true if the current device has NETWORK
     */
    public static boolean hasNetworkModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Initializes the SKMaps framework
     */
    public static boolean initializeLibrary(final Activity context) {
        SKLogging.enableLogs(true);

        // get object holding map initialization settings
        SKMapsInitSettings initMapSettings = new SKMapsInitSettings();

        final String  mapResourcesPath = ((MavigationApplication)context.getApplicationContext()).getAppPrefs().getStringPreference("mapResourcesPath");
        // set path to map resources and initial map style
        initMapSettings.setMapResourcesPaths(mapResourcesPath,
                new SKMapViewStyle(mapResourcesPath + "daystyle/", "daystyle.json"));

        final SKAdvisorSettings advisorSettings = initMapSettings.getAdvisorSettings();
        advisorSettings.setAdvisorConfigPath(mapResourcesPath +"/Advisor");
        advisorSettings.setResourcePath(mapResourcesPath +"/Advisor/Languages");
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorVoice("en");
        initMapSettings.setAdvisorSettings(advisorSettings);

        // EXAMPLE OF ADDING PREINSTALLED MAPS
//         initMapSettings.setPreinstalledMapsPath(((DemoApplication)context.getApplicationContext()).getMapResourcesDirPath()
//         + "/PreinstalledMaps");
        // initMapSettings.setConnectivityMode(SKMaps.CONNECTIVITY_MODE_OFFLINE);

        // Example of setting light maps
        // initMapSettings.setMapDetailLevel(SKMapsInitSettings.SK_MAP_DETAIL_LIGHT);
        // initialize map using the settings object

        try {
            SKMaps.getInstance().initializeSKMaps(context, initMapSettings);
            return true;
        }catch (SKDeveloperKeyException exception){
            exception.printStackTrace();
            showApiKeyErrorDialog(context);
            return false;
        }
    }


    /**
     * Shows the api key not set dialog.
     */
    public static void showApiKeyErrorDialog(Activity currentActivity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                currentActivity);

        alertDialog.setTitle("Error");
        alertDialog.setMessage("API_KEY not set");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(
                "Api Key Not Set",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

        alertDialog.show();
    }
    public static int getExactScreenOrientation(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        int rotation = defaultDisplay.getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        defaultDisplay.getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                   // Logging.writeLog(TAG, "Unknown screen orientation. Defaulting to " + "portrait.", Logging.LOG_DEBUG);
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * Deletes all files and directories from <>file</> except PreinstalledMaps
     * @param file
     */
    public static void deleteFileOrDirectory(File file){
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                if(new File(file,children[i]).isDirectory() &&!children[i].equals("PreinstalledMaps") &&!children[i].equals("Maps")){
                    deleteFileOrDirectory(new File(file,children[i]));
                }else{
                    new File(file,children[i]).delete();
                }
            }
        }
    }
}