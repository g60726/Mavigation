package com.example.ninasmacpro.mavigation;


import android.content.Intent;
import android.graphics.Color;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;


import com.skobbler.ngx.routing.SKRouteSettings;


import java.io.IOException;
import java.util.Locale;

public class AdressSearch extends AppCompatActivity {
    private SearchView addressSearch = null;
    private Geocoder geocoder = null;
    private ListView searchListView =null;
    private Button walkButton,bikeButton,carButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);
        addressSearch = (SearchView)findViewById(R.id.searchAddress);
        searchListView = (ListView)findViewById(R.id.addressListView);
        walkButton = (Button) findViewById(R.id.walkButton);
        bikeButton = (Button) findViewById(R.id.buttonBike);
        carButton = (Button) findViewById(R.id.driveButton);
        walkButton.setOnClickListener(new modeListener(SKRouteSettings.SKRouteMode.PEDESTRIAN,bikeButton,carButton));
        bikeButton.setOnClickListener(new modeListener(SKRouteSettings.SKRouteMode.BICYCLE_FASTEST,walkButton,carButton));
        carButton.setOnClickListener(new modeListener(SKRouteSettings.SKRouteMode.CAR_FASTEST,bikeButton,walkButton));
        SKRouteSettings.SKRouteMode mode = ((MavigationApplication)getApplication()).getSkRouteMode();
        if (mode == SKRouteSettings.SKRouteMode.CAR_FASTEST){
            carButton.setBackgroundColor(Color.WHITE);
            walkButton.setBackgroundColor(Color.LTGRAY);
            bikeButton.setBackgroundColor(Color.LTGRAY);
        }else if (mode == SKRouteSettings.SKRouteMode.PEDESTRIAN){
            walkButton.setBackgroundColor(Color.WHITE);
            carButton.setBackgroundColor(Color.LTGRAY);
            bikeButton.setBackgroundColor(Color.LTGRAY);
        }else{
            bikeButton.setBackgroundColor(Color.WHITE);
            carButton.setBackgroundColor(Color.LTGRAY);
            walkButton.setBackgroundColor(Color.LTGRAY);
        }
        geocoder = new Geocoder(this, Locale.getDefault());
        addressSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    searchListView.setAdapter(new AddressListAdapter(getApplicationContext(),geocoder.getFromLocationName(query,20)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Address desAddress = (Address) parent.getItemAtPosition(position);
                ((MavigationApplication) getApplication()).setDesAddress(desAddress);
                finish();
            }
        });
    }
    private class modeListener implements View.OnClickListener{
        private final MavigationApplication app;
        private final SKRouteSettings.SKRouteMode skRouteMode;
        private final Button otherButton1;
        private final Button otherButton2;
        private modeListener(SKRouteSettings.SKRouteMode skRouteMode,Button otherButton1,Button otherButton2){
            this.skRouteMode = skRouteMode;
            this.app = (MavigationApplication)getApplication();
            this.otherButton1 = otherButton1;
            this.otherButton2 = otherButton2;
        }
        @Override
        public void onClick(View v) {
            ((Button)v).setBackgroundColor(Color.WHITE);
            otherButton1.setBackgroundColor(Color.LTGRAY);;
            otherButton2.setBackgroundColor(Color.LTGRAY);;
            app.setSkRouteMode(skRouteMode);
        }
    }
}

