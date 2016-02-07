package com.example.ninasmacpro.mavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class SettingActivity extends AppCompatActivity {

    private Button logOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logOutButton = (Button) findViewById(R.id.logOutButton);

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
