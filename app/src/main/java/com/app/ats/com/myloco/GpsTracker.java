package com.app.ats.com.myloco;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GpsTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GpsTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==mContext.checkSelfPermission(perm));
        }
        return true;
    }
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            this.canGetLocation = true;
            // First if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // Then get location from Network Provider
            if (isNetworkEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e){

        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GpsTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*public LocatorModel fetchCityNameUsingGoogleMap(final TextView view, final LocatorModel loc)
    {
        String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + loc.getLatitude() + ","
                + loc.getLongitude() + "&sensor=false&language=en";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(googleMapUrl, new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers,JSONObject json) {
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
                                        if ("locality".equals(types.getString(k)) && cityName == null) {
                                            if (addressComponent.has("long_name")) {
                                                cityName = addressComponent.getString("long_name");
                                            } else if (addressComponent.has("short_name")) {
                                                cityName = addressComponent.getString("short_name");
                                            }
                                        }
                                        if ("sublocality".equals(types.getString(k))) {
                                            if (addressComponent.has("long_name")) {
                                                cityName = addressComponent.getString("long_name");
                                            } else if (addressComponent.has("short_name")) {
                                                cityName = addressComponent.getString("short_name");
                                            }
                                        }
                                        if ("country".equals(types.getString(k))) {
                                            if (addressComponent.has("long_name")) {
                                                loc.setCountry(addressComponent.getString("long_name"));
                                            } else if (addressComponent.has("short_name")) {
                                                loc.setCountry(addressComponent.getString("short_name"));
                                            }
                                        }
                                        if ("formatted_address".equals(types.getString(k))) {
                                            if (addressComponent.has("long_name")) {
                                                loc.setAddress_l1(addressComponent.getString("long_name"));
                                            } else if (addressComponent.has("short_name")) {
                                                loc.setAddress_l1(addressComponent.getString("short_name"));
                                            }
                                        }
                                    }
                                }
                            }
                            if (cityName != null) {
                                if(view != null)
                                    view.setText(cityName);
                                loc.setCity(cityName);
                            }
                        }
                    }
                }catch(JSONException e){

                }
            }
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject  error) {
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });

        return loc;
    }*/

}
