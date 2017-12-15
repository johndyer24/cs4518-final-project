package com.example.android.theroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.android.theroom.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Tarek on 12/12/2017.
 */

public class Popup extends Activity{

    public boolean wantLocation;
    private DatabaseReference firebase;
    private String mPartnerID;
    private String mPartnerDisplayName;
    private String mChatID;

    /**
     * Static method that returns intent used to start PopupActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context, String partnerDisplayName, String partnerID, String chatID) {
        Intent i = new Intent(context, Popup.class);
        i.putExtra("partnerID", partnerID);
        i.putExtra("partnerDisplayName", partnerDisplayName);
        i.putExtra("chatID", chatID);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.7), (int)(height*.3));

        Button yesBtn = findViewById(R.id.yesBtn);
        Button noBtn = findViewById(R.id.noBtn);

        firebase = FirebaseDatabase.getInstance().getReference();

        mPartnerID = getIntent().getStringExtra("partnerID");
        mPartnerDisplayName = getIntent().getStringExtra("partnerDisplayName");
        mChatID = getIntent().getStringExtra("chatID");

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wantLocation = true;
                Intent intent = new Intent();
                intent.putExtra("wantLocation", wantLocation); //value should be your string from the edittext
                setResult(1, intent); //The data you want to send back
                firebase.child("chats/" + mChatID + "/" + mPartnerID + "/sharedLocation").setValue(false);
                finish();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wantLocation = false;
                Intent intent = new Intent();
                intent.putExtra("wantLocation", wantLocation); //value should be your string from the edittext
                setResult(1, intent); //The data you want to send back
                finish();
            }
        });

    }
}
