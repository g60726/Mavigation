package com.example.ninasmacpro.mavigation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends Activity {
    private ListView mFriendsList = null;

    private ParseUser mParseUser = null;
    private ParseObject mUserInfo = null; // user's associated UserInfo object
    private List<ParseUser> mParseObjectFriends = null; // user's list of friends
    MyCustomAdapter mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mParseUser = ParseUser.getCurrentUser();
        getFriends();

    }

    // get user's friends from Parse
    private void getFriends() {
        mParseUser.getParseObject("userInfo").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    mUserInfo = object; //TODO: will this work?
                    ParseRelation friendsRelation = mUserInfo.getRelation("friends");
                    mParseObjectFriends = friendsRelation.getQuery().find();
                    displayListView(); // once we get a list of friends, display them
                    checkButtonClick();
                } catch (com.parse.ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    // display a list of friends
    private void displayListView() {

        ArrayList<Friend> friends = new ArrayList<Friend>();
        for (ParseUser parseObjectFriend: mParseObjectFriends) {
            Friend friend = new Friend((String) parseObjectFriend.get("nickName"), parseObjectFriend.getEmail(),false);
            friends.add(friend);
        }

        Friend friend = new Friend("Johny", "he@sp.xom",false);
        friends.add(friend);

        mAdapter = new MyCustomAdapter(this, R.layout.friend_layout, friends);
        mFriendsList = (ListView) findViewById(R.id.friendList);
        // Assign adapter to ListView
        mFriendsList.setAdapter(mAdapter);


        mFriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });

    }

    public class Friend {

        String code = null; // nickName
        String name = null; // email
        boolean selected = false;

        public Friend(String code, String name, boolean selected) {
            super();
            this.code = code;
            this.name = name;
            this.selected = selected;
        }

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

    }


    private class MyCustomAdapter extends ArrayAdapter<Friend> {
        private ArrayList<Friend> friendList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Friend> friendList) {
            super(context, textViewResourceId, friendList);
            this.friendList = friendList;
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.friend_layout, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Friend friend = (Friend) cb.getTag();
                        friend.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Friend friend = friendList.get(position);
            holder.code.setText(" (" +  friend.getCode() + ")");
            holder.name.setText(friend.getName());
            holder.name.setChecked(friend.isSelected());
            holder.name.setTag(friend);

            return convertView;

        }

    }

    private void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            // once clicked, add all selected friends into group
            @Override
            public void onClick(View v) {
                /*
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Friend> friendList = mAdapter.friendList;
                for(int i=0;i<friendList.size();i++){
                    Friend friend = friendList.get(i);
                    if(friend.isSelected()){
                        responseText.append("\n" + friend.getName());
                    }
                }
                */
            }
        });

    }

}
