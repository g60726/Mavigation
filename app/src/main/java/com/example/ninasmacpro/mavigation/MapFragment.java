package com.example.ninasmacpro.mavigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCalloutView;
import com.skobbler.ngx.map.SKCircle;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements SKMapSurfaceListener, SKCurrentPositionListener,SKRouteListener,SKNavigationListener
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_CODE_CREATE_GROUP = 1;
    private static final int REQUEST_CODE_ADD_PEOPLE_TO_GROUP = 2;

    private SKMapSurfaceView mapView; // Surface view for displaying the map


    private ParseUser mParseUser = null;
    private ParseObject mUserInfo = null; // user's associated UserInfo object

    private boolean hasGroup = false;
    private ParseObject mGroupOnParse = null;
    private String mGroupName = null;
    private String mGroupObjectId = null;
    private ArrayList<String> mGroupMemberObjectId = null;

    private TabActivity mTabActivity = null;
    Timer mTimer;
    MyTimerTask mTimerTask;
    private boolean mIsLeader = true; // true so we can search

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * the view that holds the map view
     */
    private SKMapViewHolder mapHolder;

    private SKCurrentPositionProvider currentPositionProvider;
    /**
     * Current position
     */
    private SKPosition currentPosition;

    /**
     * timestamp for the last currentPosition
     */
    private long currentPositionTime;
    /*
    check if start already by centering
     */
    private boolean start = true;
    /*
    destination coordinate creating by single tap
     */
    private SKCoordinate desCoordinate = null;
    /*
    navigation button
     */
    private ImageButton navigateButton;
    /*
    text to speech engine
     */
    private TextToSpeech textToSpeechEngine;
    /*
    text to speech map config
     */
    private enum MapAdvices {
        TEXT_TO_SPEECH, AUDIO_FILES
    }
    /*
    bool indicate navigation is in progress
     */
    private boolean navInProg = false;
    /*
    search button
     */
    private Button searchButton;
    // geocoder
    private Geocoder geocoder;
    /** the "+" button on map fragment */
    // TODO: add this to ParseCurrentUser
    public void onButtonGroup() {
        // if user doesn't have a group yet, jump to another activity to create a group and add people
        if (!hasGroup) {
            Intent intent = new Intent(getActivity(), GroupActivity.class);
            intent.putExtra("hasGroup", hasGroup);
            startActivityForResult(intent, REQUEST_CODE_CREATE_GROUP);
        } else { // if the user has a group, jump to another activity to add more people (from friends) to the group
            Intent intent = new Intent(getActivity(), GroupActivity.class); //TODO: change this to a diff class?
            intent.putExtra("hasGroup", hasGroup);
            intent.putExtra("groupName", mGroupName);
            intent.putStringArrayListExtra("currentGroupMemberObjectId", mGroupMemberObjectId);
            startActivityForResult(intent, REQUEST_CODE_ADD_PEOPLE_TO_GROUP);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CREATE_GROUP) {
            if (Activity.RESULT_OK == resultCode) {
                mIsLeader = true;
                mParseUser.put("isLeader", mIsLeader);
                mParseUser.saveInBackground();

                hasGroup = true;
                Bundle bundle = data.getExtras();
                mGroupName = bundle.getString("groupName");
                ArrayList<String> groupMemberObjectId = bundle.getStringArrayList("groupMemberObjectId");

                groupMemberObjectId.add(mParseUser.getObjectId()); // add currentUser to group
                Log.w("groupmemberobjectid", groupMemberObjectId.toString());
                mGroupMemberObjectId = new ArrayList<String>(groupMemberObjectId);
                Log.w("groupmember_hashset", mGroupMemberObjectId.toString());
                mGroupOnParse = new ParseObject("Group");
                updateGroup(groupMemberObjectId); // to ensure uniqueness of each member

            } else {
                // not used now
            }
        } else if (requestCode == REQUEST_CODE_ADD_PEOPLE_TO_GROUP) {
            if (Activity.RESULT_OK == resultCode) {
                Bundle bundle = data.getExtras();
                boolean leaveGroup = bundle.getBoolean("leaveGroup");

                if (leaveGroup) {
                    leaveCurrentGroup();
                } else {
                    mGroupName = bundle.getString("groupName"); // user may have changed group name
                    ArrayList<String> newGroupMemberObjectId = bundle.getStringArrayList("groupMemberObjectId");
                    addToCurrentGroup(newGroupMemberObjectId);
                }
            } else {
                // now used now
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // As long as you have mGroupObjectId, just call this function then it will grab
    // everything you need for the group, as well as keep updating group information
    // This function is being called by MyTimerTask
    public void updateEverythingAboutGroup() {
        // get Group object
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mGroupOnParse = object;

                    // get destination
                    Double latitude = mGroupOnParse.getDouble("destLatitude");
                    Double longitude = mGroupOnParse.getDouble("destLongitude");
                    if (latitude != null && longitude != null) {
                        if (navInProg && (desCoordinate.getLatitude() != latitude || desCoordinate.getLongitude() != longitude)) {
                            stopNavigtion();
                        }
                        desCoordinate = new SKCoordinate(longitude, latitude);
                        updateDestination();
                        addCircle();
                    }

                    mGroupMemberObjectId = new ArrayList<String>();
                    mGroupName = mGroupOnParse.getString("groupName");
                    ParseRelation<ParseUser> temp = mGroupOnParse.getRelation("members");
                    try {
                        List<ParseUser> groupMember = temp.getQuery().find();
                        for (ParseUser temp2: groupMember) {
                            Log.w("getGroupObject", temp2.getObjectId());
                            mGroupMemberObjectId.add(temp2.getObjectId());
                        }
                        showGroupMembersLocation();
                    } catch (ParseException e2) {

                    }
                } else {
                    // something went wrong
                }
            }
        });
    }

    private void addToCurrentGroup(final ArrayList<String> newGroupMemberObjectId) {
        mGroupMemberObjectId.addAll(newGroupMemberObjectId);

        // get Group object
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mGroupOnParse = object;
                    // update Group object
                    updateGroup(newGroupMemberObjectId);
                } else {
                    // something went wrong
                }
            }
        });

    }


    private void updateGroup(final ArrayList<String> newGroupMember) {
        mGroupOnParse.put("groupName", mGroupName);
        final ParseRelation<ParseUser> relation = mGroupOnParse.getRelation("members");
        String [] temp = newGroupMember.toArray(new String[newGroupMember.size()]);
        // query all group members (a list of ParseUsers) from Parse
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", Arrays.asList(temp));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    for (ParseUser user : users) {
                        relation.add(user);
                    }
                    mGroupOnParse.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            mGroupObjectId = mGroupOnParse.getObjectId();
                            //send request
                            sendGroupRequest(mGroupObjectId, newGroupMember);
                            mParseUser.put("groupObjectId", mGroupObjectId);
                            mParseUser.saveInBackground();
                            showGroupMembersLocation();

                            // schedule a timer to update group information
                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer = null;
                            }
                            mTimer = new Timer();
                            mTimerTask = new MyTimerTask(mTabActivity.getMapFragment());
                            mTimer.schedule(mTimerTask, 1000, 3000); //delay 1000ms, repeat in 3000ms
                        }
                    });
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    // send notification to all new group members
    private void sendGroupRequest(String groupObjectId, ArrayList<String> newGroupMember){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        for (String contactObjectId: newGroupMember){
            if (!contactObjectId.equals(currentUserId) ){
                pushQuery.whereEqualTo("user", contactObjectId);
                String message = groupObjectId + " invites  you";
                try{
                    JSONObject data = new JSONObject();
                    data.put("alert", message);
                    data.put("action", "group");
                    data.put("groupObjectId", groupObjectId);
                    ParsePush push = new ParsePush();
                    push.setQuery(pushQuery); // Set our Installation query
                    push.setData(data);
                    push.sendInBackground();
                }catch (Exception e){
                    Log.i("debug3", "jason data type went wrong");
                }
            }

        }
    }

    public String getGroupObjectId() {
        return mGroupObjectId;
    }

    // Receiver of push notification from Parse.
    public void notificationUpdateGroup(String groupObjectId) {
        hasGroup = true;
        mGroupObjectId = groupObjectId;



        if (mParseUser == null) {
            mParseUser = ParseUser.getCurrentUser();
        }

        mUserInfo = mParseUser.getParseObject("userInfo");
        mIsLeader = false;
        mParseUser.put("isLeader", mIsLeader);
        mParseUser.put("groupObjectId", mGroupObjectId);
        mParseUser.saveInBackground();

        updateEverythingAboutGroup();

        // start fetching group chat
        mTabActivity.getMessageFragment().setTabActivity(mTabActivity);
        mTabActivity.getMessageFragment().startRetrievingGroupMessages(mGroupObjectId);

        // schedule a timer to update group information
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        mTimerTask = new MyTimerTask(mTabActivity.getMapFragment());
        mTimer.schedule(mTimerTask, 1000, 3000); //delay 1000ms, repeat in 3000ms

        
    }

    private void showGroupMembersLocation() {
        if (mapView != null) {
            mapView.deleteAllAnnotationsAndCustomPOIs();
        }

        mapPopup.hide();

        // update group member's location on map
        ParseRelation<ParseUser> temp = mGroupOnParse.getRelation("members");
        try {
            List<ParseUser> groupMember = temp.getQuery().find();
            int count = 1;
            for (ParseUser temp2: groupMember) {
                if (temp2.getObjectId() != mParseUser.getObjectId()) {
                    ParseObject userInfo = temp2.getParseObject("userInfo");
                    String userNickName =  (String) temp2.get("nickName");
                    if (userInfo != null) {
                        try {
                            Double longitude = userInfo.fetchIfNeeded().getDouble("longitude");
                            Double latitude = userInfo.fetchIfNeeded().getDouble("latitude");
                            addAnnotation(userNickName, new SKCoordinate(longitude, latitude), count);
                        } catch (ParseException e2) {
                            e2.printStackTrace();
                        }

                    }
                    count++;
                }
            }
        } catch (ParseException e2) {

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabActivity = (TabActivity) this.getActivity();

        // get current Parse user and its pointer to user info
        mParseUser = ParseUser.getCurrentUser();
        mIsLeader = mParseUser.getBoolean("isLeader");
        mUserInfo = mParseUser.getParseObject("userInfo");

        // check if user is in a group
        mGroupObjectId = (String) mParseUser.get("groupObjectId");
        if (mGroupObjectId != null && mGroupObjectId != "") {
            // get Group object
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
            query.getInBackground(mGroupObjectId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        if (object != null) {
                            mGroupOnParse = object;
                            showGroupMembersLocation(); // since mapView may not have been initialized yet, this may not do anything at all
                            hasGroup = true;


                            mTimer = new Timer();
                            mTimerTask = new MyTimerTask(mTabActivity.getMapFragment());
                            mTimer.schedule(mTimerTask, 1000, 3000); //delay 1000ms, repeat in 3000ms

                            mTabActivity.getMessageFragment().shouldStartRetrievingGroupMessages(mGroupObjectId);

                            mGroupName = mGroupOnParse.getString("groupName");
                            mGroupMemberObjectId = new ArrayList<String>();
                            ParseRelation<ParseUser> temp = mGroupOnParse.getRelation("members");
                            try {
                                List<ParseUser> groupMember = temp.getQuery().find();
                                for (ParseUser temp2: groupMember) {
                                    Log.w("getGroupObject", temp2.getObjectId());
                                    mGroupMemberObjectId.add(temp2.getObjectId());
                                }
                            } catch (ParseException e2) {

                            }
                        }
                    } else {
                        // something went wrong
                    }
                }
            });
        }
    }
    //map text
    private TextView annText;
    //map pop up view
    private View popUpView;
    //map pop up text
    private SKCalloutView mapPopup;
    //Map annotation to display text hash table
    private Map<Integer,String> annTextMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_map, container, false);
        //start map holder map view and start map view listener
        mapHolder = (SKMapViewHolder) rootView.findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);
        //map popup text view
        popUpView = inflater.inflate(R.layout.annotation_layout, null);
        annText = (TextView)popUpView.findViewById(R.id.textAnnotation);
        mapPopup = mapHolder.getCalloutView();
        mapPopup.setCustomView(popUpView);
        //initiate ann text map
        annTextMap = new HashMap<>();
        navigateButton = (ImageButton)rootView.findViewById(R.id.navigate_button);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!navInProg && currentPosition != null) {
                    if (desCoordinate != null) {
                        if (textToSpeechEngine == null) {
                            Toast.makeText(getContext(), "Initializing TTS engine",
                                    Toast.LENGTH_LONG).show();
                            textToSpeechEngine = new TextToSpeech(getContext(),
                                    new TextToSpeech.OnInitListener() {
                                        @Override
                                        public void onInit(int status) {
                                            if (status == TextToSpeech.SUCCESS) {
                                                int result = textToSpeechEngine.setLanguage(Locale.ENGLISH);
                                                if (result == TextToSpeech.LANG_MISSING_DATA || result ==
                                                        TextToSpeech.LANG_NOT_SUPPORTED) {
                                                    Toast.makeText(getContext(),
                                                            "This Language is not supported",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "text to speech not initialize",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            setAdvicesAndStartNavigation(MapAdvices.TEXT_TO_SPEECH);
                                        }
                                    });
                        } else {
                            setAdvicesAndStartNavigation(MapAdvices.TEXT_TO_SPEECH);
                        }
                        // get a route object and populate it with the desired properties
                        SKRouteSettings route = new SKRouteSettings();
                        // set start and destination points
                        route.setStartCoordinate(currentPosition.getCoordinate());
                        route.setDestinationCoordinate(desCoordinate);
                        // set the number of routes to be calculated
                        route.setNoOfRoutes(1);
                        // set the route mode
                        route.setRouteMode(((MavigationApplication)getActivity().getApplication()).getSkRouteMode());
                        // set whether the route should be shown on the map after it's computed
                        route.setRouteExposed(true);
                        // set the route listener to be notified of route calculation
                        // events
                        SKRouteManager.getInstance().setRouteListener(MapFragment.this);
                        // pass the route to the calculation routine
                        SKRouteManager.getInstance().calculateRoute(route);
                        navInProg = true;
                        navigateButton.setBackgroundResource(R.drawable.stop_navigation);

                    }
                } else {
                    //in navigation must be the stop button
                    stopNavigtion();

                }
            }
        });
        //search button
        searchButton = (Button) rootView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLeader == true) { // only a leader can do a search
                    Intent intent = new Intent(getActivity(), AdressSearch.class);
                    startActivity(intent);
                }

            }
        });
        //set geocoder
        geocoder = new Geocoder(getContext(),Locale.getDefault());
        //set current position
        currentPositionProvider = new SKCurrentPositionProvider(getActivity());
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(Utils.hasGpsModule(getActivity()), Utils.hasNetworkModule(getActivity()), false);
        return rootView;
    }

    // update destination
    private void updateDestination() {
        try {
            List<Address> temp = geocoder.getFromLocation(desCoordinate.getLatitude(), desCoordinate.getLongitude(), 1);
            if (!temp.isEmpty()) {
                Address address = temp.get(0);
                String result = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    result+=address.getAddressLine(i);
                    result+=" ";
                }
                if (getActivity() != null) {
                    ((MavigationApplication)getActivity().getApplication()).setDesAddress(address);
                    searchButton.setText(result);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leaveCurrentGroup() {
        // stop updating group info
        if (mTimer!= null) {
            mTimer.cancel();
            mTimer = null;
        }


        // stop getting group messages from Parse
        if (mTabActivity.getMessageFragment() != null) {
            mTabActivity.getMessageFragment().stopRetrievingGroupMessages();
        }

        hasGroup = false;
        mIsLeader = true; // on my own then I'm a leader of myself
        mGroupName = null;
        mGroupMemberObjectId = null;

        // update ParseUser on Parse
        mGroupObjectId = null;
        mParseUser.put("groupObjectId", "");
        mParseUser.put("isLeader", mIsLeader);
        mParseUser.saveInBackground();

        // update Group on Parse
        ParseRelation<ParseUser> updateGroup = mGroupOnParse.getRelation("members");

        // remove all group member's location
        mapView.deleteAllAnnotationsAndCustomPOIs();
        mapPopup.hide();


        
        try {
            List<ParseUser> groupMember = updateGroup.getQuery().find();
            Log.w("group member list", groupMember.toString());
            if (groupMember.size() == 1) {
                Log.w("delete", " enter delete group");
                mGroupOnParse.deleteInBackground(new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.w("delete", " succeed delete group");
                            mGroupOnParse = null;
                        } else {
                            // not used
                        }
                    }
                });
            } else {
                updateGroup.remove(mParseUser);
                mGroupOnParse.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            mGroupOnParse = null;
                        } else {
                            // not used
                        }
                    }
                });
            }
        } catch (ParseException e2) {

        }


    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    //add annotation
    private void addAnnotation(String text, SKCoordinate coordinate, int id) {
        if(mapView!=null){
            annTextMap.put(id, text);

            SKAnnotation ann = new SKAnnotation(0);
            ann.setMininumZoomLevel(5);
            ann.setUniqueID(id);
            ann.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_BLUE);
            ann.setLocation(coordinate);

            mapView.addAnnotation(ann, SKAnimationSettings.ANIMATION_NONE);
        }
    }

    private void removeAnnotation(int id) {
        mapView.deleteAnnotation(id);
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {

        mapView = mapHolder.getMapSurfaceView();
        mapView.deleteAllAnnotationsAndCustomPOIs();
        mapView.clearAllOverlays();

        if (currentPosition != null) {
            mapView.centerMapOnPosition(currentPosition.getCoordinate());
            start=false;
        }

        if (mGroupOnParse != null) {
            showGroupMembersLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapHolder.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTabActivity = (TabActivity) this.getActivity();

        // get current Parse user and its pointer to user info
        mParseUser = ParseUser.getCurrentUser();
        mUserInfo = mParseUser.getParseObject("userInfo");

        mapHolder.onResume();
        Address desAddress =((MavigationApplication) getActivity().getApplication()).getDesAddress();
        if (desAddress != null) {
            String result = "";
            for (int i = 0; i < desAddress.getMaxAddressLineIndex(); i++) {
                result += desAddress.getAddressLine(i);
                result += " ";
            }
            searchButton.setText(result);
            desCoordinate = new SKCoordinate(desAddress.getLongitude(), desAddress.getLatitude());
            addCircle();
            if(mapView != null) {
                mapView.centerMapOnPosition(desCoordinate);
                mapView.setZoom(17);
            }

            if (hasGroup) { // only leader can set new destination and upload it to Parse
                mGroupOnParse.put("destLatitude", desAddress.getLatitude());
                mGroupOnParse.put("destLongitude", desAddress.getLongitude());
                mGroupOnParse.saveInBackground();
            }
        }
    }
    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {
    }
    private void addCircle(){
        SKCircle circle = new SKCircle();
        circle.setCircleCenter(desCoordinate);
        float[] out = new float[4];
        out[0] = (float) 0.99;
        out[1] = (float) 0.553;
        out[2] = (float) 0.016;
        out[3] = (float) 0.6;
        circle.setOutlineColor(out);
        float[] in = new float[4];
        in[0] = (float) 0.99;
        in[1] = (float) 0.553;
        in[2] = (float) 0.016;
        in[3] = (float) 0.6;
        circle.setColor(in);
        circle.setRadius((float) 4.5);
        circle.setOutlineSize(100);
        circle.setMaskedObjectScale((float) 2);
        if (mapView != null) {
            mapView.addCircle(circle);
        }
    }
    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {
        if(mapView!=null) {
            if (mIsLeader == true) {
                desCoordinate = mapView.pointToCoordinate(skScreenPoint);
                updateDestination();
                addCircle();

                if (hasGroup) { // only leader can set new destination and upload it to Parse
                    mGroupOnParse.put("destLatitude", desCoordinate.getLatitude());
                    mGroupOnParse.put("destLongitude", desCoordinate.getLongitude());
                    mGroupOnParse.saveInBackground();
                }
            }

        }
    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {

    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {

    }

    @Override
    public void onAnnotationSelected(SKAnnotation skAnnotation) {
        annText.setText(annTextMap.get(skAnnotation.getUniqueID()));
        int annotationHeight = (int) (64 * getResources().getDisplayMetrics().density);;
        float annotationOffset = skAnnotation.getOffset().getY();
        mapPopup.setVerticalOffset(-annotationOffset + annotationHeight / 2);
        mapPopup.showAtLocation(skAnnotation.getLocation(), true);
    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onObjectSelected(int i) {

    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onBoundingBoxImageRendered(int i) {

    }

    @Override
    public void onGLInitializationError(String s) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    @Override
    public void onCurrentPositionUpdate(SKPosition skPosition) {

        this.currentPositionTime = System.currentTimeMillis();
        this.currentPosition = skPosition;
        if(this.start){
            if (mapView != null){
                mapView.centerMapOnPosition(this.currentPosition.getCoordinate());
                this.start=false;
            }
        }
        if (mUserInfo != null) {
            mUserInfo.put("longitude",this.currentPosition.getCoordinate().getLongitude());
            mUserInfo.put("latitude",this.currentPosition.getCoordinate().getLatitude());
            mUserInfo.saveInBackground();
        }

        SKPositionerManager.getInstance().reportNewGPSPosition(this.currentPosition);
    }

    @Override
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {

    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onAllRoutesCompleted() {
        SKNavigationSettings navigationSettings = new SKNavigationSettings();

        navigationSettings.setNavigationType(((MavigationApplication)getActivity().getApplication()).getNavigationType());

        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mapView);
        navigationManager.setNavigationListener(this);
        navigationManager.startNavigation(navigationSettings);
        searchButton.setClickable(false);
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

    }

    @Override
    public void onDestinationReached() {
        stopNavigtion();

    }

    @Override
    public void onSignalNewAdviceWithInstruction(String instruction) {
        textToSpeechEngine.speak(instruction, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean b) {
        SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] audioFiles, boolean b) {
        SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
    }

    @Override
    public void onSpeedExceededWithInstruction(String instruction, boolean b) {
        textToSpeechEngine.speak(instruction, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState) {
        double distance = skNavigationState.getDistanceToDestination();
        double speed = skNavigationState.getCurrentSpeed();
        if(speed != 0) {
            // write estimationTime in minutes
            mUserInfo.put("estimationTime", distance/speed/60);
        }else if(distance==0){
            mUserInfo.put("estimationTime", 0);
        }
    }

    @Override
    public void onReRoutingStarted() {

    }

    @Override
    public void onFreeDriveUpdated(String s, String s1, String s2, SKNavigationState.SKStreetType skStreetType, double v, double v1) {

    }

    @Override
    public void onViaPointReached(int i) {

    }

    @Override
    public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState) {

    }

    @Override
    public void onTunnelEvent(boolean b) {

    }
    /**
     * Setting the audio advices
     */
    private void setAdvicesAndStartNavigation(MapAdvices currentMapAdvices) {
        final SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorConfigPath(((MavigationApplication) getActivity().getApplication()).getMapResourcesDirPath() + "/Advisor");
        advisorSettings.setResourcePath(((MavigationApplication) getActivity().getApplication()).getMapResourcesDirPath() + "/Advisor/Languages");
        advisorSettings.setAdvisorVoice("en");
        switch (currentMapAdvices) {
            case AUDIO_FILES:
                advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
                break;
            case TEXT_TO_SPEECH:
                advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.TEXT_TO_SPEECH);
                break;
        }
        SKRouteManager.getInstance().setAudioAdvisorSettings(advisorSettings);
    }
    private void stopNavigtion(){
        if (textToSpeechEngine != null) {
            textToSpeechEngine.stop();
        }
        navInProg = false;
        SKRouteManager.getInstance().clearCurrentRoute();
        SKNavigationManager.getInstance().stopNavigation();
        navigateButton.setBackgroundResource(R.drawable.navigate);
        searchButton.setClickable(true);
    }
}