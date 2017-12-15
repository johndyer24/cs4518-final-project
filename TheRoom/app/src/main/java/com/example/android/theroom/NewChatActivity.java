package com.example.android.theroom;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.theroom.models.Chat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private static final int REQUEST_LOCATION = 0;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userID;
    private ValueEventListener mValueEventListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mNewRequestKey;
    private Location mLocation;
    private Button mCancelButton;

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //mCancelButton = (Button) findViewById(R.id.cancel_request_button);

        // check whether we have already sent a chat request
        userID = mAuth.getUid();

        mDatabase.child("users/" + userID + "/requestedChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if we haven't already sent a chat request, create a new one
                if (!dataSnapshot.exists()) {
                    requestChat();
                } else {
                    mNewRequestKey = (String) dataSnapshot.getValue();
                    //enableCancelButton(); // add listener for cancel button
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
                        goToChat((String)dataSnapshot.getValue());
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDatabase.child("chatRequests/" + mNewRequestKey).removeValue();
        mDatabase.child("users/" + userID + "/requestedChat").removeValue();
    }

    private void requestChat() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION);
        } else {
            try {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d(TAG, "we got a location");

                                    final Location l = location;

                                    // lookup user's displayName
                                    mDatabase.child("users/" + userID + "/displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final String displayName = (String) dataSnapshot.getValue();

                                            // lookup user's interests
                                            mDatabase.child("users/" + userID + "/interests").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot1) {

                                                    final DataSnapshot dataSnapshot2 = dataSnapshot1;

                                                    mDatabase.child("users/" + userID + "/hasChatsWith").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot3) {
                                                            // finally request a chat with the user's location data
                                                            mNewRequestKey = mDatabase.child("chatRequests").push().getKey();
                                                            mDatabase.child("users/" + userID + "/requestedChat").setValue(mNewRequestKey);
                                                            mDatabase.child("users/" + userID + "/requestedChat").setValue(mNewRequestKey);
                                                            mDatabase.child("chatRequests/" + mNewRequestKey + "/userID").setValue(userID);
                                                            mDatabase.child("chatRequests/" + mNewRequestKey + "/displayName").setValue(displayName);
                                                            mDatabase.child("chatRequests/" + mNewRequestKey + "/time").setValue(ServerValue.TIMESTAMP);
                                                            mDatabase.child("chatRequests/" + mNewRequestKey + "/latitude").setValue(l.getLatitude());
                                                            mDatabase.child("chatRequests/" + mNewRequestKey + "/longitude").setValue(l.getLongitude());

                                                            // add interests data to request
                                                            for (DataSnapshot d : dataSnapshot2.getChildren()) {
                                                                mDatabase.child("chatRequests/" + mNewRequestKey + "/interests/" + d.getKey()).setValue(true);
                                                            }

                                                            // add data of user's current chats to request
                                                            for (DataSnapshot d : dataSnapshot3.getChildren()) {
                                                                mDatabase.child("chatRequests/" + mNewRequestKey + "/hasChatsWith/" + d.getKey()).setValue(true);
                                                            }

                                                            // add listener for cancel button
                                                            //enableCancelButton();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                } else {
                                    Log.d(TAG, "location is null");
                                }

                            }
                        });
            } catch (SecurityException e) {
                Log.e(TAG, e.getMessage());
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION:
                requestChat();
        }
    }

    /**
     * Navigate to Chat Activity
     */
    private void goToChat(final String chatID) {
        // get chat info from firebase, in order to pass user's display name to Chat Activity
        mDatabase.child("chats/" + chatID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat c = dataSnapshot.getValue(Chat.class);
                c.setChatID(dataSnapshot.getKey());

                Intent i;
                if (c.getUser1().equals(mAuth.getUid())) {
                    i = ChatActivity.newIntent(getApplicationContext(), c.getUser2(), c.getUser2DisplayName(), c.getChatID());
                } else {
                    i = ChatActivity.newIntent(getApplicationContext(), c.getUser1(), c.getUser1DisplayName(), c.getChatID());
                }
                startActivity(i);
                finish(); // finish Activity so that user can't navigate back
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Sets onClickListener for the cancel button
     */
//    private void enableCancelButton() {
//        mCancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "newRequestKey: " + mNewRequestKey);
//                // delete request
//                mDatabase.child("chatRequests/" + mNewRequestKey).removeValue();
//                mDatabase.child("users/" + userID + "/requestedChat").removeValue();
//                finish(); // close activity
//            }
//        });
//    }
}
