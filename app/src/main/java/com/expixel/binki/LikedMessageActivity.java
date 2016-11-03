package com.expixel.binki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    String bookKey;
    Long postTime;
    @BindView(R.id.toolbar_message_liked)
    Toolbar toolbar;
    @BindView(R.id.etContact_message_liked)
    EditText etContact;
    @BindView(R.id.imgGmail_message_liked)
    ImageView imgGmail;


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
        postTime = bundle.getLong("postTime");
        Glide.with(getApplicationContext())
                .load(bundle.getString("userImg"))
                .crossFade()
                .into(imgUser);
        userName.setText(bundle.getString("userName"));
        bookName.setText(bundle.getString("bookName"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                            post.starCount = post.starCount + 1;

                            String chatKey = dbRef.child("chat").push().getKey();
                            String messageKey = dbRef.child("chat").child(chatKey).push().getKey();

                            HashMap<String, Object> content = new HashMap<>();
                            content.put("uid", getUid());
                            content.put("time", System.currentTimeMillis());
                            content.put("contact", etContact.getEditableText().toString());
                            content.put("content", etMessage.getEditableText().toString());

                            dbRef.child("chat").child(chatKey).child(messageKey).setValue(content);

                            post.likers.put(getUid(), chatKey);
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
                        } else {
                            Log.e(TAG, "LikedMessageActivity: onComplete: error: " + databaseError);
                        }
                    }
                });

                this.finish();

            }
        }
        return super.onOptionsItemSelected(item);
    }

}
