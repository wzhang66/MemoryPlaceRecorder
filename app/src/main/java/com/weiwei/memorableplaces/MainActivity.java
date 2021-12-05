package com.weiwei.memorableplaces;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// I USE DATA PASSING METHOD TO store and update everything, while tutor use static method to make the data consistant across app


public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    ListView listView;
    ArrayList<LatLng> locationList = new ArrayList<>();
    ArrayList<String> listPlaces;
    LatLng selectedLocation;

    public String generateName(LatLng location){
        String result = location.toString();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            List<Address> listAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if(listAddress != null && listAddress.size() > 0){
                if(listAddress.get(0).getThoroughfare() != null){
                    result = listAddress.get(0).getThoroughfare();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Log.i("message", "coming");
                if(data != null){
                    if(data.getExtras() != null){
                        if(data.getExtras().getParcelable("location") != null){
                            LatLng incomingLocation = data.getExtras().getParcelable("location");
                            locationList.add(incomingLocation);
                        }
                    }
                }
            }
        }
        updateListView();
    }

    public void updateListView(){
        listPlaces = new ArrayList<String>();
        listPlaces.add("Add a new place...");
        if(locationList.size() > 0){
            for(LatLng loc : locationList){
                String locName = generateName(loc);
                listPlaces.add(locName);
            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listPlaces);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), locationActivity.class);
                if(i == 0){
                    intent.putExtra("location", (android.os.Parcelable) null);
                } else {
                    selectedLocation = locationList.get(i-1);
                    intent.putExtra("location", selectedLocation);
                }
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        updateListView();
    }
}