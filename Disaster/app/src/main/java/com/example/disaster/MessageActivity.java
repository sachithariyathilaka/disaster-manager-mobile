package com.example.disaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageActivity extends AppCompatActivity {
    Button send, share;
    TextView message;
    String id,HOST;
    Double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ProgressDialog progressDialog;
    TextView idView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setTitle("Disaster Management Center");
        message = findViewById(R.id.msg);
        send = findViewById(R.id.send);
        share = findViewById(R.id.share);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Data...");
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHomeActivity();
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        id = getIntent().getExtras().getString("id");
        idView = findViewById(R.id.idView);
        idView.setText("Your User Id is "+id);
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MessageActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this).
                        setTitle("Required Location Permission")
                        .setMessage("You have to give this Permission to access the feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ActivityCompat.requestPermissions(MessageActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(MessageActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {


            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude=location.getLatitude();
                                longitude=location.getLongitude();

                            }
                        }
                    });
        }
    }

    private void sendData() {
        progressDialog.show();
        String Message = message.getText().toString().trim();
        String Longitude = longitude.toString().trim();
        String Latitude = latitude.toString().trim();
        HOST = "http://18.216.245.34/androidApp/saveData.php?id="+id+"&message="+Message+"&longitude="+Longitude+"&latitude="+Latitude;

        if (Message.equals("")) {
            Toast.makeText(MessageActivity.this,"Please Enter Your Message",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        else{
            sendDataToDB();
        }
    }

    private void sendDataToDB() {
        StringRequest stringRequest=new StringRequest(Request.Method.GET, HOST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String Output=jsonObject.getString("Output");
                            if(Output.equals("true"))
                            {
                                progressDialog.dismiss();
                                message.setText("");
                                Toast.makeText(MessageActivity.this,"Message Sent Succusfully!!", Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(MessageActivity.this, "Error Occured due to "+ e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(MessageActivity.this, "Error Occured "+ error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sendUserToHomeActivity() {
        Intent homeIntent = getPackageManager().getLaunchIntentForPackage("com.android.chatty");
        startActivity(homeIntent);

    }

}
