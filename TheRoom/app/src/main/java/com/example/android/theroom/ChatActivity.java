package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.theroom.models.Message;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private static final String MSG = "messages/";
    private String mUserName;
    private String mChatID;
    private ChildEventListener chatListener;
    private DatabaseReference firebase;
    private FirebaseAuth mAuth;

    private UltimateRecyclerView mMessageRecyclerView;
    private TextView mLoadingTextView;
    private ChatAdapter mAdapter;
    private List<Message> mMessagesList;
    private ImageButton sendButton;
    private TextView inputText;

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
        sendButton = findViewById(R.id.sendButton);
        inputText = findViewById(R.id.inputText);
        firebase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // get the user's display name and the chatID from the intent
        mUserName = getIntent().getStringExtra("userName");
        mChatID = getIntent().getStringExtra("chatID");

        // set title to include the user's display name
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.chat_activity_title, mUserName));
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
            firebase.child(MSG + mChatID).addChildEventListener(chatListener);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputText.getText() == "") {
                    String newKey = firebase.child(MSG + mChatID).push().getKey();
                    firebase.child(MSG + mChatID + "/" + newKey + "/text").setValue(inputText.getText());
                    firebase.child(MSG + mChatID + "/" + newKey + "/time").setValue(ServerValue.TIMESTAMP);
                    firebase.child(MSG + mChatID + "/" + newKey + "/userID").setValue(mAuth.getUid());
                    inputText.setText("");
                }
            }
        });
    }

    private void addMessage(Message message) {
        mMessagesList.add(message);
        mAdapter.setMessages(mMessagesList);
        mAdapter.notifyDataSetChanged();
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

        private TextView mTextView;
        private Message mMessage;
        private int mPosition;

        public ChatHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.message_list_row_textview);
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
            mTextView.setText(text);
            mPosition = position;
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
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_row, parent, false);
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

