package com.example.ninasmacpro.mavigation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.skobbler.ngx.navigation.SKNavigationSettings;

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

    /**
     * object for the naviagation mode selection
     */
    private SKNavigationSettings.SKNavigationType navigationType= SKNavigationSettings.SKNavigationType.SIMULATION;

    private Address desAddress = null;

    public void setDesAddress(Address desAddress) {
        this.desAddress = desAddress;
    }

    public Address getDesAddress() {
        return desAddress;
    }

    public SKNavigationSettings.SKNavigationType getNavigationType() {
        return navigationType;
    }

    public void setNavigationType(SKNavigationSettings.SKNavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public void Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appPrefs = new ApplicationPreferences(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();
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


