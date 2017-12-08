package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;

public class NewChatActivity extends AppCompatActivity {

    private final String TAG = "NewChatActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userID;
    private ValueEventListener mValueEventListener;

    /**
     * Static method that returns intent used to start MainActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, NewChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        // get references to database and auth object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // check whether we have already sent a chat request
        userID = mAuth.getUid();
        mDatabase.child("users/" + userID + "/requestedChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if we haven't already sent a chat request, create a new one
                if (!dataSnapshot.exists()) {
                    mDatabase.child("users/" + userID + "/requestedChat").setValue(true);
                    String newRequestKey = mDatabase.child("chatRequests").push().getKey();
                    mDatabase.child("chatRequests/" + newRequestKey + "/userID").setValue(userID);
                    mDatabase.child("chatRequests/" + newRequestKey + "/time").setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        // set child event listener on newUserChats listening for a new chat
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Chat " + dataSnapshot.getValue());

                    // make sure value isn't null before starting ChatActivity
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "New chat, navigating to Chat Activity");
                        // chat has been seen by user, remove it from newUserChats
                        mDatabase.child("newUserChats/" + userID).setValue(null);
                        // allow user to create new requests
                        mDatabase.child("users/" + userID + "/requestedChat").setValue(null);
                        goToChat();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            // start event listener
            mDatabase.child("newUserChats/" + userID).addValueEventListener(mValueEventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // remove child event listener listening for new chats
        if (mValueEventListener != null) {
            mDatabase.child("newUserChats/" + userID).removeEventListener(mValueEventListener);
        }
    }

    /**
     * Navigate to Chat Activity
     */
    private void goToChat() {
        Intent i = ChatActivity.newIntent(this);
        startActivity(i);
        finish(); // finish Activity so that user can't navigate back
    }
}
