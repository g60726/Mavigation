package com.example.ninasmacpro.mavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {

    private static final String TAG = "SearchFriendActivity";

    ListView lv;
    SearchView sv;
    private List<Population> friendpoopulationlist = null;
    FriendListViewAdapter adapter;
//    List<ParseObject> ob;

//    ArrayAdapter<>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.findFriendListView);
        sv = (SearchView) findViewById(R.id.findFriendSearchView);

        //
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sv.setVisibility(View.GONE);
            Log.i("debug ", "intent comes from notification:");
            String contactEmail = extras.getString("contactEmail");
            queryFriend(contactEmail);
        }

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Log.i(TAG, "onQueryTextSubmit: success " + " query: " + query);
//
////                ParseQuery queryFriend = ParseUser.getQuery();
//                ParseQuery<ParseUser> queryFriend = ParseUser.getQuery();
////                Log.d("check1", "working");
////                queryFriend.orderByAscending("username");
//                queryFriend.whereStartsWith("username", query);
//                queryFriend.findInBackground(new FindCallback<ParseUser>() {
//                    public void done(List<ParseUser> friendsList, ParseException e) {
//                        if (e == null) {
//                            Log.w("check", "this query was successful");
//                            friendpoopulationlist = new ArrayList<Population>();
////                            Log.d("score", "Retrieved " + friendsList.size() + " scores");
//                            for (ParseObject friend : friendsList) {
//                                // Locate images in flag column
////                                ParseFile image = (ParseFile) country.get("flag");
//
//                                Population people = new Population();
//                                people.setNickname((String) friend.get("nickName"));
//                                people.setUsername((String) friend.get("username"));
//                                people.setObjectId((String) friend.getObjectId());
//
////                                String nickname = (String )friend.get("nickName");
////                                Log.e("nick name is ", nickname);
////                                String username = (String )friend.get("username");
////                                Log.e("username is ", username);
////                                String objectId = (String )friend.getObjectId();
////                                Log.e("objectId is ", objectId);
////                                String nickname = people.getNickname();
////                                Log.e("nick name is ", nickname);
////                                String username = people.getUsername();
////                                Log.e("username is ", username);
////                                String objectId = people.getObjectId();
////                                Log.e("objectId is ", objectId);
//                                friendpoopulationlist.add(people);
//                            }
//                            lv = (ListView) findViewById(R.id.findFriendListView);
//                            adapter = new FriendListViewAdapter(SearchFriendActivity.this, friendpoopulationlist);
//                            lv.setAdapter(adapter);
//                        } else {
//                            Log.w("score", "Error: " + e.getMessage());
//                            Log.w("check", "Something went wrong.");
//
//                        }
//                    }
//                });
                queryFriend(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean queryFriend(String query) {
        Log.i(TAG, "onQueryTextSubmit: success " + " query: " + query);
        ParseQuery<ParseUser> queryFriend = ParseUser.getQuery();
        queryFriend.whereStartsWith("username", query);
        queryFriend.whereEqualTo("hideEmail", false);
        queryFriend.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> friendsList, ParseException e) {
                if (e == null) {
                    Log.w("check", "this query was successful");
                    friendpoopulationlist = new ArrayList<Population>();
                    for (ParseObject friend : friendsList) {
                        Population people = new Population();
                        people.setNickname((String) friend.get("nickName"));
                        people.setUsername((String) friend.get("username"));
                        people.setObjectId((String) friend.getObjectId());
                        friendpoopulationlist.add(people);
                    }
                    lv = (ListView) findViewById(R.id.findFriendListView);
                    adapter = new FriendListViewAdapter(SearchFriendActivity.this, friendpoopulationlist);
                    lv.setAdapter(adapter);
                } else {
                    Log.w("score", "Error: " + e.getMessage());
                    Log.w("check", "Something went wrong.");
                }
            }
        });
        return false;
    }
}
