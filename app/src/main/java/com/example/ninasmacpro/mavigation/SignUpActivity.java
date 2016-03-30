package com.example.ninasmacpro.mavigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mRetypePasswordView;
    private EditText mNickNameView;
    private View mProgressView;
    private View mSignUpFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the sign up form.
        mEmailView = (EditText) findViewById(R.id.sign_up_email);
        mPasswordView = (EditText) findViewById(R.id.sign_up_password);
        mRetypePasswordView = (EditText) findViewById(R.id.sign_up_rpassword);
        mNickNameView = (EditText) findViewById(R.id.sign_up_nick_name);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.signup || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = 1;
                attemptSignUp();
            }
        });

        mSignUpFormView = findViewById(R.id.sign_up_form);
        mProgressView = findViewById(R.id.sign_up_progress);
    }

    /**
     * Attempts to sign in the account specified by the sign up form.
     */
    private void attemptSignUp() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the sign up attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String retypePassword = mRetypePasswordView.getText().toString();
        String nickName = mNickNameView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // check if password = retyped password
        if (!password.equals(retypePassword)) {
            log(password);
            log(retypePassword);
            mRetypePasswordView.setError("passwords don't match!");
            focusView = mRetypePasswordView;
            cancel = true;
        }

        // must enter a nick name
        if (TextUtils.isEmpty(nickName)) {
            mNickNameView.setError("Must enter a nick name!");
            focusView = mNickNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            showProgress(true);

            final ParseUser user = new ParseUser();
            user.setEmail(email);
            user.setUsername(email); //user name is the same as email, and is unique
            user.setPassword(password);
            user.put("nickName", nickName);
            user.put("hideEmail", false);
            //create user info object


            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        final ParseObject userInfo = new ParseObject("UserInfo");
                        userInfo.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    user.put("userInfo", userInfo);
                                    user.saveInBackground();
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put("user",ParseUser.getCurrentUser().getObjectId());
                                    installation.saveInBackground();
                                } else {
                                }
                            }
                        });
                        // Hooray! Let them use the app now.
                        finish();
                        Intent mapIntent = new Intent(SignUpActivity.this, TabActivity.class);
                        startActivity(mapIntent);
                    } else {
                        // Sign up didn't succeed. Look at the ParseException to figure out what went wrong
                        showProgress(false);
                        log("Invalid email!");
                        mEmailView.setError("Invalid email!");
                        mEmailView.requestFocus();
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return password.length() > 4;
        return true;
    }

    /**
     * Shows the progress UI and hides the sign up form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private static void log(String msg) {
        Log.v(TAG, msg);
    }

}
