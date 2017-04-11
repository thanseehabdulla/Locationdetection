package com.app.ats.com.myloco;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.ats.com.myloco.model.Broadcast;
import com.app.ats.com.myloco.model.LocatorModel;
import com.app.ats.com.myloco.services.SyncService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class MainActivity3 extends AppCompatActivity implements TextToSpeech.OnInitListener {


    Button b;
    private String la;
    private String ga;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private Button finishh;
    private AudioManager myAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main3);
//startService();
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        b=(Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMappedLocationMap(v);
            }
        });

        finishh=(Button)findViewById(R.id.finish);
        try{
            finishh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


showMappedLocationMap2(v);
//                    finish();
                }
            });}catch (Exception e){

        }
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


    }

    public void showMappedLocationMap(View v) {
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
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
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

        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivityForResult(mapIntent, 2);
    }

    public void showMappedLocationMap2(View v) {
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
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
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

        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivityForResult(mapIntent, 3);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                @SuppressWarnings("RedundantCast") LocatorModel mappedLocation = data.getParcelableExtra("location");
                la = mappedLocation.getLatitude();
                ga = mappedLocation.getLongitude();

                SharedPreferences ltlng2 = getSharedPreferences("libary", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = ltlng2.edit();
                                editor.clear();
                editor.putString("lat", la);
                editor.putString("lng", ga);
                editor.apply();

                String city = mappedLocation.getCity();
                String country = mappedLocation.getCountry();

                Toast.makeText(getApplicationContext(), "Mode Libary destination is selected :  " + mappedLocation.getZip() + "," + mappedLocation.getAddress_l1() + "," + city + "," + country, Toast.LENGTH_LONG).show();


            }
        }
            if (requestCode == 3) {
                if (resultCode == RESULT_OK) {
                    @SuppressWarnings("RedundantCast") LocatorModel mappedLocation = data.getParcelableExtra("location");
                    la = mappedLocation.getLatitude();
                    ga = mappedLocation.getLongitude();

                    SharedPreferences ltlng3 = getSharedPreferences("office", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = ltlng3.edit();
                                editor.clear();
                    editor.putString("lat", la);
                    editor.putString("lng", ga);
                    editor.apply();

                    String city = mappedLocation.getCity();
                    String country = mappedLocation.getCountry();

                    Toast.makeText(getApplicationContext(), "Mode Office destination is selected :  " + mappedLocation.getZip() + "," + mappedLocation.getAddress_l1() + "," + city + "," + country, Toast.LENGTH_LONG).show();


                }
            }


    }
    public void startService() {
        startService(new Intent(getBaseContext(), SyncService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), SyncService.class));
    }


    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        // Set title bar


    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    // This method will be called when a HelloWorldEvent is posted
    public void onEvent(Broadcast event){
//        Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_SHORT).show();
        SharedPreferences ltlng = getSharedPreferences("distance", Context.MODE_PRIVATE);
        String as =ltlng.getString("lat","1");
        String bs =ltlng.getString("lng","1");

        SharedPreferences sppp=getSharedPreferences("office",Context.MODE_PRIVATE);
        String ass =sppp.getString("lat", "0");
        String bss= sppp.getString("lng", "0");

        SharedPreferences sppp2=getSharedPreferences("libary",Context.MODE_PRIVATE);
        String asss =sppp2.getString("lat", "0");
        String bsss= sppp2.getString("lng", "0");

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

//        Toast.makeText(getApplicationContext(),"Destination tracking for "+as+bs,Toast.LENGTH_SHORT).show();
        if(as.equals(ass) && bs.equals(bss)){
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(getApplicationContext(),"Office Reached",Toast.LENGTH_SHORT).show();
//            speakWords("You have Reached Destination Office");
        }else if(as.equals(asss) && bs.equals(bsss)) {
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(getApplicationContext(), "Libary Reached", Toast.LENGTH_SHORT).show();
//        speakWords("You have Reached Destination Libary");
        }else
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    //setup TTS
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    //speak the user text
    private void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
