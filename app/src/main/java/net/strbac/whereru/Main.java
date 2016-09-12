package net.strbac.whereru;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity implements LocationListener {

    private static Main instance;
//    private String provider;
    private String callerNumber;
    private String callerName;
    protected LocationManager locationManager;
//    protected LocationListener locationListener;
    Button reqButton;
    Button respButton;
    public static String whereruString = "whereru";
    public static String uriString = "https://maps.google.com/maps/@";

    public static Main instance() {
        return instance;
    }

    @Override
    public void onStart() {
        super.onStart();
        instance = this;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        reqButton = (Button) findViewById(R.id.reqButton);

        reqButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", whereruString);
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


        respButton = (Button) findViewById(R.id.respButton);
        respButton.setEnabled(false);

        respButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    String phoneNo = getCallerNumber();
                    if (phoneNo == null) {
                        respButton.setText("UNKNOWN CALLER");
                        return;
                    }

                    StringBuilder uri = new StringBuilder();
                    uri.append(Uri.parse(getURI()));
                    SmsManager mgr = SmsManager.getDefault();
                    mgr.sendTextMessage(phoneNo, null, uri.toString(), null, null);
                    setResponseButtonColor(Color.GREEN);
                    respButton.setEnabled(false);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Failed to send location!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

    }

    public void setResponseButtonText(String callerName) {
        respButton.setText(callerName + " IS ASKING WHERE YOU ARE. CLICK HERE!");
    }


    public void setResponseButtonColor(int color) {
        if (!respButton.isEnabled()) {
            respButton.setEnabled(true);
        }
        respButton.setBackgroundColor(color);
    }

    public String getURI() {
        Location location = null;
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
            }

            return uriString + location.getLatitude() + "," + location.getLongitude();

        } catch (NullPointerException npe) {
              npe.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void showMap(String uri) {
        String coordinates = "geo:" + uri.replaceFirst(uriString, "");
        Uri gmmIntentUri = Uri.parse(coordinates);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }


    public void setCallerNumber(String cn) {
        callerNumber = cn;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void onLocationChanged(Location location) {

    }


    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("Latitude", "status"); }

}