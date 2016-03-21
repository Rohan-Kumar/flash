package com.rohan.flash;

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
    //    String name, email, id, mobile;
    AutoCompleteTextView phone, name, email;
    String Response = "";
    static ArrayList<LatLng> route =  new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        Log.d("values", getLocationFromAddress("Ulsoor") + "");
    }

    private void init() {
        name = (AutoCompleteTextView) findViewById(R.id.userName);
        email = (AutoCompleteTextView) findViewById(R.id.emailID);
        phone = (AutoCompleteTextView) findViewById(R.id.phoneNumber);
    }

    public void signUp(View view) {
//        checking if name is not entered
        if (name.getText().toString().equals("")) {
            name.setError("Please enter your name");
            name.requestFocus();
            return;
        }
//        checking if email is not entered
        if (email.getText().toString().equals("")) {
            email.setError("Please enter your email id");
            email.requestFocus();
            return;
        }
//        checking if phone number is not entered
        if (phone.getText().toString().equals("")) {
            phone.setError("Please enter your phone number");
            phone.requestFocus();
            return;
        }
//        checking if phone number is valid (greater than 10 digits)
        if (phone.getText().toString().length() < 10) {
            phone.setError("Please enter a valid phone number");
            phone.requestFocus();
            return;
        }
//        checking if email is valid (if it contains '@' symbol)
        if (!email.getText().toString().contains("@")) {
            email.setError("Please enter a valid email id");
            email.requestFocus();
            return;
        }
//        if everything is fine then send data to server db
        new SendData(name.getText().toString(), email.getText().toString(), phone.getText().toString()).execute();


    }

    public class SendData extends AsyncTask<Void, Void, Void> {

        String name, email, phone;

        SendData(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(LoginActivity.this,MapsActivity.class));
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=kumarswamy%20layout,Bangalore&destination=kathriguppe,Bangalore&key=%20AIzaSyDm0xyQGJ1mDIMezQZxpUjGbtadDpuhdiU");
//https://maps.googleapis.com/maps/api/directions/json?origin=12.45,77.45&destination=12.55,77.85&key=%20AIzaSyDm0xyQGJ1mDIMezQZxpUjGbtadDpuhdiU
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setRequestMethod("POST");

//                Uri.Builder builder = new Uri.Builder();
//                    builder.appendQueryParameter("name",name);
//                    builder.appendQueryParameter("email",email);
//                    builder.appendQueryParameter("phone",phone);


//                String query = builder.build().getEncodedQuery();

//                OutputStream os = httpURLConnection.getOutputStream();

//                BufferedWriter mBufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                mBufferedWriter.write(query);
//                mBufferedWriter.flush();
//                mBufferedWriter.close();
//                os.close();

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

            parseResponse(Response);

            return null;
        }

        private void parseResponse(String response) {


            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject jsonObject1 = routesArray.getJSONObject(0);
                JSONArray legsArray = jsonObject1.getJSONArray("legs");
                JSONObject jsonObject2 = legsArray.getJSONObject(0);
                JSONArray steps = jsonObject2.getJSONArray("steps");
                for (int i = 0; i < steps.length(); i++) {
                    JSONObject jsonObject3 = steps.getJSONObject(i);
                    JSONObject loc = jsonObject3.getJSONObject("end_location");
                    double lat = loc.getDouble("lat");
                    double lon = loc.getDouble("lng");
                    route.add(new LatLng(lat,lon));
                    JSONObject loc2 = jsonObject3.getJSONObject("start_location");
                    lat = loc2.getDouble("lat");
                    lon = loc2.getDouble("lng");
//                    Log.d("test", "lat " + lat + " lon " + lon);
                    route.add(new LatLng(lat,lon));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            Log.d("what", "a:" + address);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((location.getLatitude()),
                    (location.getLongitude()));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}