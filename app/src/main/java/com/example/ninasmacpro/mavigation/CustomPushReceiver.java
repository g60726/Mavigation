package com.example.ninasmacpro.mavigation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhengyu on 16-03-25.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    public CustomPushReceiver() {
        super();
    }
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //Start activity
//
//    }
@Override
protected void onPushOpen(Context context, Intent intent) {
    JSONObject pushData = null;

    try {
        pushData = new JSONObject(intent.getExtras().getString("com.parse.Data"));
//        Log.i("debug", pushData.getString("alert"));
        String action = pushData.getString("action");
//        Log.i("action is: ", action);

        if(action.equals("friend")){
            Log.i("friend action: ", " is ready ");
            String contactEmail = pushData.getString("contactEmail");
            String username = pushData.getString("username");
            Intent i = new Intent(context, SearchFriendActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("contactEmail", contactEmail);
            i.putExtra("username", username);
            context.startActivity(i);
        }
        if (action.equals("group")){
            Log.i("action: ", " is to group ");
            String groupObjectId = pushData.getString("groupObjectId");

//            Intent i = new Intent(context, TabActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.putExtra("groupObjectId", groupObjectId);
//            context.startActivity(i);


            Intent i = new Intent();
            i.setAction("updateGroup");
            i.putExtra("groupObjectId", groupObjectId);

            context.sendBroadcast(i);


        }
//        pushData.getString("data");
//        Intent pushIntent = new Intent(context, YourActivity.class);
//        pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        pushIntent.putExtra("store", pushData.getString("data"));
//        context.startActivity(pushIntent);

    } catch (JSONException e) {
        e.printStackTrace();
    }
}
}
