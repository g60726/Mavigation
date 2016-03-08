package com.example.ninasmacpro.mavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private ListView mFriendsList = null;

    private ParseUser mParseUser = null;
    private List<ParseUser> mParseObjectFriends = null; // user's list of friends
    private MyCustomAdapter mAdapter = null;
    private EditText mGroupNameEditText = null;

    private boolean hasGroup = false;
    private String mGroupName = "";
    ArrayList<Friend> mFriends = new ArrayList<Friend>();
    ArrayList<String> currentGroupMemberObjectId = new ArrayList<String>(); // a list of ObjectId

    private Button mLeaveGroupButton;
    private ListView mCurrentGroupMemberListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mGroupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        mLeaveGroupButton = (Button) findViewById(R.id.LeaveGroupButton);


        Bundle bundle = getIntent().getExtras();
        hasGroup = bundle.getBoolean("hasGroup");

        if (hasGroup) { // if there's a group, show group name in EditText
            mGroupName = bundle.getString("groupName");
            mGroupNameEditText.setText(mGroupName);
            currentGroupMemberObjectId = bundle.getStringArrayList("currentGroupMemberObjectId");
            mLeaveGroupButton.setVisibility(View.VISIBLE);
        } else {
            mLeaveGroupButton.setVisibility(View.GONE);
        }

        mParseUser = ParseUser.getCurrentUser();
        getFriends();

    }

    // get user's friends from Parse
    private void getFriends() {
        ParseRelation friendsRelation = mParseUser.getRelation("friends");
        ParseQuery<ParseUser> query = friendsRelation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    mParseObjectFriends = users;
                    displayListViews(); // once we get lists of friends/group members, display them
                    checkButtonsClick(); // only check buttons click after we get lists of friends/group members
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    // display a list of friends, and a list of current group members
    private void displayListViews() {
        mFriends = new ArrayList<Friend>();
        ArrayList<String> currentGroupMember = new ArrayList<String>();
        for (ParseUser parseObjectFriend: mParseObjectFriends) {
            String currentObjectId = parseObjectFriend.getObjectId();
            if (!currentGroupMemberObjectId.contains(currentObjectId)) { // put to list of friends
                Friend friend = new Friend(currentObjectId, parseObjectFriend.getEmail(),
                        (String) parseObjectFriend.get("nickName"), false);
                mFriends.add(friend);
            } else { // put to list of current group members
                StringBuilder temp = new StringBuilder();
                temp.append((String) parseObjectFriend.get("nickName"));
                temp.append(" (");
                temp.append(parseObjectFriend.getEmail());
                temp.append(")");
                currentGroupMember.add(temp.toString());
                mCurrentGroupMemberListView = (ListView) findViewById(R.id.currentGroupList);
                ArrayAdapter<String> currentGroupMemberAdapter = new ArrayAdapter<String>(this,
                        R.layout.group_current_member_list_layout, R.id.code2, currentGroupMember);
                mCurrentGroupMemberListView.setAdapter(currentGroupMemberAdapter);
            }

        }
        mAdapter = new MyCustomAdapter(this, R.layout.group_friend_list_layout, mFriends);
        mFriendsList = (ListView) findViewById(R.id.friendList);
        mFriendsList.setAdapter(mAdapter);

    }

    public class Friend {
        String objectId = null;
        String code = null; // nickName
        String name = null; // email
        boolean selected = false;

        public Friend(String objectId, String code, String name, boolean selected) {
            super();
            this.objectId = objectId;
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
                convertView = vi.inflate(R.layout.group_friend_list_layout, null);

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

    private void checkButtonsClick() {
        Button addFriendButton = (Button) findViewById(R.id.findSelected);
        addFriendButton.setOnClickListener(new View.OnClickListener() {

            // once clicked, add all selected friends into group and back to MapFragment
            @Override
            public void onClick(View v) {
                View focusView = null;
                boolean cancel = false;
                mGroupName = mGroupNameEditText.getText().toString();
                //TODO: must enter a group name
                if (TextUtils.isEmpty(mGroupName)) {
                    mGroupNameEditText.setError(getString(R.string.error_field_required));
                    focusView = mGroupNameEditText;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("groupName", mGroupName); // pass in groupName
                    intent.putExtra("leaveGroup", false);

                    // pass in all the selected friends' objectIds
                    ArrayList<String> selectedFriends = new ArrayList<String>();
                    for (Friend temp: mFriends) {
                        if (temp.isSelected()) {
                            selectedFriends.add(temp.objectId);
                        }
                    }
                    intent.putStringArrayListExtra("groupMemberObjectId", selectedFriends);

                    // Set the result with this data, and finish the activity
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        if (mLeaveGroupButton != null) {
            mLeaveGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("leaveGroup", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

}
