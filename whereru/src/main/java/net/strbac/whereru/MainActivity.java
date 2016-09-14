package net.strbac.whereru;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {

    private WhereRUService service;
    private static MainActivity instance;
    private String callerNumber;
    private String callerName;
    protected LocationManager locationManager;
    Button reqButton;
    Button respButton;
    public static String whereruString = "whereru";
    public static String uriString = "https://maps.google.com/maps/@";

    public static MainActivity instance() {
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
                        setResponseButtonText("UNKNOWN CALLER");
                        return;
                    }

                    StringBuilder uri = new StringBuilder();
                    uri.append(Uri.parse(getURI()));
                    SmsManager mgr = SmsManager.getDefault();
                    mgr.sendTextMessage(phoneNo, null, uri.toString(), null, null);
                    setResponseButtonText("LOCATION SENT");
                    setResponseButtonColor(Color.GREEN);
                    respButton.setEnabled(false);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Failed to send location. Try again later.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

    }

    public void setResponseButtonText(String text) {
        respButton.setText(text);
    }


    public void setResponseButtonColor(int color) {
        if (!respButton.isEnabled()) {
            respButton.setEnabled(true);
        }
        respButton.setBackgroundColor(color);
    }


    public void buttonBlink(Button button) {
        setContentView(R.layout.main);

        Animation mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(300);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(3);
        mAnimation.setRepeatMode(Animation.REVERSE);
        button.startAnimation(mAnimation);
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

    public void onLocationChanged(Location location) {  }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, WhereRUService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            WhereRUService.WRUBinder b = (WhereRUService.WRUBinder) binder;
            service = b.getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
        }
    };


    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("Latitude", "status"); }

}