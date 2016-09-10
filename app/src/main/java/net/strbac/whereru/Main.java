package net.strbac.whereru;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity implements LocationListener {

    private String provider;
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button reqB = (Button) findViewById(R.id.reqButton);

        reqB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    String smsBody = "Whare are you?";
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", smsBody);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

//        MessageChecker mc = new MessageChecker();

        Button respB = (Button) findViewById(R.id.respButton);

        respB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    String phoneNo = "14083344288";

                    StringBuilder smsBody = new StringBuilder();
                    smsBody.append(Uri.parse(getURI()));
                    SmsManager mgr = SmsManager.getDefault();
                    mgr.sendTextMessage(phoneNo, null, smsBody.toString(), null, null);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

    }


    public String getURI() {
        Location location;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Location in onCreate is NULL",
                        Toast.LENGTH_LONG).show();
                return null;
            }

            return "https://maps.google.com/maps/@" + location.getLatitude() + "," + location.getLongitude();

        } catch (SecurityException e) {
            e.printStackTrace();
            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
        }
        return null;
    }


    public void onLocationChanged(Location location) {

    }


    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

}