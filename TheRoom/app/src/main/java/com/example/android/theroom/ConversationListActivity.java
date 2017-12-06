package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

public class ConversationListActivity extends AppCompatActivity {

    private UltimateRecyclerView mConversationRecyclerView;
    private ConversationAdapter mAdapter;

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

        mAdapter = new ConversationAdapter();
        mConversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        mConversationRecyclerView.setAdapter(mAdapter);
    }

    private class ConversationHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener{

        public ConversationHolder(View itemView) {
            super(itemView);
        }

        public void bind() {

        }

        @Override
        public void onClick(View view) {

        }
    }

    private class ConversationAdapter extends UltimateViewAdapter<ConversationHolder> {

        @Override
        public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
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
            return null;
        }

        @Override
        public void onBindViewHolder(ConversationHolder holder, int position) {
            holder.bind();
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public int getAdapterItemCount() {
            return 0;
        }

        @Override
        public long generateHeaderId(int position) {
            return 0;
        }
    }
}
