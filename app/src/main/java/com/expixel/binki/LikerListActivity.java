package com.expixel.binki;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cellbody on 2016/10/27.
 */

public class LikerListActivity extends BaseActivity {
    @BindView(R.id.bookName_liker_list)
    TextView bookName;
    @BindView(R.id.recyclerView_liker_list)
    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;
    String bookKey;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_liker_list);
        ButterKnife.bind(this);

        linearLayoutManager = new LinearLayoutManager(this);
        // 讓列表資料反轉 THIS ALSO SETS setStackFromBottom to true
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        Intent intent = this.getIntent();
        bookKey = intent.getStringExtra("bookKey");

        dbRef.child("post").child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                bookName.setText(post.bookName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Long, LikerItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, LikerItemViewHolder>(
                        Long.class,
                        LikerItemViewHolder.layoutResId,
                        LikerItemViewHolder.class,
                        dbRef.child("post").child(bookKey).child("likers")

                ) {
                    @Override
                    protected void populateViewHolder(final LikerItemViewHolder viewHolder, final Long postTime, int position) {

                    }
                };

    }

    public static class LikerItemViewHolder extends RecyclerView.ViewHolder{

        public final static int layoutResId = R.layout.item_liker;
        CircleImageView imgUser;
        TextView userName;

        public LikerItemViewHolder(View view) {
            super(view);
            imgUser = (CircleImageView) view.findViewById(R.id.imgUser_item_liker);
            userName = (TextView) view.findViewById(R.id.userName_item_liker);
        }

    }
}
