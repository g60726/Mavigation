package com.example.ninasmacpro.mavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengyu on 16-02-20.
 */
public class FriendListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    private List<Population> friendPopulation = null;
    private ArrayList<Population> arraylist;

    public FriendListViewAdapter(Context context,
                           List<Population> friendPopulation) {
        this.context = context;
        this.friendPopulation = friendPopulation;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Population>();
        this.arraylist.addAll(friendPopulation);
    }

    public class ViewHolder {
        TextView username;
        String objectId;
        TextView nickname;
    }

    public List<Population> getData(){
        return   friendPopulation;
    }
    @Override
    public int getCount() {
        return friendPopulation.size();
    }

    @Override
    public Object getItem(int position) {
        return friendPopulation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.friendlistview_item, null);

            //locate TextViews in friendlistview_item.xml
            holder.username = (TextView) convertView.findViewById(R.id.userNameTextView);
            holder.nickname = (TextView)  convertView.findViewById(R.id.nickNameTextView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Set the results into TextViews
        final String nickname = friendPopulation.get(position).getNickname();
        final String objectId = friendPopulation.get(position).getObjectId();
        holder.username.setText(friendPopulation.get(position).getUsername());
        holder.nickname.setText(nickname);
        holder.objectId = friendPopulation.get(position).getObjectId();
        // Listen for ListView Item Click
        convertView.setOnClickListener(new View.OnClickListener() {
            private String TAG = "in the friend list view page";

            @Override
            public void onClick(View v) {
                showFriendConfirmDialog(v, nickname, objectId);
                Log.i(TAG, "onClick: loading... ");
            }
        });
        return convertView;
    }

    private AlertDialog.Builder builder;
    private void showFriendConfirmDialog(final View view, final String nickname, final String objectId){
        builder=new AlertDialog.Builder(view.getContext());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Follow");
        builder.setMessage("Are you sure you want to add " + nickname);

        //listen to yes/no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(), R.string.toast_negative, Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(),R.string.toast_postive, Toast.LENGTH_SHORT).show();
                final ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<ParseObject> relation = user.getRelation("friends");
                relation.add(ParseObject.createWithoutData("_User", objectId));
                user.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        showCallBackInfo(view);
                        sendFriendRequest(objectId, user.getUsername(), user.getEmail());
                    }
                });
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private void sendFriendRequest(String contactObjectId, String username, String contactEmail){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", contactObjectId);
        String message = username + " want to add you";
        try{
//            JSONObject data = new JSONObject("{\"alert\": \"xxxx want to add you!!!\"}");
            JSONObject data = new JSONObject();
            data.put("alert", message);
//            JSONObject data = new JSONObject("{\"alert\": \"xxxx want to add you!\",\"uri\": \"myapp://host/path\"}");
            data.put("action", "friend");
            data.put("contactEmail", contactEmail);
            ParsePush push = new ParsePush();
            push.setQuery(pushQuery); // Set our Installation query
//            push.setMessage("xxxx want to add you");
            push.setData(data);
//            push.sendPushInBackground();
            push.sendInBackground();
        }catch (Exception e){
            Log.i("debug3", "jason data type went wrong");
        }

    }
    private void showCallBackInfo(View view){
        AlertDialog.Builder alert;
        alert=new AlertDialog.Builder(view.getContext());
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle("Add friend");
        alert.setMessage("successfully");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(),R.string.toast_postive, Toast.LENGTH_SHORT).show();
                Log.i("add friend", " successful");


            }
        });

        alert.setCancelable(true);
        AlertDialog dialog=alert.create();
        dialog.show();
    }

}
