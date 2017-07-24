package group5.projectprototype;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
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

public class MainActivity extends AppCompatActivity {
Button signup;
    Intent i;
    EditText etpass,etdispname,etemail;
    String dispname,email,userID;
    private static final int RC_SIGN_IN = 9001;

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup = (Button) findViewById(R.id.buttontest);

        etdispname = (EditText) findViewById(R.id.dispnametext);
        etemail= (EditText) findViewById(R.id.emailtxt);

        i=new Intent(MainActivity.this , NavigationActivity.class);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);


    }



    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    signup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (etemail.getText().toString().isEmpty()){
                                Toast.makeText(getApplicationContext(),"Please Add your email",
                                        Toast.LENGTH_LONG).show();
                            } else if (etdispname.getText().toString().isEmpty()){
                                Toast.makeText(getApplicationContext(), "Please Add a Display name",
                                        Toast.LENGTH_LONG).show();
                            } else {

                                dispname = etdispname.getText().toString();
                                email = etemail.getText().toString();
                                new SendPostRequest().execute();

                                getSharedPreferences("PREFERENCE1", MODE_PRIVATE).edit()
                                        .putString("USEREMAIL",email).commit();
                                getSharedPreferences("PREFERENCE2", MODE_PRIVATE).edit()
                                        .putString("USERID",userID).commit();
                                getSharedPreferences("PREFERENCE3", MODE_PRIVATE).edit()
                                        .putString("DISPNAME",dispname).commit();
                                startActivity(i);
                            }
                        }});

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    //API WORK
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://188.226.144.157/group8/7awlya/register.php"); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pw","pw08" );
                postDataParams.put("display_name",dispname);
                postDataParams.put("email",email);


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

            Log.e("output" , result);
            try {
                JSONObject obj = new JSONObject(result);
                userID = obj.get("results").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return '•'; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };

}

