package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationActivity extends AppCompatActivity {

    private String mPartnerID;
    private String mPartnerDisplayName;
    private String mChatID;
    private boolean mRequestedLocation;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button mConfirmButton;
    private Button mCancelButton;
    private TextView mMessageView;

    /**
     * Static method that returns intent used to start LocationActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context, String partnerDisplayName, String partnerID, String chatID, boolean requestedLocation) {
        Intent i = new Intent(context, LocationActivity.class);
        i.putExtra("partnerID", partnerID);
        i.putExtra("partnerDisplayName", partnerDisplayName);
        i.putExtra("chatID", chatID);
        i.putExtra("requestedLocation", requestedLocation);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mPartnerID = getIntent().getStringExtra("partnerID");
        mPartnerDisplayName = getIntent().getStringExtra("partnerDisplayName");
        mChatID = getIntent().getStringExtra("chatID");
        mRequestedLocation = getIntent().getBooleanExtra("requestedLocation", false);

        mMessageView = (TextView) findViewById(R.id.message_text_view);
        mConfirmButton = (Button) findViewById(R.id.confirm_share_location_button);


        if (mRequestedLocation) {
            mMessageView.setText(getString(R.string.share_location_initial_message, mPartnerDisplayName));
        } else {
            mMessageView.setText(getString(R.string.share_location_confirm_message, mPartnerDisplayName));
        }

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRequestedLocation) {
                    mDatabase.child("chats/" + mChatID + "/" + mAuth.getUid() + "/shareLocation").setValue(true);
                    mMessageView.setText(R.string.share_location_waiting_message);
                } else {
                    mDatabase.child("chats/" + mChatID + "/" + mAuth.getUid() + "/shareLocation").setValue(true);
                    displayLocations();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRequestedLocation) {
                    finish();
                } else {

                }
            }
        });

    }

    private void displayLocations() {

    }
}
