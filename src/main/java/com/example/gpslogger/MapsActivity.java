package com.example.gpslogger;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String fileName;
    private FileInputStream is;
    private BufferedReader reader;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        file = new File(getExternalFilesDir(null)+ "/" + fileName);

        try{
            is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null){
                String[] locInfo = line.split(",");
                LatLng marker = new LatLng(Double.parseDouble(locInfo[2]),Double.parseDouble(locInfo[3]));
                mMap.addMarker(new MarkerOptions().position(marker).title(locInfo[0] + " " + locInfo[1]));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                Log.d("lat-long",line);
                line = reader.readLine();
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException i){
            i.printStackTrace();
        }
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
       //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
