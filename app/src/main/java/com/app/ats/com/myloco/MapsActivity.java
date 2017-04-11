package com.app.ats.com.myloco;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import com.app.ats.com.myloco.model.LocatorModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LatLng myPosition;
    GpsTracker gps;
    LatLng locationLL;
    String a, b;
    private Animation _animDown;
    private Animation _animUp;
    boolean mapFlag = false;
    boolean doubleBackToExitPressedOnce = false;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    ProgressDialog prgDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.onCreate(savedInstanceState);
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        _animDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animscaledown);
        _animUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animscaleup);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("turn On GPS");
            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_maps);

        String latlang = "";
        boolean flag = false;

        showActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                latlang = bundle.getString("latlang", "");
            }
            flag = bundle.getBoolean("current", false);
        }


        setUpMapIfNeeded(flag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded(mapFlag);
    }
    private void showActionBar() {
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflator.inflate(R.layout.findonmap, null);
        ImageButton imb =(ImageButton)v.findViewById(R.id.btn4);
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    popupsettings();
    }

    private void popupsettings() {
       final ImageButton imb =(ImageButton) findViewById(R.id.btn4);
        imb.setOnTouchListener(_animListener);
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void processWriteRequest() {

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(boolean flag) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);
        }
        mapFlag = flag;
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {
        LatLng currentLocation = currentLocation();
        if (currentLocation != null) {
//            zoomToPoint(currentLocation, 12);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(currentLocation);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));
            // Add new marker to the Google Map Android API V2
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.addMarker(options);
//                zoomToPoint(point, 15);

            locationLL = currentLocation;
//            button.setVisibility(View.VISIBLE);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            Button button = (Button) findViewById(R.id.selectLocation);

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);
//                zoomToPoint(point, 15);

                locationLL = point;
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpMapForCurrentLocation() {
        LatLng currentLocation = currentLocation();
        if (currentLocation != null) {
            Button button = (Button) findViewById(R.id.selectLocation);
            locationLL = currentLocation;

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
            // Enable MyLocation Button in the Map
            //mMap.setMyLocationEnabled(true);
            zoomToPoint(currentLocation, 12);
            // button.setVisibility(View.VISIBLE);
        }
    }

    public LatLng currentLocation() {
        gps = new GpsTracker(this);
        LatLng m = null;

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            m = new LatLng(latitude, longitude);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        return m;
    }

    public LocatorModel getLocator(double latitude, double longitude) {

        SharedPreferences sprf = this.getSharedPreferences("tokenpass", 0);
        LocatorModel loc = new LocatorModel();
        loc.setLatitude(String.valueOf(latitude));
        loc.setLongitude(String.valueOf(longitude));
        loc.setSenderName(sprf.getString("username", "<Anonymous>"));

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

            String add = "";
            if (addresses.size() > 0) {
                loc.setAddress_l1(addresses.get(0).getAddressLine(1));
                //loc.setAddress_l2(addresses.get(0).getAddressLine(2));
//                loc.setCity(addresses.get(0).getLocality());

                if (addresses.get(0).getLocality() == null) {
                    loc.setCity(addresses.get(0).getFeatureName());

                } else {
                    loc.setCity(addresses.get(0).getLocality());
                }
                loc.setCountry(addresses.get(0).getCountryName());
                loc.setZip(addresses.get(0).getPostalCode());

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return loc;
    }

    private void zoomToPoint(LatLng point, int level) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(point.latitude,
                        point.longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(level);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    private void showNavigation(String latlang) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latlang);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        finish();
    }

    /*public void selectLocation(View v){
        Intent intent = new Intent();
        intent.putExtra("location", getLocator(locationLL.latitude, locationLL.longitude));
       *//* double o=locationLL.latitude;
        double p=locationLL.longitude;
        LatLng   n = new LatLng(o, p);
        SharedPreferences sharedpreferences = getSharedPreferences("abc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("key", String.valueOf(n));
        editor.commit();*//*
        setResult(RESULT_OK, intent);
        finish();

    }*/

    public void selectLocation(View v) {
//        prgDialog = new ProgressDialog(this);
//        prgDialog.setMessage("Please wait...");
//        prgDialog.show();

        final Intent intent = new Intent();
        //intent.putExtra("location", getLocator(locationLL.latitude, locationLL.longitude));
        final LocatorModel loc = new LocatorModel();
        try {
            loc.setLatitude(String.valueOf(locationLL.latitude));
            loc.setLongitude(String.valueOf(locationLL.longitude));
            //loc = gps.fetchCityNameUsingGoogleMap(null, loc);
        }catch (Exception e){

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + loc.getLatitude() + ","
                    + loc.getLongitude() + "&sensor=false&language=en";

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(googleMapUrl, new JsonHttpResponseHandler() {

                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    prgDialog.hide();
                    if (prgDialog != null) {
                        prgDialog.dismiss();
                    }
                    try {
                        // search for locality and sublocality
                        String cityName = null;

                        JSONArray results = (JSONArray) json.get("results");
                        for (int i = 0; i < results.length(); i++) {
                            // loop among all addresses within this result
                            JSONObject result = results.getJSONObject(i);
                            if (result.has("address_components")) {
                                JSONArray addressComponents = result.getJSONArray("address_components");
                                // loop among all address component to find a 'locality' or 'sublocality'
                                for (int j = 0; j < addressComponents.length(); j++) {
                                    JSONObject addressComponent = addressComponents.getJSONObject(j);
                                    if (result.has("types")) {
                                        JSONArray types = addressComponent.getJSONArray("types");

                                        for (int k = 0; k < types.length(); k++) {
                                            if ("locality".equals(types.getString(k))) {
                                                if (addressComponent.has("long_name")) {
                                                    loc.setCity(addressComponent.getString("long_name"));
                                                } else if (addressComponent.has("short_name")) {
                                                    loc.setCity(addressComponent.getString("short_name"));
                                                }
                                            }
                                            if ("sublocality".equals(types.getString(k))) {
                                                if (addressComponent.has("long_name")) {
                                                    loc.setCity(addressComponent.getString("long_name"));
                                                } else if (addressComponent.has("short_name")) {
                                                    loc.setCity(addressComponent.getString("short_name"));
                                                }
                                            }
                                            if ("country".equals(types.getString(k))) {
                                                if (addressComponent.has("long_name")) {
                                                    loc.setCountry(addressComponent.getString("long_name"));
                                                } else if (addressComponent.has("short_name")) {
                                                    loc.setCountry(addressComponent.getString("short_name"));
                                                }
                                            }
                                            if ("route".equals(types.getString(k))) {
                                                if (addressComponent.has("long_name")) {
                                                    loc.setAddress_l1(addressComponent.getString("long_name"));
                                                } else if (addressComponent.has("short_name")) {
                                                    loc.setAddress_l1(addressComponent.getString("short_name"));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {

                    }
                    intent.putExtra("location", loc);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                    prgDialog.hide();
                    if (prgDialog != null) {
                        prgDialog.dismiss();
                    }
                    Log.e("omg android", statusCode + " " + throwable.getMessage());
                }
            });
        } else {
            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(Double.parseDouble(loc.getLatitude()),
                        Double.parseDouble(loc.getLongitude()), 1);
                //citty = addresses.get(0).getLocality();
                String add = "";
                if (addresses.size() > 0) {
                    loc.setAddress_l1(addresses.get(0).getAddressLine(1));
                    //loc.setAddress_l2(addresses.get(0).getAddressLine(2));
                    loc.setCity(addresses.get(0).getLocality());
                    loc.setCountry(addresses.get(0).getCountryName());
                    loc.setZip(addresses.get(0).getPostalCode());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception w){

            }
            intent.putExtra("location", loc);
            setResult(RESULT_OK, intent);
            finish();
        }


    }


    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
//            return;
        }
//        mMap.setMyLocationEnabled(true);
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);


        //	map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition,15));;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
//        mMap.getUiSettings().setScrollGesturesEnabled(true);
//        mMap.getUiSettings().setTiltGesturesEnabled(true);

        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            if(mapFlag)
                setUpMapForCurrentLocation();
            else
                setUpMap();
        }



    }

  /*  @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }*/

   /* @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();

        }
        back_pressed = System.currentTimeMillis();
    }*/


    private View.OnTouchListener _animListener = new View.OnTouchListener()
    {

        @Override
        public boolean onTouch(final View view, MotionEvent event) {
            switch(event.getAction()) {

                case (MotionEvent.ACTION_DOWN) :
                    view.startAnimation(_animDown);
                    Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(25);
                    return false;
                case (MotionEvent.ACTION_UP):
                    view.startAnimation(_animUp);

                    return false;
                case (MotionEvent.ACTION_CANCEL) :
                    view.startAnimation(_animUp);

            }
            return false;
        }
    };


}
