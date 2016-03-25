package com.rohan.flash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class OTPActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AutoCompleteTextView otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        preferences = getSharedPreferences("Flash", Context.MODE_PRIVATE);
        editor=preferences.edit();
        otp = (AutoCompleteTextView) findViewById(R.id.otp);
    }

    public void otp(View view) {
        if (otp.getText().toString().equals("108")||otp.getText().toString().equals("110")){
            editor.putInt("LOGGED_IN",2);
            editor.apply();
            startActivity(new Intent(OTPActivity.this, Main2Activity.class));
            finish();
        }
        else {
            otp.setError("Wrong otp");

        }
    }
}
