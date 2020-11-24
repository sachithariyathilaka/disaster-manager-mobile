package com.example.disaster;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText password,re_password,email,mobile,name;
    Button register;
    ProgressDialog progressDialog;
    String HOST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        register = findViewById(R.id.register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.Re_password);
        mobile = findViewById(R.id.mobile);
        name = findViewById(R.id.name);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Registering User...");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String Re_password = re_password.getText().toString().trim();
        String Mobile = mobile.getText().toString().trim();
        String Name = name.getText().toString().trim();
        HOST = "http://18.216.245.34/androidApp/register.php?email="+Email+"&password="+Password+"&mobile="+Mobile+"&name="+Name;
        progressDialog.show();

        if(Email.equals("")){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this,"Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(Password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this,"Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if(Re_password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this,"Please Enter Repeat Password", Toast.LENGTH_SHORT).show();
        }
        else if(Mobile.equals("")){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this,"Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        else if(Name.equals("")){
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this,"Please Enter Your Name", Toast.LENGTH_SHORT).show();
        }
        else{
            sendDataToDatabase();
        }
    }

    private void sendDataToDatabase() {

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
                                Toast.makeText(RegisterActivity.this,"Registration Succusfully!!", Toast.LENGTH_LONG).show();
                                goToLoginActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Error Occured due to "+ e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Error Occured "+ error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void goToLoginActivity() {
        Intent loginIntent=new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}

