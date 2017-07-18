package group5.projectprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class MarkerPage extends AppCompatActivity {
Button reviewbt , reportbt;
    EditText est_name , est_address;

    RatingBar rb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_page);
        reportbt = (Button) findViewById(R.id.reportbutton);
        reviewbt = (Button) findViewById(R.id.reviewbutton);
        est_address = (EditText) findViewById(R.id.address);
        est_name = (EditText) findViewById(R.id.name);
        rb = (RatingBar) findViewById(R.id.ratingBar);
        rb.setProgress(5);
        reportbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(MarkerPage.this);
                final String message = "Report?";

                builder.setMessage(message)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        //post request to send report
                                        d.dismiss();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        d.cancel();
                                    }
                                });
                builder.create().show();
            }});


        reviewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to reviews page
            }});
    }
}
