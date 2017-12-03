package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    /**
     * Static method that returns intent used to start MainActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * onClick handler for new chat button, starts NewChatActivity
     * @param v
     */
    public void newChat(View v) {
        Intent i = NewChatActivity.newIntent(this);
        startActivity(i);
    }

    /**
     * onClick handler for conversations button, starts CoversationsListActivity
     * @param v
     */
    public void viewConversations(View v) {
        Intent i = ConversationListActivity.newIntent(this);
        startActivity(i);
    }

    /**
     * onClick handler for settings button, starts SettingsActivity
     * @param v
     */
    public void viewSettings(View v) {
        Intent i = SettingsActivity.newIntent(this);
        startActivity(i);
    }
}
