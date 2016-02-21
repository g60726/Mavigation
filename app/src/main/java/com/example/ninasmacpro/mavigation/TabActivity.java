package com.example.ninasmacpro.mavigation;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class TabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static final int REQUEST_READ_PHONE_STATE = 0;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        requestPermissions();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    // stupid android 6.0!!!!!!
    private void requestPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(TabActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TabActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout,"Read Phone State permission is needed for the map to work",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(TabActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        REQUEST_READ_PHONE_STATE);
                            }
                        })
                        .show();

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(TabActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_READ_PHONE_STATE);

            }
        }

        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_READ_PHONE_STATE);
        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.INTERNET},
                REQUEST_READ_PHONE_STATE);
        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                REQUEST_READ_PHONE_STATE);
        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_READ_PHONE_STATE);
        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                REQUEST_READ_PHONE_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent aboutIntent = new Intent(this, SettingActivity.class);
            startActivity(aboutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class CustomViewPager extends ViewPager {

        private boolean enabled;

        public CustomViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
//            this.enabled = true;
        }

//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            if (this.enabled) {
//                return super.onTouchEvent(event);
//            }
//
//            return false;
//        }
//
//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent event) {
//            if (this.enabled) {
//                return super.onInterceptTouchEvent(event);
//            }
//
//            return false;
//        }
//
//        public void setPagingEnabled(boolean enabled) {
//            this.enabled = enabled;
//        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    // Top Rated fragment activity
                    return MapFragment.newInstance("p1", "p2");
                case 1:
                    // Games fragment activity
                    return FriendFragment.newInstance("p1", "p2");
                case 2:
                    // Movies fragment activity
                    return MessageFragment.newInstance("p1", "p2");
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Map";
                case 1:
                    return "Friends";
                case 2:
                    return "Message";
            }
            return null;
        }
    }
}
