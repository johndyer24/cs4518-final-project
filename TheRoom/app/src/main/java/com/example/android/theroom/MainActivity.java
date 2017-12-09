package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                viewSettings();
                return true;
            case R.id.sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
     * onClick handler for settings button, starts ProfileActivity
     * @param v
     */
    public void viewProfile(View v) {
        Intent i = ProfileActivity.newIntent(this);
        startActivity(i);
    }

    /**
     * Navigate to SettingsActivity
     */
    public void viewSettings() {
        Intent i = SettingsActivity.newIntent(this);
        startActivity(i);
    }

    /**
     * Sign out of firebase and facebook, and navigate back to LoginActivity
     */
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent i = LoginActivity.newIntent(this);
        startActivity(i);
    }
}
