package com.expixel.binki;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    @BindView(R.id.toolbar_liker)
    Toolbar toolbar;
    @BindView(R.id.appBar_liker)
    AppBarLayout appBar;
    @BindView(R.id.recyclerView_liker_list)
    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;
    String bookKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_liker_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                getSupportActionBar().setTitle(post.bookName);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    @Override
    protected void onResume() {
        super.onStart();
        FirebaseRecyclerAdapter<String, LikerItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, LikerItemViewHolder>(
                        String.class,
                        LikerItemViewHolder.layoutResId,
                        LikerItemViewHolder.class,
                        dbRef.child("post").child(bookKey).child("likers")

                ) {
                    @Override
                    protected void populateViewHolder(final LikerItemViewHolder viewHolder, final String chatKey, int position) {
                        final String likerKey = getRef(position).getKey();

                        Log.d(TAG, "LikerListActivity: populateViewHolder: likerKey: " + likerKey);
                        Log.d(TAG, "LikerListActivity: populateViewHolder: chatKey: " + chatKey);

                        final String[] userData = new String[2];

                        //  分開撈是因為不用撈user全部
                        dbRef.child("users").child(likerKey).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userName = dataSnapshot.getValue(String.class);
                                viewHolder.userName.setText(userName);
                                userData[0] = userName;

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "LikerListActivity: load username: onCancelled: " + databaseError);

                            }
                        });
                        dbRef.child("users").child(likerKey).child("imgUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String imgUrl = dataSnapshot.getValue(String.class);
                                Glide.with(LikerListActivity.this.getApplicationContext())
                                        .load(imgUrl)
                                        .into(viewHolder.imgUser);
                                userData[1] = imgUrl;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "LikerListActivity: load username: onCancelled: " + databaseError);

                            }
                        });


                        dbRef.child("chat").child(chatKey).orderByChild("time").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                                    Message thisMessage = msgSnapshot.getValue(Message.class);
                                    if(thisMessage.check) {
                                        viewHolder.imgChat.setImageResource(R.drawable.chat_read);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "LikerListActivity: load chat check: onCancelled: " + databaseError);

                            }
                        });


                        viewHolder.imgChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Animation myAnim = AnimationUtils.loadAnimation(LikerListActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                view.startAnimation(myAnim);

                                Intent intent = new Intent();
                                intent.setClass(LikerListActivity.this, LikerInfoActivity.class);
                                intent.putExtra("userName", userData[0]);
                                intent.putExtra("userImgUrl", userData[1]);
                                intent.putExtra("likerKey", likerKey);
                                intent.putExtra("chatKey", chatKey);
                                startActivity(intent);
                            }
                        });


                    }
                };

        synchronized(recyclerView){
            recyclerView.setAdapter(adapter);
        }

    }



    public static class LikerItemViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_liker;
        CircleImageView imgUser;
        TextView userName;
        ImageView imgChat;

        public LikerItemViewHolder(View view) {
            super(view);
            imgUser = (CircleImageView) view.findViewById(R.id.imgUser_item_liker);
            userName = (TextView) view.findViewById(R.id.userName_item_liker);
            imgChat = (ImageView) view.findViewById(R.id.imgChat_item_liker);
        }

    }
}
