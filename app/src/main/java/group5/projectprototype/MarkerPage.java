package group5.projectprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class MarkerPage extends AppCompatActivity {
Button reviewbt , reportbt;
    EditText est_name , est_address;
String Name,Address,Avg_rating,est_id,useremail,userpass,userid;
    Integer pos_neg;
    int ratingset;
    RatingBar rb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_page);
        reportbt = (Button) findViewById(R.id.reportbutton);
        reviewbt = (Button) findViewById(R.id.reviewbutton);
        est_address = (EditText) findViewById(R.id.marker_address);
        est_name = (EditText) findViewById(R.id.marker_name);
        rb = (RatingBar) findViewById(R.id.ratingBar);
        Intent marker = getIntent();
        Bundle extras = getIntent().getExtras();
        Name = extras.getString("MARKER_NAME");
        Address = extras.getString("MARKER_ADDRESS");
        Avg_rating = extras.getString("MARKER_AVG");
        est_id = extras.getString("MARKER_ID");
        est_name.setEnabled(false);
        est_address.setEnabled(false);
        est_name.setText(Name);
        est_address.setText(Address);
        ratingset = (int) (Math.round(Double.parseDouble(Avg_rating)) * 2);
        Log.e("rating" , Avg_rating);
        rb.setProgress(ratingset);

        useremail = getSharedPreferences("PREFERENCE1", MODE_PRIVATE)
                .getString("USERMAIL","N/A");
        userid = getSharedPreferences("PREFERENCE2", MODE_PRIVATE)
                .getString("USERID","N/A");

        reportbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(MarkerPage.this);
                final String message = "does this place exist?";

                builder.setMessage(message)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {

                                        //post request to send positive
                                        pos_neg = 1;
                                        d.dismiss();
                                        new SendPostRequest().execute();

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        //post request to send negative
                                        pos_neg = 0;
                                        d.dismiss();
                                        new SendPostRequest().execute();


                                    }
                                });
                builder.create().show();
            }});


        reviewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to reviews page
                Intent i = new Intent(MarkerPage.this ,ReviewActivity.class);
                i.putExtra("EST_ID",est_id);
                startActivity(i);

            }});
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://188.226.144.157/group8/7awlya/report_establishment.php"); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pw","pw08");
                postDataParams.put("est_id",est_id);
                postDataParams.put("email","email_1@email.com");
                postDataParams.put("id","1");
                postDataParams.put("pos_neg",pos_neg);

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
            Toast.makeText(getApplicationContext(), "Thank you for your feedback",
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
