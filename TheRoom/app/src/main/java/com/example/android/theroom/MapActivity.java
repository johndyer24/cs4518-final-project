package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.theroom.models.UserLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    private String mPartnerID;
    private String mPartnerDisplayName;
    private String mChatID;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    /**
     * Static method that returns intent used to start MapActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context, String partnerDisplayName, String partnerID, String chatID) {
        Intent i = new Intent(context, MapActivity.class);
        i.putExtra("partnerID", partnerID);
        i.putExtra("partnerDisplayName", partnerDisplayName);
        i.putExtra("chatID", chatID);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPartnerID = getIntent().getStringExtra("partnerID");
        mPartnerDisplayName = getIntent().getStringExtra("partnerDisplayName");
        mChatID = getIntent().getStringExtra("chatID");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // set title to include the user's display name
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.map_activity_title, mPartnerDisplayName));
        }

        mDatabase.child("chats/" + mChatID + "/" + mAuth.getUid() + "/location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserLocation location = dataSnapshot.getValue(UserLocation.class);
                    Log.d("MapActivity", "Latitude: " + location.getLatitude());
                    Log.d("MapActivity", "Longitude: " + location.getLongitude());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
