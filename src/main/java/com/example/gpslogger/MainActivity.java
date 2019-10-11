package com.example.gpslogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private Button getLocation;
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation ;
    private String fileName;
    private File file;
    private String selectedFile;
    private TextView selectedFileTextView;

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocation = findViewById(R.id.getLocation);
        textView = findViewById(R.id.textView);
        selectedFileTextView = findViewById(R.id.selectedFile);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = location;
                    if (location != null) {
                        textView.setText(" Latitude " + location.getLatitude()
                                + "\n Longitude " + location.getLongitude() + "\n Speed " +
                                location.getSpeed());
                        writeToFile(location);
                    } else {
                        textView.setText("Location not identified");
                    }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            // checks if gps is turned off
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configureButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configureButton();
                break;
            default:
                break;
        }
    }

    private void configureButton() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }


        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    fileName = "gpsLogger" + System.currentTimeMillis() + ".csv";
                    file = new File(getExternalFilesDir(null),fileName);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

                    if (currentLocation == null) {
                        textView.setText("Location not identified, please wait");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void writeToFile(Location location) {
        String locInfo = System.currentTimeMillis() + "," + location.getSpeed() + "," + location.getLatitude() + "," + location.getLongitude() + "\n" ;

        try {

            OutputStream os = new FileOutputStream(file,true);
            os.write(locInfo.getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopGpsLogging(View view){
        try {
            locationManager.removeUpdates(locationListener);
        }catch(Exception e){
            e.printStackTrace();
        }
        textView.setText("GPS Logger");

    }

    public void createMap(View view){
        if(selectedFile == null){
            Context context = getApplicationContext();
            CharSequence text = "Please select file!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("fileName", selectedFile);
            startActivity(mapIntent);
        }
    }

    public void browseFiles(View view){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        String[] fileUri = resultData.getData().getLastPathSegment().split("/");
        selectedFile = fileUri[fileUri.length-1];
        selectedFileTextView.setText(selectedFile);

    }
}
