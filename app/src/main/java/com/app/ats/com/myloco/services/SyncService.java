package com.app.ats.com.myloco.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.ats.com.myloco.GpsTracker;
import com.app.ats.com.myloco.model.Broadcast;
import com.app.ats.com.myloco.model.LocatorModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by abdulla on 8/3/17.
 */

public class SyncService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 5 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    currentlocation();
                    EventBus.getDefault().postSticky(new Broadcast("syncing"));

                }

            });
        }
}

    public String currentlocation(){
        SharedPreferences sprf = getApplicationContext().getSharedPreferences("tokenpass", 0);
        final String access_token = sprf.getString("token", "0");
        final LocatorModel[] currentLocation = {new LocatorModel()};
        final LocatorModel loc = new LocatorModel();
        final String[] location = new String[1];
        final LocatorModel[] locmodel = new LocatorModel[1];
        GpsTracker gps = new GpsTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            double latitudes = gps.getLatitude();
            double longitudes = gps.getLongitude();
            loc.setLatitude(String.valueOf(latitudes));
            loc.setLongitude(String.valueOf(longitudes));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + loc.getLatitude() + ","
                        + loc.getLongitude() + "&sensor=false&language=en";

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest = new StringRequest(Request.Method.GET, googleMapUrl, new Response.Listener<String>() {




                    @Override
                    public void onResponse(String response) {
                        try {
                            // search for locality and sublocality
                            JSONObject testV = null;
                            try {
                                testV = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray results = null;
                                if ((testV != null ? testV.length() : 0) != 0)
                                    results = testV.getJSONArray("results");
                                String cityName = null;
                                for (int i = 0; i < (results != null ? results.length() : 0); i++) {
                                    JSONObject result = (JSONObject) results.get(i);

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
                                currentLocation[0] = loc;
                                locmodel[0] = new LocatorModel();
                                location[0] =currentLocation[0].getLatitude()+","+currentLocation[0].getLongitude();
                                SharedPreferences ltlng = getSharedPreferences("distance", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = ltlng.edit();
                                editor.clear();
                                editor.putString("lat",currentLocation[0].getLatitude());
                                editor.putString("lng",currentLocation[0].getLongitude());
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                },new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("access_token", access_token);
                        params.put("_method", "0");

                        return params;
                    }
                };
                postRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                500000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                queue.add(postRequest);
            } else {
                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(latitudes, longitudes, 1);
                    if (addresses.size() > 0) {
                        loc.setAddress_l1(addresses.get(0).getAddressLine(1));
                        loc.setCity(addresses.get(0).getLocality());
                        loc.setCountry(addresses.get(0).getCountryName());
                        loc.setZip(addresses.get(0).getPostalCode());
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                currentLocation[0] = loc;
            }

        }
        return location[0];
    }
}