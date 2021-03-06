package com.expixel.binki;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cellbody on 2016/10/28.
 */

public class LikerInfoActivity extends BaseActivity {
    @BindView(R.id.imgUser_liker_info)
    CircleImageView imgUser;
    @BindView(R.id.userName_liker_info)
    TextView userName;
    @BindView(R.id.bookLink_liker_info)
    TextView bookLink;
    @BindView(R.id.message_liker_info)
    TextView message;
    @BindView(R.id.contact_liker_info)
    TextView contact;
    @BindView(R.id.link_liker_info)
    RelativeLayout linkRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_liker_info);
        ButterKnife.bind(this);

        Intent intent = this.getIntent();
        String likerKey = intent.getStringExtra("likerKey");
        final String chatKey = intent.getStringExtra("chatKey");
        String userName = intent.getStringExtra("userName");
        String imgUrl = intent.getStringExtra("userImgUrl");
        this.userName.setText(userName);
        Glide.with(LikerInfoActivity.this.getApplicationContext())
                .load(imgUrl)
                .into(imgUser);

        dbRef.child("users").child(getUid()).child("link").child(likerKey).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    linkRelativeLayout.setVisibility(View.VISIBLE);
                    for (DataSnapshot lastLikeBook : dataSnapshot.getChildren()) {

                        bookLink.setText(lastLikeBook.getValue(String.class));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("chat").child(chatKey).orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                    Message thisMessage = msgSnapshot.getValue(Message.class);
                    message.setText(thisMessage.content);
                    contact.setText(thisMessage.contact);

                    Log.d(TAG, "LikerInfoActivity: messageker: " + msgSnapshot.getKey());
                    thisMessage.check = true;
                    dbRef.child("chat").child(chatKey).child(msgSnapshot.getKey()).setValue(thisMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "LikerInfoActivity: load chat: onCancelled: " + databaseError);

            }
        });


    }
}
