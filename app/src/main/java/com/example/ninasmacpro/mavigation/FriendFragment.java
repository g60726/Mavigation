package com.example.ninasmacpro.mavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ListView mFriendListView;
    private ArrayAdapter<String> mFriendAdapter;
    // private OnFragmentInteractionListener mListener;

    public FriendFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
//    ListView lv;
//    SearchView sv;
//    private List<Population> friendpoopulationlist = null;
//    FriendListViewAdapter adapter;

    private List<Population> friendList = null;
    ListView friendLv;
    ListFriendViewAdapter adapter;
    private void getFriendList(View view){
//        List<Population> friendList;
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> relation = user.getRelation("friends");
        friendList = new ArrayList<Population>();
        try{
            List<ParseUser> contactObjects = relation.getQuery().find();
            for (ParseUser friend: contactObjects){

                Population people = new Population();
                people.setNickname((String) friend.get("nickName"));
                people.setUsername((String) friend.get("username"));
                people.setObjectId((String) friend.getObjectId());
//                if (friendList != null) {
//                    Log.i("check~~","friendList is not null");
//                }else {
//                    Log.i("check~~","friendList is null");
//                }
                friendList.add(people);

            }
            friendLv = (ListView) view.findViewById(R.id.friendListView);
            adapter = new ListFriendViewAdapter(getActivity(), friendList);
            if (friendLv != null) {
                Log.i("check~~","friendLv is not null");
            }else {
                Log.i("check~~","friendLv is null");
            }
            friendLv.setAdapter(adapter);
        }catch (ParseException e){

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        friendLv = (ListView) rootView.findViewById(R.id.friendListView);

        getFriendList(rootView);
//
//        friendLv = (ListView) rootView.findViewById(R.id.findFriendListView);
//        adapter = new FriendListViewAdapter(getActivity(), friendList);
//        friendLv.setAdapter(adapter);


//
//        mFriendListView = (ListView) rootView.findViewById(R.id.friendListView);
//        mFriendAdapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, new String[] { "Item 1",
//                "Item 2", "Item 2", "Item 3", "Item 4", "Item 5" });
//        mFriendListView.setAdapter(mFriendAdapter);



//        add friend button
        Button newPage = (Button)rootView.findViewById(R.id.searchFriendButton);
        newPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });

        Button refresh = (Button)rootView.findViewById(R.id.refreshFriendButton);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshFriendList();
            }
        });
        return rootView;
    }
    public void refreshFriendList(){
        friendList = null;
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> relation = user.getRelation("friends");
        friendList = new ArrayList<Population>();
        try{
            List<ParseUser> contactObjects = relation.getQuery().find();
            for (ParseUser friend: contactObjects){

                Population people = new Population();
                people.setNickname((String) friend.get("nickName"));
                people.setUsername((String) friend.get("username"));
                people.setObjectId((String) friend.getObjectId());
//                if (friendList != null) {
//                    Log.i("check~~","friendList is not null");
//                }else {
//                    Log.i("check~~","friendList is null");
//                }
                friendList.add(people);

            }
            adapter.getData().clear();
            adapter.getData().addAll(friendList);
            adapter.notifyDataSetChanged();

//            friendLv = (ListView) view.findViewById(R.id.friendListView);
//            adapter = new FriendListViewAdapter(getActivity(), friendList);
//            if (friendLv != null) {
//                Log.i("check~~","friendLv is not null");
//            }else {
//                Log.i("check~~","friendLv is null");
//            }
//            friendLv.setAdapter(adapter);
        }catch (ParseException e){

        }
    }


}
