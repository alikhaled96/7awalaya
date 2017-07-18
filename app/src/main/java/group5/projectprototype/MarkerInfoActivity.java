package group5.projectprototype;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class MarkerInfoActivity extends AppCompatActivity {
Spinner spselect;
    Intent i;
    String Name,Phone,quickInfo;
    Button submit;
    EditText nameet,phet,quickinfoet;
    String selected_category;
    double lat,longt;
    ArrayList<String> types = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);
        new Getrequest1().execute("188.226.144.157/group8/7awlya/get_types.php?id=1&email=email_1@email.com");

        spselect= (Spinner) findViewById(R.id.spinnerselect);

        submit = (Button) findViewById(R.id.Submit);
        nameet = (EditText) findViewById(R.id.Nameselect);
        phet = (EditText) findViewById(R.id.Phoneselect);
        quickinfoet = (EditText) findViewById(R.id.quickinfo);
        Intent marker = getIntent();
        Bundle extras = getIntent().getExtras();
        lat = extras.getDouble("LAT");
        longt = extras.getDouble("LONGT");

        spselect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selected_category = parentView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameet.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Add a Name ",
                            Toast.LENGTH_LONG).show();
                } else if (phet.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Add a Phone Number",
                            Toast.LENGTH_LONG).show();
                } else if (selected_category=="Select Category"){
                    Toast.makeText(getApplicationContext(),"Please Select a Category",
                            Toast.LENGTH_LONG).show();
                } else {

                    Name = nameet.getText().toString();
                    Phone = phet.getText().toString();
                    quickInfo = quickinfoet.getText().toString();

                    Intent data = new Intent();
                    data.putExtra("MARKERNAME", Name);
                    //data.putExtra("n2", disp.getText().toString());
                    setResult(RESULT_OK, data);
                    //startActivity(i);
                    finish();
                }

            }
        });
    }

/*
   "pw": "pw09",
    "id": "99",
    “email” : “user99@email.com”,
    “est_name” : “Am Ahmed”,
    “est_type” : “Koshk”,
    “est_addr” : “9 share3 kaza”,
    “est_lat” : “123.11”,
    “est_lng” : “-99.2”
  */
    //API WORK
    public class Getrequest1 extends AsyncTask<String , Void ,String> {
        String server_response1;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response1 = readStream(urlConnection.getInputStream());

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("resp" , server_response1);
            processResult(server_response1);

        }

    }
    private void processResult(String test)
    {
        try {
            JSONObject obj = new JSONObject(test);
            JSONArray resultarray = obj.getJSONArray("results");

            for(int i=0 ; i<resultarray.length() ; i++){
               types.add(resultarray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                types );

        spselect.setAdapter(arrayAdapter);

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


}
