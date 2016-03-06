package com.example.ninasmacpro.mavigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.util.SKLogging;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements SKMapSurfaceListener, SKCurrentPositionListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_CODE_CREATE_GROUP = 1;
    private static final int REQUEST_CODE_ADD_PEOPLE_TO_GROUP = 2;

    private SKMapSurfaceView mapView; // Surface view for displaying the map


    private ParseUser mParseUser = null;
    private ParseObject mUserInfo = null; // user's associated UserInfo object

    private boolean hasGroup = false;
    //private List<ParseUser> groupUser = null;
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
        /*
        for (String temp: newGroupMemberObjectId) {
            mGroupMemberObjectId.add(temp);
        }*/

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
                    Log.w("add user to relation", "success");
                    // The query was successful.
                    for (ParseUser user: users) {
                        Log.w("add user to relation", "there is some user!");
                        relation.add(user);
                    }
                    mGroupOnParse.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            mGroupObjectId = mGroupOnParse.getObjectId();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_map, container, false);
        //start map holder map view and start map view listener
        mapHolder = (SKMapViewHolder) rootView.findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);

        //set current position
        currentPositionProvider = new SKCurrentPositionProvider(getActivity());
        currentPositionProvider.setCurrentPositionListener(this);
        currentPositionProvider.requestLocationUpdates(Utils.hasGpsModule(getActivity()), Utils.hasNetworkModule(getActivity()), false);
        return rootView;
    }

    public void leaveCurrentGroup() {
        hasGroup = false;
        mGroupOnParse = null;
        mGroupName = null;
        mGroupObjectId = null;
        mGroupMemberObjectId = null;
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
        SKCoordinate co = mapView.pointToCoordinate(skScreenPoint);
        SKCircle circle = new SKCircle();
        circle.setCircleCenter(co);

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
}
