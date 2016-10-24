package com.expixel.binki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cellbody on 2016/10/23.
 */

public class LikedMessageActivity extends BaseActivity {
    @BindView(R.id.btnOK_message_liked)
    Button btnOK;
    @BindView(R.id.imgUser_message_liked)
    CircleImageView imgUser;
    @BindView(R.id.userName_message_liked)
    TextView userName;
    @BindView(R.id.bookName_message_liked)
    TextView bookName;
    @BindView(R.id.toolbar_message_liked)
    Toolbar toolbar;

    String key;
    String postTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_message_liked);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");
        postTime = bundle.getString("postTime");
        Glide.with(this)
                .load(bundle.getString("userImg"))
                .crossFade()
                .into(imgUser);
        userName.setText(bundle.getString("userName"));
        bookName.setText(bundle.getString("bookName"));


    }



    @OnClick(R.id.btnOK_message_liked)
    public void onClick() {
        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();

//        dbRef.child("users").child(getUid()).child("main").child(key).removeValue();
//        dbRef.child("users").child(getUid()).child("liked").child(key).setValue(postTime);
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
        return super.onOptionsItemSelected(item);
    }

}
