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
        new Getrequest1().execute("http://188.226.144.157/group8/7awlya/get_types.php?id=1&email=email_1@email.com");

        spselect= (Spinner) findViewById(R.id.spinnerselect);

        submit = (Button) findViewById(R.id.Submit);
        nameet = (EditText) findViewById(R.id.Nameselect);
        quickinfoet = (EditText) findViewById(R.id.quickinfo);
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
                } else if (quickinfoet.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Add an Address",
                            Toast.LENGTH_LONG).show();
                } else if (selected_category=="Select Category"){
                    Toast.makeText(getApplicationContext(),"Please Select a Category",
                            Toast.LENGTH_LONG).show();
                } else {

                    Name = nameet.getText().toString();
                    quickInfo = quickinfoet.getText().toString();
                    new SendPostRequest().execute();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULTNAME",Name);
                    resultIntent.putExtra("RESULTINFO",quickInfo);
                    setResult(MarkerInfoActivity.RESULT_OK, resultIntent);
                    finish();

                }

            }
        });
    }

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


    //API WORK
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://188.226.144.157/group8/7awlya/add_establishment.php"); // here is your URL path
                Double test1 = Math.round (lat * 100000.0) / 100000.0;
                Double test2 = Math.round (longt * 100000.0) / 100000.0;
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pw","pw08");
                postDataParams.put("id","1");
                postDataParams.put("email","email_1@email.com");
                postDataParams.put("est_name",Name);
                postDataParams.put("est_type",selected_category);
                postDataParams.put("est_addr",quickInfo);
                postDataParams.put("est_lat",String.valueOf(test1));
                postDataParams.put("est_lng",String.valueOf(test2));

                Log.e("params",postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "you can find your marker in the general category",
                    Toast.LENGTH_LONG).show();
            Log.e("output" , result);
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
