package com.example.ninasmacpro.mavigation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;
/**
 * Created by ninasmacpro on 16/2/6.
 */
public class MavigationApplication extends Application {

    /**
     * Path to the map resources directory on the device
     */
    private String mapResourcesDirPath;

    /**
     * Absolute path to the file used for mapCreator - mapcreatorFile.json
     */
    private String mapCreatorFilePath;

    /**
     * Object for accessing application preferences
     */
    private ApplicationPreferences appPrefs;

    public void Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appPrefs = new ApplicationPreferences(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
    public void setMapResourcesDirPath(String mapResourcesDirPath) {
        this.mapResourcesDirPath = mapResourcesDirPath;
    }

    public String getMapResourcesDirPath() {
        return mapResourcesDirPath;
    }

    public String getMapCreatorFilePath() {
        return mapCreatorFilePath;
    }

    public void setMapCreatorFilePath(String mapCreatorFilePath) {
        this.mapCreatorFilePath = mapCreatorFilePath;
    }

    public ApplicationPreferences getAppPrefs() {
        return appPrefs;
    }

    public void setAppPrefs(ApplicationPreferences appPrefs) {
        this.appPrefs = appPrefs;
    }

}


