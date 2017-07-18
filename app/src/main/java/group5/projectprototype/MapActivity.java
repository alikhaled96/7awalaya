package group5.projectprototype;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static group5.projectprototype.R.id.map;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Intent i;
    View mapView;
    EditText et1,ettag,tagdata;
    Spinner sp;
    int selection;
    int dialog;
    Button btclear;
    String tagtext,tagselect,responseFromserver;
    String MarkerName,Markerinfo,MarkerRatingCount,MarkerAVGRating;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    JSONObject obj ;
    JSONArray resultarray ;
    //json testing
    List<Marker> markers = new ArrayList<Marker>();
    ArrayList<Integer> est_id = new ArrayList<Integer>();
    ArrayList<String> est_name = new ArrayList<String>();
    ArrayList<String> est_city = new ArrayList<String>();
    ArrayList<String> est_address = new ArrayList<String>();
    ArrayList<Double> est_lat = new ArrayList<Double>();
    ArrayList<Double> est_longt = new ArrayList<Double>();
    ArrayList<Integer> est_reviewcount= new ArrayList<Integer>();
    ArrayList<Double> est_avgrating = new ArrayList<Double>();
    ArrayList<Integer> est_confirmed = new ArrayList<Integer>();
    ArrayList<String> est_image = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
       // new GetMethodDemo().execute("http://188.226.144.157/group8/7awlya/get_establishments.php?id=1&email=email_1@email.com&type=Pharmacy");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        tagtext="";
        tagselect="";
        et1 = (EditText) findViewById(R.id.search1);
        et1.setEnabled(false);
       // ettag = (EditText) findViewById(R.id.tag);
//        ettag.setEnabled(false);
        tagdata = (EditText) findViewById(R.id.tagset);
        tagdata.setEnabled(false);
        //btclear = (Button) findViewById(R.id.btclear);
       // btclear.setVisibility(View.GONE);
        sp = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);



        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //et1.setText(parentView.getItemAtPosition(position).toString());
                tagselect = parentView.getItemAtPosition(position).toString();
                selection = position;
                if(tagtext.contains(tagselect) || tagselect.equals("select category")){

                }else {
                    tagtext = tagselect + "," ;
                }
                tagdata.setText(tagdata.getText()+tagtext);
//                btclear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                et1.setText("");

            }

        });


    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng sydney = new LatLng(30.017941, 31.500269);
        mMap.addMarker(new MarkerOptions().position(sydney).title("AUC").snippet("i hate this place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f));
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            MapActivity.displayPromptForEnablingGPS(this,true);

        } else {
            Toast.makeText(getApplicationContext(),"GPS ready",
                    Toast.LENGTH_LONG).show();
        }


        new Getrequest().execute("http://188.226.144.157/group8/7awlya/get_establishments.php?id=1&email=email_1@email.com&type=Pharmacy");





        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                //PUT ACTIVITY FOR INFO HERE
                i=new Intent(MapActivity.this , MarkerPage.class);
                //int test = arg0.getId();
                MarkerName = arg0.getTag().toString();
                Markerinfo = arg0.getSnippet().toString();
                int s = est_name.indexOf(MarkerName);
                MarkerAVGRating = est_avgrating.get(s).toString();

                i.putExtra("MARKER_NAME",MarkerName);
                i.putExtra("MARKER_ADDRESS",Markerinfo);
                i.putExtra("MARKER_AVG",MarkerAVGRating);

                startActivity(i);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Toast.makeText(getApplicationContext(),"time to make a new marker",
                        Toast.LENGTH_LONG).show();
                i=new Intent(MapActivity.this , MarkerInfoActivity.class);
                double lat = latLng.latitude;
                double longt = latLng.longitude;
                i.putExtra("LAT",lat);
                i.putExtra("LONGT",longt);
                startActivity(i);

                mMap.addMarker(new MarkerOptions()
                       .position(latLng)
                        .title(MarkerName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        });


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface OnTaskDoneListener {
        void onTaskDone(String responseData);

        void onError();
    }

    public class Getrequest extends AsyncTask<String , Void ,String> {
     String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //urlConnection.disconnect();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processResult(server_response);
        }

    }
    private void processResult(String test)
    {
        try {
            obj = new JSONObject(test);
            resultarray = obj.getJSONArray("results");
            for(int i=0 ; i<resultarray.length() ; i++){
                est_id.add(Integer.parseInt(resultarray.getJSONObject(i).get("id").toString()));
                est_name.add(resultarray.getJSONObject(i).get("name").toString());
                est_city.add(resultarray.getJSONObject(i).get("city").toString());
                est_address.add(resultarray.getJSONObject(i).get("address").toString());
                est_lat.add(Double.parseDouble(resultarray.getJSONObject(i).get("lat").toString()));
                est_longt.add(Double.parseDouble(resultarray.getJSONObject(i).get("lng").toString()));
                est_reviewcount.add(Integer.parseInt(resultarray.getJSONObject(i).get("reviews_count").toString()));
                est_avgrating.add(Double.parseDouble(resultarray.getJSONObject(i).get("lat").toString()));
                est_confirmed.add(Integer.parseInt(resultarray.getJSONObject(i).get("confirmed").toString()));
                //est_image.add(i,resultarray.getJSONObject(i).getInt(""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0;i<est_name.size();i++) {

            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(est_lat.get(i), est_longt.get(i))).title(est_name.get(i)).snippet(est_address.get(i)));

            markers.add(marker);


        }
    }
// Converting InputStream to String

    public String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }





    public static void displayPromptForEnablingGPS(
            final Activity activity,boolean d)
    {
        if(d) {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(activity);
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Enable either GPS or any other location"
                    + " service to find current location.  Click OK to go to"
                    + " location services settings to let you do so.";

            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    activity.startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
            builder.create().show();

        }

    }






}


