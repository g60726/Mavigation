package com.example.ninasmacpro.mavigation;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TabActivity extends AppCompatActivity {
    private boolean needToGetNotification = false;
    private String mGroupObjectId = null;
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

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int REQUEST_INTERNET = 2;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 3;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 4;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 5;
    private static final int REQUEST_ACCESS_WIFI_STATE = 6;

    private MapFragment mMapFragment = MapFragment.newInstance("p1", "p2");
    private MessageFragment mMessageFragment = MessageFragment.newInstance("p1", "p2");
    private FriendFragment mFriendFragment = FriendFragment.newInstance("p1", "p2");

    private View mLayout;


    public void onButtonGroup(View view) {
        mMapFragment.onButtonGroup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.i("debug ", "intent comes from notification:");
            String groupObjectId = extras.getString("groupObjectId");
            mGroupObjectId = groupObjectId;
            if(mMapFragment != null) {
                mMapFragment.notificationUpdateGroup(groupObjectId);
            } else {
                needToGetNotification = true;
            }
//            mMapFragment.hasGroup = true;
            //call new update group function to find group members,

        }

        //check activity
//        try {
//            Log.i("debug1", "check activity");
//            Intent intent = getIntent();
//            Bundle extras = intent.getExtras();
//            if (extras != null) {
//                Log.i("debug2", "extras is not null");
//                String jsonData = extras.getString("com.parse.Data");
//                JSONObject json;
//                json = new JSONObject(jsonData);
//                String pushStore = json.getString("alert");
////                data.setText(pushStore);
//                if(pushStore!=null) {
//                    Log.i("debug3", "start new activity");
//
//                    Intent pushIntent = new Intent(TabActivity.this, SearchFriendActivity.class);
////                    pushIntent.setClassName(TabActivity.this, "package.name.List");
//                    pushIntent.putExtra("store", pushStore);
//                    startActivity(pushIntent);
//                }
//            }
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }




        setContentView(R.layout.activity_tab);

        requestPermissions();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // hide fab in tabs other than map fragment
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                switch (position) {
                    case 0: // map fragment
                        fab.show();
                        break;

                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public MapFragment getMapFragment() {
        return mMapFragment;
    }

    public MessageFragment getMessageFragment() {
        return mMessageFragment;
    }

    // stupid android 6.0!!!!!!
    private void requestPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(TabActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(TabActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                Snackbar.make(mLayout,"Read Phone State permission is needed for the map to work",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(TabActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        REQUEST_READ_PHONE_STATE);
                            }
                        })
                        .show();

            } else {
                ActivityCompat.requestPermissions(TabActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_READ_PHONE_STATE);

            }
        }

        if (ContextCompat.checkSelfPermission(TabActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(TabActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(mLayout,"Read Phone State permission is needed for the map to work",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(TabActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .show();

            } else {
                ActivityCompat.requestPermissions(TabActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);

            }
        }

        if (ContextCompat.checkSelfPermission(TabActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(TabActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Snackbar.make(mLayout,"Read Phone State permission is needed for the map to work",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(TabActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .show();

            } else {
                ActivityCompat.requestPermissions(TabActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);

            }
        }

        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.INTERNET},
                REQUEST_INTERNET);
        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                REQUEST_ACCESS_NETWORK_STATE);
        ActivityCompat.requestPermissions(TabActivity.this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                REQUEST_ACCESS_WIFI_STATE);
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

                    if (needToGetNotification) {
                        mMapFragment.notificationUpdateGroup(mGroupObjectId);
                    }
                    return mMapFragment;
                case 1:
                    // Games fragment activity
                    return mFriendFragment;
                case 2:
                    // Movies fragment activity
                    return mMessageFragment;
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
