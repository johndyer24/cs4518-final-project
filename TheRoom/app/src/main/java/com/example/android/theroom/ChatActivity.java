package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    /**
     * Static method that returns intent used to start MainActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}