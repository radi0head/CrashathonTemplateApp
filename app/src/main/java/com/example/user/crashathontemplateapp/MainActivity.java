package com.example.user.crashathontemplateapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    int count=0;
    int time=0;
    int countCamera=0;
    int crash1Count=0;
    int crash2Count=0;
    Button crash1=null;
    Button crash2=null;
    TextView timerTextView=null;
    SharedPreferences sharedPref;

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.crash1){
            Button button=(Button) v;
            if(crash1Count==0){
                button.setText(getString(R.string.no_crash));
                crash1Count++;
            }else{
                button.setText(getString(R.string.crash));
                crash1Count--;
            }
        }else if(id==R.id.crash2){
            Button button=(Button) v;
            if(crash2Count==0){
                button.setText(getString(R.string.no_crash));
                crash2Count++;
            }else{
                button.setText(getString(R.string.crash));
                crash2Count--;
            }
        }else if(id==R.id.timer_text){
            //We begin by checking if the feature has been locked previously
            sharedPref=this.getPreferences(Context.MODE_PRIVATE);
            Boolean isLocked=sharedPref.getBoolean(getString(R.string.score_text_lock),false);
            if(isLocked){
                Snackbar.make(v, "You've already used this feature", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else{
                //First, lock the feature so that it can't be used more than once
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putBoolean(getString(R.string.score_text_lock), true);
                editor.apply();
                //Then, we crash the app
                crash();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the starting time
        time=readTime();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countCamera++;
                Snackbar.make(view, "Try selecting the Import option now", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Get a handle to a sharedpref object
        sharedPref=this.getPreferences(Context.MODE_PRIVATE);
        //Check to see if the game has already ended
        //if yes, then proceed to ScoreActivity autmotically, if no, then stay
        Boolean isGameOver=sharedPref.getBoolean(getString(R.string.game_over_key),false);
        if(isGameOver){
            Intent intent=new Intent(this, ScoreActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//        drawer.setVisibility(View.GONE);
        crash1=(Button)findViewById(R.id.crash1);
        crash2=(Button)findViewById(R.id.crash2);
        crash1.setOnClickListener(this);
        crash2.setOnClickListener(this);

        String scoreString="";
        File file = new File(this.getExternalFilesDir(null), "ScoreData");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            scoreString=br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(scoreString.isEmpty()){
            updateScore(count);
        }else{
            count=readScore();
        }

        timerTextView=(TextView)findViewById(R.id.timer_text);
        timerTextView.setOnClickListener(this);

        //set the timer for the game by reading the shared preferences

        new CountDownTimer(time*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextView.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                //set the game_over sharedpref value as true and direct to the ScoreActivity
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putBoolean(getString(R.string.game_over_key),true);
                editor.apply();
                Intent intent=new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        }.start();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            sharedPref=this.getPreferences(Context.MODE_PRIVATE);
            Boolean isLocked=sharedPref.getBoolean(getString(R.string.menu_item_lock),false);
            if(isLocked){
                Toast.makeText(this, "You've already used this feature", Toast.LENGTH_SHORT).show();
            }else{
                //First, lock the feature so that it can't be used more than once
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putBoolean(getString(R.string.menu_item_lock), true);
                editor.apply();
                //Then, we crash the app
                crash();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            if(countCamera==0){
                Intent cameraIntent=new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(cameraIntent);
            }else{
                sharedPref=this.getPreferences(Context.MODE_PRIVATE);
                Boolean isLocked=sharedPref.getBoolean(getString(R.string.camera_lock),false);
                if(isLocked){
                    Toast.makeText(this, "You've already used this feature", Toast.LENGTH_SHORT).show();
                }else{
                    //First, lock the feature so that it can't be used more than once
                    SharedPreferences.Editor editor=sharedPref.edit();
                    editor.putBoolean(getString(R.string.camera_lock), true);
                    editor.apply();
                    //Then, we crash the app
                    crash();
                }
            }
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean updateScore(int count){
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(this.getExternalFilesDir(null), "ScoreData");
        PrintStream p = null; // declare a print stream object
        boolean success = false;

        try {
            OutputStream os = new FileOutputStream(file);
            // Connect print stream to the output stream
            p = new PrintStream(os);
            p.println(""+count);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != p)
                    p.close();
            } catch (Exception ex) {
            }
        }
        return success;
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

    public void crash(){
        count++;
        updateScore(count);
        writeTime();
        throw new RuntimeException("crash");
    }

    public int readTime(){
        String timeText=null;
        sharedPref=this.getPreferences(Context.MODE_PRIVATE);
        timeText=sharedPref.getString(getString(R.string.timer_text_key),""+60);
        return Integer.parseInt(timeText);
    }

    public void writeTime(){
        int time=0;
        time=Integer.parseInt(timerTextView.getText().toString());
        sharedPref=this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString(getString(R.string.timer_text_key), ""+time);
        editor.apply();
    }

    public void onLogoClick(View v){
        sharedPref=this.getPreferences(Context.MODE_PRIVATE);
        Boolean isLocked=sharedPref.getBoolean(getString(R.string.logo_lock),false);
        if(isLocked){
            Snackbar.make(v, "You've already used this feature", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            //First, lock the feature so that it can't be used more than once
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putBoolean(getString(R.string.logo_lock), true);
            editor.apply();
            //Then, we crash the app
            crash();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Boolean isGameOver=sharedPref.getBoolean(getString(R.string.game_over_key),false);
        if(isGameOver){
            super.onStop();
        }else{
            writeTime();
            super.onStop();
        }
    }
}
