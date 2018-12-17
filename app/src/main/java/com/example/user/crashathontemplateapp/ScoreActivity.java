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
        textView.setText("Dear "+readName()+" your final score is "+readScore());
    }

    @Override
    public void onBackPressed() {

    }

    public String readName(){
        String name=null;

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return null;
        }

        FileInputStream fis = null;

        try
        {
            File file = new File(this.getExternalFilesDir(null), "UserData");
            fis = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                Log.w("FileUtils", "File data: " + strLine);
                name=strLine;
            }
            in.close();
        }
        catch (Exception ex) {
            Log.e("FileUtils", "failed to load file", ex);
        }
        finally {
            try {if (null != fis) fis.close();} catch (IOException ex) {}
        }

        return name;
    }

    public int readScore(){

        int score=0;

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return 13;
        }

        FileInputStream fis = null;

        try
        {
            File file = new File(this.getExternalFilesDir(null), "ScoreData");
            fis = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                Log.w("FileUtils", "File data: " + strLine);
                score=Integer.parseInt(""+strLine);
            }
            in.close();
        }
        catch (Exception ex) {
            Log.e("FileUtils", "failed to load file", ex);
        }
        finally {
            try {if (null != fis) fis.close();} catch (IOException ex) {}
        }

        return score;

    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
