package com.example.user.crashathontemplateapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LoginActivity extends AppCompatActivity {

    EditText nameField=null;
    EditText passwordField=null;
    String passwordByUser=null;
    Button proceed=null;
    String userInfo=null;
    SharedPreferences sharedPref=null;
    String password="1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref=this.getPreferences(Context.MODE_PRIVATE);
        Boolean isLoggedIn=sharedPref.getBoolean(getString(R.string.log_in_key),false);
        if(isLoggedIn){
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        proceed=(Button)findViewById(R.id.enter_game_button);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameField=(EditText)findViewById(R.id.name_field);
                userInfo=nameField.getText().toString();

                passwordField=(EditText)findViewById(R.id.password_field);
                passwordByUser=passwordField.getText().toString();
                if(passwordByUser.equals(password)){
                    writeName(userInfo);
                    //set the logged in sharedpref as true
                    SharedPreferences.Editor editor=sharedPref.edit();
                    editor.putBoolean(getString(R.string.log_in_key),true);
                    editor.apply();
                    //take the user to the MainActivity
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, "Please enter the correct password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeName(String name){
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(LoginActivity.this.getExternalFilesDir(null), "UserData");
        PrintStream p = null; // declare a print stream object

        try {
            OutputStream os = new FileOutputStream(file);
            // Connect print stream to the output stream
            p = new PrintStream(os);
            p.println(name);
            Log.w("FileUtils", "Writing file" + file);
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
