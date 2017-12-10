package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    private String mUserName;
    private String mChatID;

    /**
     * Static method that returns intent used to start MainActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context, String userName, String chatID) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("userName", userName);
        i.putExtra("chatID", chatID);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get the user's display name and the chatID from the intent
        mUserName = getIntent().getStringExtra("userName");
        mChatID = getIntent().getStringExtra("chatID");

        // set title to include the user's display name
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.chat_activity_title, mUserName));
        }

    }
}
