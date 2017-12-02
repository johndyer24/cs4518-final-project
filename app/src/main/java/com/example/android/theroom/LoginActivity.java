package com.example.android.theroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * onClick handler for login button, starts MainActivity
     * @param v
     */
    public void login(View v) {
        Intent i = MainActivity.newIntent(this);
        startActivity(i);
        finish(); // finish activity so user can't navigate back to it
    }

}
