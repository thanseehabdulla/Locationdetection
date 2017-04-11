package com.app.ats.com.myloco;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.ats.com.myloco.model.Broadcast;
import com.app.ats.com.myloco.services.SyncService;

import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by abdulla on 19/3/17.
 */

public class HomeActivity extends Activity implements TextToSpeech.OnInitListener{

    Button setAlarm,changeAlarm,emergency,viewcall,modecall;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private AudioManager myAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.home_main);
        startService();
        setAlarm=(Button)findViewById(R.id.button2);
        changeAlarm=(Button)findViewById(R.id.button3);
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        emergency=(Button)findViewById(R.id.button4);
        viewcall=(Button)findViewById(R.id.button5);
        modecall=(Button)findViewById(R.id.button6);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);


            }
        });


        changeAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(i);
            }
        });


        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences emer=getSharedPreferences("emergency",MODE_PRIVATE);
                String asss=emer.getString("num1", "0");
                String bsss=emer.getString("num2", "0");
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.listview_popup, popup.getMenu());
               popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       int i = item.getItemId();
                       if(i==R.id.edit){
                           Intent inn = new Intent(getApplicationContext(),Emergency.class);
                           startActivity(inn);
                       }else if(i==R.id.delete) {
                           Intent innn = new Intent(getApplicationContext(), Emergency2.class);
                           startActivity(innn);
                       }


                       return false;
                   }
               });
                popup.show();


                }
        });


        viewcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),MapActivity2.class);
                startActivity(i);

            }
        });

   modecall.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           Intent i = new Intent(getApplicationContext(),MainActivity3.class);
           startActivity(i);
       }
   });




    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                try {
                    myTTS = new TextToSpeech(this, this);
                }catch (Exception e){

                }
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }




    }
    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        // Set title bar


    }
    public void startService() {
        startService(new Intent(getBaseContext(), SyncService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), SyncService.class));
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
        String as =ltlng.getString("lat","0");
        String bs =ltlng.getString("lng","0");

        SharedPreferences sppp=getSharedPreferences("destination",MODE_PRIVATE);
        String ass =sppp.getString("ltg", "0");
        String bss= sppp.getString("lng", "0");
        String asss =sppp.getString("city", "0");
        String bsss= sppp.getString("country", "0");
        String assss =sppp.getString("road", "0");
        String bssss= sppp.getString("road2", "0");

        SharedPreferences sppp2=getSharedPreferences("office",Context.MODE_PRIVATE);
        String ass2 =sppp2.getString("lat", "0");
        String bss2= sppp2.getString("lng", "0");

        SharedPreferences sppp22=getSharedPreferences("libary",Context.MODE_PRIVATE);
        String asss2 =sppp22.getString("lat", "0");
        String bsss2= sppp22.getString("lng", "0");

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

//        Toast.makeText(getApplicationContext(),"Destination tracking for "+as+bs,Toast.LENGTH_SHORT).show();
        if(as.equals(ass2) && bs.equals(bss2)){
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            Toast.makeText(getApplicationContext(),"Office Reached",Toast.LENGTH_SHORT).show();
//            speakWords("You have Reached Destination Office");
        }else if(as.equals(asss2) && bs.equals(bsss2)) {
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            Toast.makeText(getApplicationContext(), "Libary Reached", Toast.LENGTH_SHORT).show();
//        speakWords("You have Reached Destination Libary");
        }else
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


//        Toast.makeText(getApplicationContext(),"Destination tracking for "+as+bs,Toast.LENGTH_SHORT).show();
        if(as.equals(ass) && bs.equals(bss)){
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

        SharedPreferences sppp=getSharedPreferences("destination",MODE_PRIVATE);
        SharedPreferences.Editor editor = sppp.edit();
        editor.clear();
        editor.apply();
        super.onBackPressed();

    }
}
