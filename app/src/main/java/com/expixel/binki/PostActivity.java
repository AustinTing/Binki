package com.expixel.binki;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.expixel.binki.R.id.fab;

/**
 * Created by cellbody on 2016/10/7.
 */

public class PostActivity extends BaseDialogActivity {
    @BindView(R.id.title_post)
    TextView titlePost;
    @BindView(R.id.etBookName_post)
    EditText etBookName;
    @BindView(R.id.etBookInfo_post)
    EditText etBookInfo;
    @BindView(R.id.btnOK_post)
    Button btnOKPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_post);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnOK_post)
    public void onClick() {
        String bookName = etBookName.getText().toString();
        if(bookName.equals("")){
            Toast.makeText(this, "You must input the book name", Toast.LENGTH_SHORT).show();
        }else if (!FastClickSensor.isFastDoubleClick()) {
            String bookInfo = etBookInfo.getText().toString();
            String key = dbRef.child("post").push().getKey();
            String userName = getUserName();
            String userImg = getUserImgeUrl();
            Post post = new Post(userName, userImg, bookName, bookInfo, System.currentTimeMillis());
            Map<String, Object> postValues = post.toMap();


            dbRef.child("post").child(key).setValue(postValues);
            dbRef.child("users").child(getUid()).child("shelf").child(key).setValue(post.postTime);
            Toast.makeText(this, "Book Added", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
}
