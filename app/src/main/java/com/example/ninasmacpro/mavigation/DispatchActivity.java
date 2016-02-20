package com.example.ninasmacpro.mavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;
/**
 * Here we go to different activity depends on whether or not the user has logged in already.
 */
public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, TabActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

}