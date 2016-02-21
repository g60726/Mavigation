package com.example.ninasmacpro.mavigation;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {

    private static final String TAG = "SearchFriendActivity";

    ListView lv;
    SearchView sv;
    List<ParseObject> ob;

//    ArrayAdapter<>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.findFriendListView);
        sv = (SearchView) findViewById(R.id.findFriendSearchView);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit: success");
                ParseQuery<ParseObject> queryFriend = new ParseQuery<ParseObject>(
                        "User");
                queryFriend.orderByAscending("username");
//                queryFriend.whereEqualTo()
//                queryFriend.getInBackground()
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


}
