package com.example.ninasmacpro.mavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;
import com.skobbler.ngx.navigation.SKNavigationSettings;

public class SettingActivity extends AppCompatActivity {

    private Button logOutButton;
    private TextView nickNameTextView;

    private Button simButton;
    private Button realButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logOutButton = (Button) findViewById(R.id.logOutButton);
        nickNameTextView = (TextView) findViewById(R.id.nickNameTextView);

        simButton = (Button) findViewById(R.id.button_sim);
        realButton = (Button) findViewById(R.id.button_real);

        String nickName = (String) ParseUser.getCurrentUser().get("nickName");
        nickNameTextView.setText(nickName);


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
        simButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNavigationType(SKNavigationSettings.SKNavigationType.SIMULATION);
            }
        });
        realButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
            }
        });
    }
    private void setNavigationType(SKNavigationSettings.SKNavigationType type){
        ((MavigationApplication)getApplication()).setNavigationType(type);
    }
}
