package com.example.ninasmacpro.mavigation;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.IOException;
import java.util.Locale;

public class AdressSearch extends AppCompatActivity {
    private SearchView addressSearch = null;
    private Geocoder geocoder = null;
    private ListView searchListView =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);
        addressSearch = (SearchView)findViewById(R.id.searchAddress);
        searchListView = (ListView)findViewById(R.id.addressListView);
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
}
