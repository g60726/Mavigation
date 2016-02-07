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

    public void Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }

}


