package com.app.ats.com.myloco;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ats.com.myloco.model.Broadcast;
import com.app.ats.com.myloco.model.LocatorModel;
import com.app.ats.com.myloco.services.SyncService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {


    Button b;
    private String la;
    private String ga;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private RecyclerView recyclerView;
    private List<Movie> movieList = new ArrayList<>();
    private MoviesAdapter mAdapter;
EditText edd;
    Button bc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
//startService();
    b=(Button)findViewById(R.id.button);
        bc=(Button)findViewById(R.id.clear);
        bc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences emer2=getSharedPreferences("list",MODE_PRIVATE);
                SharedPreferences.Editor editor2 = emer2.edit();
            editor2.clear();
                editor2.apply();
              Toast.makeText(getApplicationContext(),"Deleted in background",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ab="";
                if(!edd.equals("")){
                 ab=edd.getText().toString();
                SharedPreferences sppp=getSharedPreferences("destination",MODE_PRIVATE);
                SharedPreferences.Editor editor = sppp.edit();
                editor.clear();
                editor.putString("task", ab);


                editor.apply();
                }
                showMappedLocationMap(v);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
edd=(EditText)findViewById(R.id.task);
        mAdapter = new MoviesAdapter(movieList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();


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
           la=    mappedLocation.getLatitude();
                 ga = mappedLocation.getLongitude();
              SharedPreferences sppp=getSharedPreferences("destination",MODE_PRIVATE);
                SharedPreferences.Editor editor = sppp.edit();
//editor.clear();
                editor.putString("ltg", la);
                editor.putString("lng", ga);
                editor.putString("city", mappedLocation.getCity());
                editor.putString("country", mappedLocation.getCountry());
                editor.putString("road", mappedLocation.getAddress_l1());
                editor.putString("road2", mappedLocation.getZip());
                editor.apply();
              String task = sppp.getString("task","0");
                SharedPreferences emer2=getSharedPreferences("list",MODE_PRIVATE);

              String snnn=emer2.getString("num1","0");

                SharedPreferences.Editor editor2 = emer2.edit();
StringBuffer sb =new StringBuffer();
                sb.append(mappedLocation.getAddress_l1()+"-"+mappedLocation.getCity()+"- Task:"+task);
                sb.append(",");
                if(snnn!="0"){
                    sb.append(snnn);
                    sb.append(",");
                }
                editor2.putString("num1", String.valueOf(sb));
                editor2.apply();

                String city=mappedLocation.getCity();
                String country=mappedLocation.getCountry();

Toast.makeText(getApplicationContext(),"destination is selected :  "+mappedLocation.getZip()+","+mappedLocation.getAddress_l1()+","+city+","+country,Toast.LENGTH_LONG).show();





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
        mAdapter.prepareMovieData();
        SharedPreferences sppp=getSharedPreferences("destination",MODE_PRIVATE);
        String ass =sppp.getString("ltg", "0");
        String bss= sppp.getString("lng", "0");
        String asss =sppp.getString("city", "0");
        String bsss= sppp.getString("country", "0");
        String assss =sppp.getString("road", "0");
        String bssss= sppp.getString("road2", "0");


//        Toast.makeText(getApplicationContext(),"Destination tracking for "+as+bs,Toast.LENGTH_SHORT).show();
        if(as.equals(ass)&& bs.equals(bss)){
            Toast.makeText(getApplicationContext(),"destination Reached",Toast.LENGTH_SHORT).show();
            if(assss!="0" && bssss!="0" && asss!="0" && bsss!="0")
            speakWords("You have Reached Destination"+ assss+bssss+ asss +bsss);
        else if(assss=="0" && bssss!="0" && asss!="0" && bsss!="0")
                speakWords("You have Reached Destination"+bssss+ asss +bsss);
            else if(assss=="0" && bssss=="0" && asss!="0" && bsss!="0")
                speakWords("You have Reached Destination" +asss +bsss);
            else if(asss!="0")
                speakWords("You have Reached Destination" +asss );
            else if(bsss!="0")
                speakWords("You have Reached Destination" +bsss );
        }
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


    private void prepareMovieData() {
        SharedPreferences emer2=getSharedPreferences("list",MODE_PRIVATE);

        String snnn=emer2.getString("num1","0");
if(snnn!="0"){
    String[] numbers = snnn.split(",");
    final String[] mobile = new String[numbers.length];

    for (int i = 0; i < numbers.length; i++) {
        mobile[i] = (numbers[i]);
        Movie movie = new Movie(mobile[i], "Task initiated", "MyLoco");
        movieList.add(movie);
    }

}
        mAdapter.notifyDataSetChanged();
    }

}
