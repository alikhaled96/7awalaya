package group5.projectprototype;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
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


public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Intent i;
    View mapView;
    EditText et1, etcategory;
    Spinner sp;
    int selection;
    int dialog;
    Button btclear;
    String tagtext, tagselect, selectedCategory;
    String MarkerName, Markerinfo, MarkerRatingCount, MarkerAVGRating, MarkerID;
    JSONObject obj;
    JSONArray resultarray;

    //json testing
    List<Marker> markers = new ArrayList<Marker>();
    ArrayList<Integer> est_id = new ArrayList<Integer>();
    ArrayList<String> est_name = new ArrayList<String>();
    ArrayList<String> est_city = new ArrayList<String>();
    ArrayList<String> est_address = new ArrayList<String>();
    ArrayList<Double> est_lat = new ArrayList<Double>();
    ArrayList<Double> est_longt = new ArrayList<Double>();
    ArrayList<Integer> est_reviewcount = new ArrayList<Integer>();
    ArrayList<Double> est_avgrating = new ArrayList<Double>();
    ArrayList<Integer> est_confirmed = new ArrayList<Integer>();
    ArrayList<String> est_image = new ArrayList<String>();

    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        Intent i1 = getIntent();
        selectedCategory = i1.getStringExtra("SELECTED_CATEGORY");
        tagtext = "";
        tagselect = "";
        et1 = (EditText) findViewById(R.id.search1);
        et1.setEnabled(false);
        etcategory = (EditText) findViewById(R.id.categoryselected);
        etcategory.setEnabled(false);
        etcategory.setText(selectedCategory);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            MapActivity.displayPromptForEnablingGPS(this,true);
        } else {
            Toast.makeText(getApplicationContext(),"GPS ready",
                    Toast.LENGTH_LONG).show();
        }

        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(200, 200, 40, 400);

        new Getrequest().execute("http://188.226.144.157/group8/7awlya/get_establishments.php?id=1&email=email_1@email.com&type="+selectedCategory);





        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                //PUT ACTIVITY FOR INFO HERE
                i=new Intent(MapActivity.this , MarkerPage.class);
                MarkerName = marker.getTitle();
                int index = est_name.indexOf(MarkerName);
                Markerinfo = est_address.get(index);
                MarkerAVGRating = est_avgrating.get(index).toString();
                MarkerID = est_id.get(index).toString();
                i.putExtra("MARKER_NAME",MarkerName);
                i.putExtra("MARKER_ADDRESS",Markerinfo);
                i.putExtra("MARKER_AVG",MarkerAVGRating);
             //   i.putExtra("USERPASS" , );
              //  i.putExtra("USEREMAIL" ,);
                i.putExtra("MARKER_ID", MarkerID);
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
                startActivityForResult(i,4);

            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (MarkerInfoActivity.RESULT_OK) : {
                if (resultCode == Activity.RESULT_OK) {
                    MarkerName = data.getStringExtra("RESULTNAME");
                    Markerinfo = data.getStringExtra("RESULTINFO");

                }
                break;
            }
        }
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
                est_avgrating.add(Double.parseDouble(resultarray.getJSONObject(i).get("avg_rating").toString()));
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


