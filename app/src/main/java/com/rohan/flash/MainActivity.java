package com.rohan.flash;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView from,to;
    public static LatLng f,t;
    public static int pos = 0;
    Spinner spinner;
    public static String waypoints[] = {"","&waypoints=12.917635,77.623336","&waypoints=12.917180,77.573923","&waypoints=12.921874,77.560160","&waypoints=12.916728,77.609765"};
    List<String> categories = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        from = (AutoCompleteTextView) findViewById(R.id.from);
        to = (AutoCompleteTextView) findViewById(R.id.to);

        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categories.add("Select your waypoint");
        categories.add("Silk board");
        categories.add("Banashankri");
        categories.add("DG signal");
        categories.add("Udpi garden");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);



    }

    public void route(View view) {
        if (from.getText().toString().isEmpty()){
            from.setError("Cannot be empty");
            from.requestFocus();
            return;
        }
        if (to.getText().toString().isEmpty()){
            to.setError("Cannot be empty");
            to.requestFocus();
            return;
        }
        f=getLocationFromAddress(from.getText().toString());
        t=getLocationFromAddress(to.getText().toString());
        if (f==null){
            from.setError("Cannot find this address");
            from.requestFocus();
            return;
        }
        if (t==null){
            to.setError("Cannot find this address");
            to.requestFocus();
            return;
        }

        startActivity(new Intent(MainActivity.this,MapsActivity.class));
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
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
        catch (IndexOutOfBoundsException e){
            Toast.makeText(MainActivity.this, "Cannot find address.. Please try again", Toast.LENGTH_SHORT).show();
            return null;

        }
    }
}
