package com.example.ninasmacpro.mavigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnnotation;
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
import com.skobbler.ngx.util.SKLogging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


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
    destination cordinate creating by single tap
     */
    private SKCoordinate desCordinate = null;
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
            intent.putStringArrayListExtra("currentGroupMember", mGroupMemberObjectId);
            startActivityForResult(intent, REQUEST_CODE_ADD_PEOPLE_TO_GROUP);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CREATE_GROUP) {
            if (Activity.RESULT_OK == resultCode) {
                // Grab whatever data identifies that car that was sent in
                // setResult(int, Intent)
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
                mGroupName = bundle.getString("groupName"); // user may have changed group name
                ArrayList<String> newGroupMemberObjectId = bundle.getStringArrayList("groupMemberObjectId");
                addToCurrentGroup(newGroupMemberObjectId);
            } else {
                // now used now
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    private void updateGroup(ArrayList<String> newGroupMember) {
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
                    for (ParseUser user: users) {
                        relation.add(user);
                    }
                    mGroupOnParse.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            mGroupObjectId = mGroupOnParse.getObjectId();
                            mParseUser.put("groupObjectId", mGroupObjectId);
                            mParseUser.saveInBackground();
                        }
                    });
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get current Parse user and its pointer to user info
        mParseUser = ParseUser.getCurrentUser();

        // check if user is in a group
        mGroupObjectId = (String) mParseUser.get("groupObjectId");
        if (mGroupObjectId != null) {
            // get Group object
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
            query.getInBackground(mGroupObjectId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        if (object != null) {
                            mGroupOnParse = object;
                            hasGroup = true;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_map, container, false);
        //start map holder map view and start map view listener
        mapHolder = (SKMapViewHolder) rootView.findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);
        navigateButton = (ImageButton)rootView.findViewById(R.id.navigate_button);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!navInProg && currentPosition != null) {
                    if (desCordinate != null) {
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
                        route.setDestinationCoordinate(desCordinate);
                        // set the number of routes to be calculated
                        route.setNoOfRoutes(1);
                        // set the route mode
                        route.setRouteMode(SKRouteSettings.SKRouteMode.CAR_FASTEST);
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
                }else{
                    //in navigation must be the stop button
                    stopNavigtion();

                }
            }
        });
        //set current position
        currentPositionProvider = new SKCurrentPositionProvider(getActivity());
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(Utils.hasGpsModule(getActivity()), Utils.hasNetworkModule(getActivity()), false);
        return rootView;
    }

    public void leaveCurrentGroup() {
        hasGroup = false;
        mGroupName = null;
        mGroupMemberObjectId = null;

        // update ParseUser on Parse
        mGroupObjectId = null;
        mParseUser.put("groupObjectId", null);
        mParseUser.saveInBackground();

        // update Group on Parse
        mGroupOnParse = null;
        ParseRelation<ParseUser> updateGroup = mGroupOnParse.getRelation("members");
        updateGroup.remove(mParseUser); // FIXME: will this work?
        mGroupOnParse.saveInBackground();

    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {

        mapView = mapHolder.getMapSurfaceView();
        mapView.clearAllOverlays();
        if(currentPosition != null){
            mapView.centerMapOnPosition(currentPosition.getCoordinate());
            start=false;
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
        mapHolder.onResume();
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

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {
        
        desCordinate = mapView.pointToCoordinate(skScreenPoint);
        SKCircle circle = new SKCircle();
        circle.setCircleCenter(desCordinate);
        float[] out = new float[4];
        out[0] = (float)0.99;
        out[1] = (float)0.553;
        out[2] = (float)0.016;
        out[3] = (float)0.6;
        circle.setOutlineColor(out);
        float[] in = new float[4];
        in[0] = (float)0.99;
        in[1] = (float)0.553;
        in[2] = (float)0.016;
        in[3] = (float)0.6;
        circle.setColor(in);
        circle.setRadius((float) 4.5);
        circle.setOutlineSize(100);
        circle.setMaskedObjectScale((float)2);
        mapView.addCircle(circle);
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
            System.out.println(skNavigationState.getDistanceToDestination());
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
    }
}
