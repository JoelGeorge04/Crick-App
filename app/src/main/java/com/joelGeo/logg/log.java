package com.joelGeo.logg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class log extends AppCompatActivity {
    AppCompatButton b1;
    EditText e1, e2, e3, e4;
    //api url implementation from backend link

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.logged_page);
        b1 = (AppCompatButton) findViewById(R.id.log);
        e1 = (EditText) findViewById(R.id.name);
        e2 = (EditText) findViewById(R.id.uname);
        e3 = (EditText) findViewById(R.id.pass);
        e4 = (EditText) findViewById(R.id.ipass);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences p = getSharedPreferences("Log", MODE_PRIVATE);
                SharedPreferences.Editor editor = p.edit();
                editor.clear();
                editor.apply();
                String name = e1.getText().toString();
                String uname = e2.getText().toString();
                String pass = e3.getText().toString();
                String ipass = e4.getText().toString();

                Toast.makeText(getApplicationContext(), name + uname + pass + ipass, Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

    }
}
