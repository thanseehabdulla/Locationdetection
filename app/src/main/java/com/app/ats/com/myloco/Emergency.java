package com.app.ats.com.myloco;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by abdulla on 19/3/17.
 */

public class Emergency extends Activity {

    Button b;
    EditText e1,e2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.emergency_main);
e1=(EditText)findViewById(R.id.editText2);
        e2=(EditText)findViewById(R.id.editText3);
        SharedPreferences emer = getSharedPreferences("emergency", MODE_PRIVATE);

        final String abs = emer.getString("num1", "0");
        final String abss = emer.getString("num2", "0");

        if(abs!="0")
            e1.setText(abs);

        if(abss!="0")
            e2.setText(abss);


        b=(Button)findViewById(R.id.save);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=e1.getText().toString();
                String s2=e2.getText().toString();
SharedPreferences emer=getSharedPreferences("emergency",MODE_PRIVATE);
                SharedPreferences.Editor editor = emer.edit();

                editor.putString("num1", s);
                editor.putString("num2", s2);
                editor.apply();
                finish();

            }
        });

    }
}
