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

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button login;
    String HOST,id;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Logging User...");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        HOST = "http://18.216.245.34/androidApp/login.php?email="+Email+"&password="+Password;
        progressDialog.show();

        if(Email.equals("")){
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this,"Please Enter Email", Toast.LENGTH_LONG).show();
        }
        else if(Password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this,"Please Enter Password", Toast.LENGTH_LONG).show();
        }
        else{
            sendUserCredintialsToDatabase();
        }
    }

    private void sendUserCredintialsToDatabase() {

        StringRequest stringRequest=new StringRequest(Request.Method.GET, HOST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String Output=jsonObject.getString("Output");
                            id = jsonObject.getString("id");
                            if(Output.equals("true"))
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Login Succusfully!!", Toast.LENGTH_LONG).show();
                                goToMessageActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error Occured due to "+ e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error Occured "+ error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void goToMessageActivity() {
        Intent msgIntent=new Intent(LoginActivity.this, MessageActivity.class);
        msgIntent.putExtra("id",id);
        startActivity(msgIntent);
        finish();
    }
}

