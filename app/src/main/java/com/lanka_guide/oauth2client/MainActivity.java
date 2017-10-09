package com.lanka_guide.oauth2client;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public void onResOwnerButton(View view) {
        Intent myIntent = new Intent(MainActivity.this, ResourceOwnerGrant.class);
        MainActivity.this.startActivity(myIntent);
    }
}
