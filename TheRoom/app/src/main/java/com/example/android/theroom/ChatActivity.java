package com.example.android.theroom;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.theroom.models.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.theroom.models.Message;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private static final String MSG = "messages/";
    private static int NUM_MESSAGES_TO_SHOW_LOCATION_BUTTON = 10;
    private static int LOCATION = 0;
    private static int MESSAGE_RECIEVED = 1;
    private static int MESSAGE_SENT = 0;
    private static final int REQUEST_LOCATION = 1;
    final private FirebaseDatabase database = FirebaseDatabase.getInstance();


    private String mUserName; // other user's ID
    private String mDisplayName; // other user's display name
    private String myUserName; // my user ID
    private String mChatID;
    private ChildEventListener chatListener;
    private DatabaseReference firebase;
    private FirebaseAuth mAuth;
    private boolean wantLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private UltimateRecyclerView mMessageRecyclerView;
    private ChatAdapter mAdapter;
    private List<Message> mMessagesList;
    private ImageButton sendButton;
    private TextView inputText;
    private Button locationButton;

    /**
     * Static method that returns intent used to start ChatActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context, String userName, String displayName, String chatID) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("userName", userName);
        i.putExtra("displayName", displayName);
        i.putExtra("chatID", chatID);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendButton = findViewById(R.id.sendButton);
        inputText = findViewById(R.id.inputText);
        locationButton = findViewById(R.id.locationButton);
        // hide location button until we check how many messages have been sent
        locationButton.setVisibility(View.GONE);

        firebase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // get the user's display name and the chatID from the intent
        mUserName = getIntent().getStringExtra("userName");
        mDisplayName = getIntent().getStringExtra("displayName");
        myUserName = mAuth.getUid();
        mChatID = getIntent().getStringExtra("chatID");

        // set title to include the user's display name
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.chat_activity_title, mDisplayName));
        }

        // setup Adapter and RecyclerView
        mMessagesList = new ArrayList<>();
        mAdapter = new ChatAdapter(mMessagesList);
        mMessageRecyclerView = findViewById(R.id.message_recycler_view);
        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mMessageRecyclerView.setLayoutManager(manager);
        mMessageRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(chatListener == null) {
            chatListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("ChatActivity", dataSnapshot.getKey());
                    Message message = (Message) dataSnapshot.getValue(Message.class);

                    addMessage(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            firebase.child(MSG + mChatID).orderByChild("time").addChildEventListener(chatListener);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!inputText.getText().toString().trim().equals("")) {
                    String newKey = firebase.child(MSG + mChatID).push().getKey();
                    firebase.child(MSG + mChatID + "/" + newKey + "/text").setValue(inputText.getText().toString());
                    firebase.child(MSG + mChatID + "/" + newKey + "/time").setValue(ServerValue.TIMESTAMP);
                    firebase.child(MSG + mChatID + "/" + newKey + "/userID").setValue(mAuth.getUid());
                    inputText.setText("");

                    // hide keyboard if it's shown
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationAndShare();
            }
        });

        DatabaseReference shareLocation = database.getReference("chats/" + mChatID + "/" + mUserName + "/sharedLocation");
        shareLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if((boolean) dataSnapshot.getValue()) {
                        Intent i = Popup.newIntent(getApplicationContext(), mDisplayName, mUserName, mChatID);
                        startActivityForResult(i, LOCATION);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION) {
            wantLocation = data.getBooleanExtra("wantLocation", false);

            if (wantLocation) {
                firebase.child("chats/" + mChatID + "/" + mUserName + "/location").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserLocation location = dataSnapshot.getValue(UserLocation.class);
                            Log.d("MapActivity", "Latitude: " + location.getLatitude());
                            Log.d("MapActivity", "Longitude: " + location.getLongitude());

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + location.getLatitude() + ">,<" + location.getLongitude() + ">?q=<" + location.getLatitude() + ">,<" + location.getLongitude() + ">(" + mDisplayName + "\'s Location)"));
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

    }

    private void addMessage(Message message) {
        // add Message to top of list, so that most recent message is always displayed
        mMessagesList.add(0, message);
        mAdapter.setMessages(mMessagesList);
        mAdapter.notifyDataSetChanged();

        // when users have sent enough messages, give them the option to share their location
        if (mMessagesList.size() >= NUM_MESSAGES_TO_SHOW_LOCATION_BUTTON) {
            locationButton.setVisibility(View.VISIBLE);
        }
    }

    private void getLocationAndShare() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        firebase.child("chats/" + mChatID + "/" + myUserName + "/sharedLocation").setValue(true);
                        firebase.child("chats/" + mChatID + "/" + myUserName + "/location/latitude").setValue(location.getLatitude());
                        firebase.child("chats/" + mChatID + "/" + myUserName + "/location/longitude").setValue(location.getLongitude());
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION:
                getLocationAndShare();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        firebase.child("messages/" + mChatID).removeEventListener(chatListener);
    }
    /**
     * The RecyclerView Viewholder
     */
    private class ChatHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {

        private TextView mTextViewLeft;
        private TextView mTextViewRight;
        private Message mMessage;
        private int mPosition;

        public ChatHolder(View itemView) {
            super(itemView);

            mTextViewLeft = (TextView) itemView.findViewById(R.id.message_list_row_textview_left);
            mTextViewRight = (TextView) itemView.findViewById(R.id.message_list_row_textview_right);
            itemView.setOnClickListener(this);
        }

        /**
         * Used by Chat Adapter to bind Message object to the View
         * @param message
         * @param position
         */
        public void bind(Message message, int position) {
            mMessage = message;
            String text = mMessage.getText();
            mPosition = position;

            Log.i("AE",mAuth.getUid() + " " + message.getUserID());

            if(mAuth.getUid().equals(message.getUserID())){
                mTextViewLeft.setVisibility(TextView.INVISIBLE);
                mTextViewRight.setVisibility(TextView.VISIBLE);
                mTextViewRight.setText(text);

            }else{
                mTextViewLeft.setVisibility(TextView.VISIBLE);
                mTextViewRight.setVisibility(TextView.INVISIBLE);
                mTextViewLeft.setText(text);
            }
        }

        @Override
        public void onClick(View view) {

        }
    }

    /**
     * The RecyclerView Adapter
     */
    private class ChatAdapter extends UltimateViewAdapter<ChatActivity.ChatHolder> {

        private List<Message> mMessages;

        public ChatAdapter(List<Message> messages) {
            mMessages = messages;
        }

        public void setMessages(List<Message> message) {
            mMessages = message;
        }

        @Override
        public ChatActivity.ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_row, parent, false);

            ChatActivity.ChatHolder vh = new ChatActivity.ChatHolder(v);
            return vh;
        }

        @Override
        public ChatActivity.ChatHolder newFooterHolder(View view) {
            return null;
        }

        @Override
        public ChatActivity.ChatHolder newHeaderHolder(View view) {
            return null;
        }

        @Override
        public ChatActivity.ChatHolder onCreateViewHolder(ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_row, parent, false);
            ChatActivity.ChatHolder vh = new ChatActivity.ChatHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ChatActivity.ChatHolder holder, int position) {
            holder.bind(mMessages.get(position), position);
        }

        @Override
        public ChatActivity.ChatHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            if (mMessages == null) {
                return 0;
            } else {
                return mMessages.size();
            }
        }

        @Override
        public int getAdapterItemCount() {
            if (mMessages == null) {
                return 0;
            } else {
                return mMessages.size();
            }
        }


        @Override
        public long generateHeaderId(int position) {
            return 0;
        }
    }
}

