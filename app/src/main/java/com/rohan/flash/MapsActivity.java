package com.rohan.flash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    static LocationRequest locationRequest;
    static GoogleApiClient googleApiClient;
    Location loc;
    String Response = "";
    List<List<HashMap<String, String>>> result;
    ArrayList<LatLng> signals = new ArrayList<>();
    double overall_dist=0,signal_to_final=0;
    String direction = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        connectToGoogleApi();

        signals.add(new LatLng(0, 0));
        signals.add(new LatLng(12.91763, 77.62333));
        signals.add(new LatLng(12.91718, 77.57392));
        signals.add(new LatLng(12.92187, 77.56016));
        signals.add(new LatLng(12.91672, 77.60976));

        overall_dist = distance(MainActivity.f.latitude,MainActivity.f.longitude,MainActivity.t.latitude,MainActivity.t.longitude);
        signal_to_final = distance(signals.get(MainActivity.pos).latitude,signals.get(MainActivity.pos).longitude,MainActivity.t.latitude,MainActivity.t.longitude);


        new getRoute().execute();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (distance(loc.getLatitude(),loc.getLongitude(),MainActivity.t.latitude,MainActivity.t.longitude)<signal_to_final){
                    // send stop
                }
                else if (distance(loc.getLatitude(),loc.getLongitude(),signals.get(MainActivity.pos).latitude,signals.get(MainActivity.pos).longitude)<0.5){
                    // sending to alter the signal
                    if (direction.contains("E")){

                    }
                    else if (direction.contains("W")){

                    }
                    else if (direction.contains("N")){

                    }
                    else if (direction.contains("S")){

                    }

                }

            }
        }, 1000, 5000);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(MainActivity.f, 10);
        mMap.animateCamera(yourLocation);


//        GoogleDirection.withServerKey("AIzaSyDm0xyQGJ1mDIMezQZxpUjGbtadDpuhdiU")
//                .from(MainActivity.f)
//                .to(MainActivity.t)
//                .alternativeRoute(true)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
//                        mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.RED));
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//
//                    }
//                });
    }

    public class getRoute extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            try {
                url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + MainActivity.f.latitude + "," + MainActivity.f.longitude + "&destination=" + MainActivity.t.latitude + "," + MainActivity.t.longitude + MainActivity.waypoints[MainActivity.pos] + "&key=AIzaSyDm0xyQGJ1mDIMezQZxpUjGbtadDpuhdiU");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                httpURLConnection.connect();
                BufferedReader mBufferedInputStream = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inline;
                while ((inline = mBufferedInputStream.readLine()) != null) {
                    Response += inline;
                }
                mBufferedInputStream.close();
                Log.d("response", Response);
                result = parse(new JSONObject(Response));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Response = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(MapsActivity.this, "" + result.size(), Toast.LENGTH_SHORT).show();

            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }

            int sig_point=10;
            for (int x = 0; x < points.size(); x++) {
                Log.d("testing",points.get(x)+"\n");
                if (MainActivity.pos != 0)
                    if ((Math.abs(points.get(x).latitude - signals.get(MainActivity.pos).latitude)<0.0001)&&(Math.abs(points.get(x).longitude-signals.get(MainActivity.pos).longitude)<0.0001)){
                        sig_point = x;
                        Log.d("found location","Got the signal "+points.get(x));
                    }
            }

            direction = bearing(points.get(sig_point - 5).latitude, points.get(sig_point - 5).longitude, signals.get(MainActivity.pos).latitude, signals.get(MainActivity.pos).longitude);

            Toast.makeText(MapsActivity.this, direction, Toast.LENGTH_SHORT).show();

            mMap.addMarker(new MarkerOptions().position(points.get(sig_point - 5)));
            mMap.addMarker(new MarkerOptions().position(points.get(sig_point + 5)));


            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    protected static String bearing(double lat1, double lon1, double lat2, double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        double resultDegree= (Math.toDegrees(Math.atan2(y, x))+360)%360;
        String coordNames[] = {"N","NNE", "NE","ENE","E", "ESE","SE","SSE", "S","SSW", "SW","WSW", "W","WNW", "NW","NNW", "N"};
        double directionid = Math.round(resultDegree / 22.5);
        // no of array contain 360/16=22.5
        if (directionid < 0) {
            directionid = directionid + 16;
            //no. of contains in array
        }
        String compasLoc=coordNames[(int) directionid];

        return compasLoc;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

            dist = dist * 1.609344;

        return (dist);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }




    public void connectToGoogleApi() {

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
                Log.d("TAG", "connect");
            }
        } else {
            Log.e("TAG", "unable to connect to google play services.");
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "connected");
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        loc = location;

                    }
                });
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        JSONObject inRoutes;
        JSONObject inLegs;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                inRoutes = jRoutes.getJSONObject(i);
                jLegs = inRoutes.getJSONArray("legs");
                ArrayList path = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    inLegs = jLegs.getJSONObject(j);
                    jSteps = inLegs.getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {

//                        String html_instructions = jSteps.getJSONObject(k).getString("html_instructions");
//                        String travel_mode = jSteps.getJSONObject(k).getString("travel_mode");
//                        String maneuver = jSteps.getJSONObject(k).getString("maneuver");
//
//                        String distance_text = jSteps.getJSONObject(k).getJSONObject("distance").getString("text");
//                        String distance_value = jSteps.getJSONObject(k).getJSONObject("distance").getString("value");
//
//                        String duration_text = jSteps.getJSONObject(k).getJSONObject("duration").getString("text");
//                        String duration_value = jSteps.getJSONObject(k).getJSONObject("duration").getString("value");
//
//                        String start_lat = jSteps.getJSONObject(k).getJSONObject("start_location").getString("lat");
//                        String start_lon = jSteps.getJSONObject(k).getJSONObject("start_location").getString("lng");
//
//                        String end_lat = jSteps.getJSONObject(k).getJSONObject("end_location").getString("lat");
//                        String end_lon = jSteps.getJSONObject(k).getJSONObject("end_location").getString("lng");

                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);


                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return routes;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
