package com.rohan.flash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AutoCompleteTextView hosName,ambId,ambReg,ambDriver;
    String Response = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        preferences = getSharedPreferences("Flash", Context.MODE_PRIVATE);
        editor = preferences.edit();

        hosName = (AutoCompleteTextView) findViewById(R.id.hospitalName);
        ambId = (AutoCompleteTextView) findViewById(R.id.ambulanceId);
        ambReg = (AutoCompleteTextView) findViewById(R.id.ambulanceRegNo);
        ambDriver = (AutoCompleteTextView) findViewById(R.id.ambulanceDriver);
    }

    public void signUp(View view) {
        if (hosName.getText().toString().equals("")) {
            hosName.setError("Please enter hospital name");
            hosName.requestFocus();
            return;
        }
        if (ambId.getText().toString().equals("")) {
            ambId.setError("Please enter ambulance id");
            ambId.requestFocus();
            return;
        }
        if (ambReg.getText().toString().equals("")) {
            ambReg.setError("Please enter ambulance registration number");
            ambReg.requestFocus();
            return;
        }
        if (ambDriver.getText().toString().equals("")) {
            ambDriver.setError("Please enter ambulance driver");
            ambDriver.requestFocus();
            return;
        }

        editor.putString("hosName",hosName.getText().toString());
        editor.putString("ambId",ambId.getText().toString());
        editor.putString("ambReg",ambReg.getText().toString());
        editor.putString("ambDriver",ambDriver.getText().toString());
        editor.apply();
//        if everything is fine then send data to server db
        new SendData(hosName.getText().toString(), ambId.getText().toString(), ambReg.getText().toString(),ambDriver.getText().toString()).execute();


    }

    public class SendData extends AsyncTask<Void, Void, Void> {

        String name, id, reg,driver;

        SendData(String name, String aId, String num,String dr) {
            this.name = name;
            this.id = aId;
            this.reg = num;
            this.driver = dr;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            editor.putInt("LOGGED_IN",1);
            editor.apply();
            startActivity(new Intent(LoginActivity.this, Main2Activity.class));
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL("http://204.152.203.111/ec/ambulance_signup.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder();
                    builder.appendQueryParameter("hospName",name);
                    builder.appendQueryParameter("ambId",id);
                    builder.appendQueryParameter("ambReg",reg);
                    builder.appendQueryParameter("ambDriver",driver);


                String query = builder.build().getEncodedQuery();

                OutputStream os = httpURLConnection.getOutputStream();

                BufferedWriter mBufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                mBufferedWriter.write(query);
                mBufferedWriter.flush();
                mBufferedWriter.close();
                os.close();

                httpURLConnection.connect();
                BufferedReader mBufferedInputStream = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inline;
                while ((inline = mBufferedInputStream.readLine()) != null) {
                    Response += inline;
                }
                mBufferedInputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("Response", Response);


            return null;
        }

   }

}