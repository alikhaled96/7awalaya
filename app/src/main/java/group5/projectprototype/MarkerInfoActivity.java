package group5.projectprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MarkerInfoActivity extends AppCompatActivity {
Spinner spselect;
    Intent i;
    String Name,Phone,quickInfo;
    Button submit;
    EditText nameet,phet,quickinfoet;
    int selected_category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);
        spselect= (Spinner) findViewById(R.id.spinnerselect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spselect.setAdapter(adapter);
        submit = (Button) findViewById(R.id.Submit);
        nameet = (EditText) findViewById(R.id.Nameselect);
        phet = (EditText) findViewById(R.id.Phoneselect);
        quickinfoet = (EditText) findViewById(R.id.quickinfo);

        spselect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selected_category = position;
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
                } else if (selected_category==0){
                    Toast.makeText(getApplicationContext(),"Please Select a Category",
                            Toast.LENGTH_LONG).show();
                }

                Name = nameet.getText().toString();
                Phone = phet.getText().toString();
                quickInfo = quickinfoet.getText().toString();

                i=new Intent(MarkerInfoActivity.this , MapActivity.class);
                Bundle extras= new Bundle();
                extras.putString("Marker_Name",Name);
                extras.putString("Marker_phone",Phone);
                extras.putString("Marker_info",quickInfo);
                extras.putInt("Marker_Category",selected_category);
                i.putExtras(extras);
                startActivity(i);

            }
        });
    }
}
