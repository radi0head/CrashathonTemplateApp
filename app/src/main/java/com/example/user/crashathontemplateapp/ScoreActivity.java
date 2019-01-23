package com.example.user.crashathontemplateapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        TextView textView=(TextView)findViewById(R.id.score_text);
        textView.setText("Dear "+Utils.readName(this)+" your final score is "+Utils.readScore(this));
    }

    @Override
    public void onBackPressed() {

    }
}
