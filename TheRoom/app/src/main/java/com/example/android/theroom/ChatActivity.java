package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
<<<<<<< HEAD
import com.google.firebase.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
=======
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.theroom.models.Message;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;
>>>>>>> origin/master

public class ChatActivity extends AppCompatActivity {

    private static final String CHS = "newChat";
    private String mUserName;
    private String mChatID;
    private ChildEventListener chatListener;
    private DatabaseReference firebase;

    private UltimateRecyclerView mMessageRecyclerView;
    private TextView mLoadingTextView;
    private ChatAdapter mAdapter;
    private List<Message> mMessagesList;

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

    @Override
    protected void onStart(){
        super.onStart();

<<<<<<< HEAD
        firebase.child(CHS );
=======
        // setup Adapter and RecyclerView
        mMessagesList = new ArrayList<>();
        mAdapter = new ChatAdapter(mMessagesList);
        mMessageRecyclerView = findViewById(R.id.message_recycler_view);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(manager);
        mMessageRecyclerView.setAdapter(mAdapter);

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
>>>>>>> origin/master
    }

    public void addMessage()
}

