package com.app.ats.com.myloco;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by abdulla on 19/3/17.
 */

public class Emergency2 extends Activity {


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Button b1, b2, b3, b4;
    TextView t1, t2;
    private String phoneNo;
    private String message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        b1 = (Button) findViewById(R.id.but1);
        b2 = (Button) findViewById(R.id.but2);
        b3 = (Button) findViewById(R.id.but3);
        b4 = (Button) findViewById(R.id.but4);

        t1 = (TextView) findViewById(R.id.num1);
        t2 = (TextView) findViewById(R.id.num2);


        SharedPreferences emer = getSharedPreferences("emergency", MODE_PRIVATE);

        final String abs = emer.getString("num1", "0");
        final String abss = emer.getString("num2", "0");


 if(abs!="0")
     t1.setText(abs);

        if(abss!="0")
            t2.setText(abss);



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + abs));

                if (ActivityCompat.checkSelfPermission(Emergency2.this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(abs);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + abss));

                if (ActivityCompat.checkSelfPermission(Emergency2.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(abss);
            }
        });
    }

    protected void sendSMSMessage(String abss) {
        phoneNo = abss;
        message = "We have an Emergency";

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , abss);
        smsIntent.putExtra("sms_body"  ,message );
        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Emergency2.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


}