package com.example.ninasmacpro.mavigation;

import java.util.TimerTask;

/**
 * Created by ninasmacpro on 16/3/27.
 */
public class MyTimerTask extends TimerTask {
    private MapFragment mMapFragment = null;


    MyTimerTask(MapFragment mapFragment) {
        mMapFragment = mapFragment;
    }

    @Override
    public void run() {
        mMapFragment.updateEverythingAboutGroup();
    }
}
