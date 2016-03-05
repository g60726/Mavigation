package com.example.ninasmacpro.mavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

public class SettingActivity extends AppCompatActivity {

    private Button logOutButton;
    private TextView nickNameTextView;
    private TextView leaveGroupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logOutButton = (Button) findViewById(R.id.logOutButton);
        nickNameTextView = (TextView) findViewById(R.id.nickNameTextView);
        leaveGroupTextView = (Button) findViewById(R.id.LeaveGroupButton);

        String nickName = (String) ParseUser.getCurrentUser().get("nickName");
        nickNameTextView.setText(nickName);

        leaveGroupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // not used
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                // the activity is finished
                finish();
                Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}
