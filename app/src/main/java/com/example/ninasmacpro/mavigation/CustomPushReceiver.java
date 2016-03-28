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
        String contactEmail = pushData.getString("contactEmail");
        Log.i("action is: ", action);

        if(action.equals("friend")){
            Log.i("friend action: ", " is ready ");
            Intent i = new Intent(context, SearchFriendActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("contactEmail", contactEmail);
            context.startActivity(i);
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
