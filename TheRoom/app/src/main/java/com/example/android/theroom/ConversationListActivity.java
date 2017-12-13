package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.theroom.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity {

    private final String TAG = "ConversationList";

    private UltimateRecyclerView mConversationRecyclerView;
    private TextView mLoadingTextView;
    private ConversationAdapter mAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ValueEventListener mValueEventListener;
    private List<Chat> mChatList;

    /**
     * Static method that returns intent used to start MainActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ConversationListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coversation_list);

        // get references to database and auth object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // setup Adapter and RecyclerView
        mChatList = new ArrayList<>();
        mAdapter = new ConversationAdapter(mChatList);
        mConversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        mLoadingTextView = (TextView) findViewById(R.id.loading_list_text);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mConversationRecyclerView.setLayoutManager(manager);
        mConversationRecyclerView.setAdapter(mAdapter);

        // hide RecyclerView and show loading message while pulling data from firebase
        mConversationRecyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // add value event listener for user's chats
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mChatList == null) {
                        mChatList = new ArrayList<>();
                    }
                    mChatList.clear();
                    Log.d(TAG, "Num Chats: " + dataSnapshot.getChildrenCount());

                    // hide loading message and show RecyclerView
                    mLoadingTextView.setVisibility(View.GONE);
                    mConversationRecyclerView.setVisibility(View.VISIBLE);

                    // if there are no chats, show RecyclerView with empty view
                    if (!dataSnapshot.exists() || dataSnapshot.getChildrenCount() <= 0) {
                        Log.d(TAG, "snapshot does not exist");
                        mConversationRecyclerView.setEmptyView(R.layout.empty_list_view, UltimateRecyclerView.STARTWITH_OFFLINE_ITEMS);
                        mConversationRecyclerView.showEmptyView();
                        return;
                    }

                    // otherwise, pull chat information from firebase and add Chat to Adapter
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        mDatabase.getRef().child("chats/" + d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot ds) {
                                Chat c = ds.getValue(Chat.class);
                                c.setChatID(ds.getKey());
                                Log.d(TAG, "Chat start time: " + c.getStartTime());
                                Log.d(TAG, "Chat user1: " + c.getUser1());
                                Log.d(TAG, "Chat user2: " + c.getUser2());
                                mChatList.add(0, c);
                                Log.d(TAG, "Num Chats in list: " + mChatList.size());
                                mAdapter.setChats(mChatList);
                                mAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Num Chats in adapter: " + mAdapter.getItemCount());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            // start event listener
            mDatabase.child("userChats/" + mAuth.getUid()).addValueEventListener(mValueEventListener);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop event listener
        mDatabase.child("userChats/" + mAuth.getUid()).removeEventListener(mValueEventListener);
    }

    /**
     * The RecyclerView Viewholder
     */
    private class ConversationHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {

        private TextView mTextView;
        private Chat mChat;
        private int mPosition;

        public ConversationHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.conversation_list_row_textview);
            itemView.setOnClickListener(this);
        }

        /**
         * Used by Conversation Adapter to bind Chat object to the View
         * @param chat
         * @param position
         */
        public void bind(Chat chat, int position) {
            mChat = chat;
            String text = chat.getUser1().equals(mAuth.getUid()) ? chat.getUser2DisplayName() : chat.getUser1DisplayName();
            mTextView.setText(text);
            //mTextView.setText(getString(R.string.conversation_list_row_start_time_text, mChat.getStartTime()));
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            // start Chat Activity
            Intent i;
            if (mChat.getUser1().equals(mAuth.getUid())) {
                i = ChatActivity.newIntent(getContext(), mChat.getUser2(), mChat.getUser2DisplayName(), mChat.getChatID());
            } else {
                i = ChatActivity.newIntent(getContext(), mChat.getUser1(), mChat.getUser1DisplayName(), mChat.getChatID());
            }
            startActivity(i);
        }
    }

    /**
     * The RecyclerView Adapter
     */
    private class ConversationAdapter extends UltimateViewAdapter<ConversationHolder> {

        private List<Chat> mChats;

        public ConversationAdapter(List<Chat> chats) {
            mChats = chats;
        }

        public void setChats(List<Chat> chats) {
            mChats = chats;
        }

        @Override
        public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.conversation_list_row, parent, false);
            ConversationHolder vh = new ConversationHolder(v);
            return vh;
        }

        @Override
        public ConversationHolder newFooterHolder(View view) {
            return null;
        }

        @Override
        public ConversationHolder newHeaderHolder(View view) {
            return null;
        }

        @Override
        public ConversationHolder onCreateViewHolder(ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.conversation_list_row, parent, false);
            ConversationHolder vh = new ConversationHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ConversationHolder holder, int position) {
            holder.bind(mChats.get(position), position);
        }

        @Override
        public ConversationHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            if (mChats == null) {
                return 0;
            } else {
                return mChats.size();
            }
        }

        @Override
        public int getAdapterItemCount() {
            if (mChats == null) {
                return 0;
            } else {
                return mChats.size();
            }
        }

        @Override
        public long generateHeaderId(int position) {
            return 0;
        }
    }
}
