package com.example.ninasmacpro.mavigation;

import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by ninasmacpro on 16/2/6.
 */
public class UserData {
    private static final String TAG = "UserData";

    public void init() {

    }

    // create a new ParseUser when a user sign up.
    public void signUp(String password, String email, String nickName) {
        ParseUser user = new ParseUser();
        user.setUsername(email); //user name is the same as email, and is unique
        user.setPassword(password);
        user.setEmail(email);

        // other fields can be set just like with ParseObject
        user.put("nickName", nickName);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException to figure out what went wrong
                    log("Invalid user name or email!");
                }
            }
        });
    }



    private static void log(String msg) {
        Log.v(TAG, msg);
    }
}
