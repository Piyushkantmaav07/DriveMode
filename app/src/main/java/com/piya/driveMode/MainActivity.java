package com.piya.driveMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements LocationListener {
    ToggleButton bot_status;//Toggle to turn on/off the Bot
    ToggleButton call_bot_status;//Toggle to turn on/off the call Bot
    SharedPreferences sharedPreferences;//Shared Prefrence for Bot
    SharedPreferences callSharedPreferences;//Shared Prefrence for Bot
    SharedPreferences callSharedPreferences1;//Shared Prefrence for Bot
    LocationManager mlocationManager;
    Location mlocation;
    double lattitude,longitude;
    Switch switch1;
    Boolean check = false;

    public Boolean getCheck() {
        return check;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switch1 = findViewById(R.id.switch1);
        //initialization
        bot_status = findViewById(R.id.toggleButton);
        call_bot_status = findViewById(R.id.calltoogle);
        sharedPreferences = getSharedPreferences(Const.BOT, Context.MODE_PRIVATE);
        callSharedPreferences = getSharedPreferences(Const.CALLBOT, Context.MODE_PRIVATE);
        callSharedPreferences1 = getSharedPreferences(Const.CALLBOT1, Context.MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        //updatevalue
        mlocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mlocation = mlocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        //Ask for Notification Permission
        if(!Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners").contains(getPackageName())){
            Toast.makeText(this, "Please Enable Notification Access", Toast.LENGTH_LONG).show();
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        bot_status.setChecked(getStatus());
        bot_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(!getStatus());//Invert or Toggle :P

            }
        });
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked()){
                    check = true;
                }
                lattitude = mlocation.getLatitude();
                longitude = mlocation.getLongitude();
                callSharedPreferences.edit().putString("lat",String.valueOf(lattitude)).apply();
                callSharedPreferences1.edit().putString("lng",String.valueOf(longitude)).apply();
            }
        });


        if(mlocation!=null && switch1.isChecked()) {

            lattitude = mlocation.getLatitude();
            longitude = mlocation.getLongitude();

            switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(switch1.isChecked()){
                        check = true;
                    }
                    callSharedPreferences.edit().putString("lat",String.valueOf(lattitude)).apply();
                    callSharedPreferences1.edit().putString("lng",String.valueOf(longitude)).apply();

                }
            });

        }
        else {
            Toast.makeText(MainActivity.this,"Location can't be fetched try after sometime",Toast.LENGTH_LONG).show();
            callSharedPreferences.edit().putString("lat","null").apply();
            callSharedPreferences1.edit().putString("lng","null").apply();
        }



    }



    @Override
    public void onLocationChanged(Location location) {

        if(location!=null && switch1.isChecked()) {

            lattitude = location.getLatitude();
            longitude = location.getLongitude();

            if(getStatus()){
                callSharedPreferences.edit().putString("lat",String.valueOf(lattitude)).apply();
                callSharedPreferences1.edit().putString("lng",String.valueOf(longitude)).apply();
            }

        }
        else {
            Toast.makeText(MainActivity.this,"Location can't be fetched try after sometime",Toast.LENGTH_LONG).show();
            callSharedPreferences.edit().putString("lat","null").apply();
            callSharedPreferences1.edit().putString("lng","null").apply();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /*
    * Get Status of Toggle
    */
    private boolean getStatus(){
        return sharedPreferences.getBoolean(Const.STATUS, false);
    }

    /*
     * Set Status of Toggle
     */
    private void setStatus(boolean status){
        sharedPreferences.edit().putBoolean(Const.STATUS, status).apply();
    }



    /*
   * Get Call Status of Toggle
   */

}
