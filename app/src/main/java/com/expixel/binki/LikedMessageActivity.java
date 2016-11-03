package com.expixel.binki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cellbody on 2016/10/23.
 */

public class LikedMessageActivity extends BaseActivity {

    @BindView(R.id.imgUser_message_liked)
    CircleImageView imgUser;
    @BindView(R.id.userName_message_liked)
    TextView userName;
    @BindView(R.id.bookName_message_liked)
    TextView bookName;
    @BindView(R.id.etMessage_message_liked)
    EditText etMessage;
    @BindView(R.id.toolbar_message_liked)
    Toolbar toolbar;
    @BindView(R.id.etContact_message_liked)
    EditText etContact;
    @BindView(R.id.imgGmail_message_liked)
    ImageView imgGmail;

    String bookKey;
    String bkName;
    Long postTime;
    String userId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_message_liked);
        ButterKnife.bind(this);
        setTitle("Send Message");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        bookKey = bundle.getString("key");
        userId = bundle.getString("userId");
        postTime = bundle.getLong("postTime");
        bkName = bundle.getString("bookName");
        Glide.with(getApplicationContext())
                .load(bundle.getString("userImg"))
                .crossFade()
                .into(imgUser);
        userName.setText(bundle.getString("userName"));
        bookName.setText(bkName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation myAnim = AnimationUtils.loadAnimation(LikedMessageActivity.this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                view.startAnimation(myAnim);
                etContact.setText(auth.getCurrentUser().getEmail());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_liked, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.btnOK_message_liked) {

            if (!FastClickSensor.isFastDoubleClick()) {

                dbRef.child("post").child(bookKey).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Post post = mutableData.getValue(Post.class);
                        if (post == null) {
                            return Transaction.success(mutableData);
                        }

                        if (!post.likers.containsKey(getUid())) {
                            Log.d(TAG, "LikedMessageActivity: doTransaction: containsKey");

                            String chatKey = dbRef.child("chat").push().getKey();
                            String messageKey = dbRef.child("chat").child(chatKey).push().getKey();

                            HashMap<String, Object> content = new HashMap<>();
                            content.put("uid", getUid());
                            content.put("time", System.currentTimeMillis());
                            content.put("contact", etContact.getEditableText().toString());
                            content.put("content", etMessage.getEditableText().toString());
                            content.put("check", false);
                            dbRef.child("chat").child(chatKey).child(messageKey).setValue(content);

                            post.starCount = post.starCount + 1;
                            post.likers.put(getUid(), chatKey);

                            dbRef.child("users").child(getUid()).child("link").child(userId).child(bookKey).setValue(bkName);

                            dbRef.child("users").child(getUid()).child("main").child(bookKey).removeValue();
                            dbRef.child("users").child(getUid()).child("liked").child(bookKey).setValue(System.currentTimeMillis());

                        } else {
                            Log.e(TAG, "LikedMessageActivity: doTransaction: likers containsKey before like");
                        }

                        mutableData.setValue(post);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError == null) {
                            Log.i(TAG, "LikedMessageActivity: onComplete: ");
                            Toast.makeText(LikedMessageActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            LikedMessageActivity.this.finish();
                        } else {
                            Log.e(TAG, "LikedMessageActivity: onComplete: error: " + databaseError);
                        }
                    }
                });

                

            }
        }
        return super.onOptionsItemSelected(item);
    }

}
