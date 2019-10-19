package com.darkweb.genesissearchengine;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class orbot_view_settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orbot_view_settings);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}