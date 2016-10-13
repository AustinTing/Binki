package com.expixel.binki;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    DataSnapshot  allPost;

    private ShowcaseView showcaseView;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayoutManager = new LinearLayoutManager(this);
        // 讓列表資料反轉 THIS ALSO SETS setStackFromBottom to true
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(fab))
                .setOnClickListener(this)


                .build();
//        showcaseView.setButtonText(getString(R.string.next));

    }


    private void setAlpha(float alpha, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            for (View view : views) {
                view.setAlpha(alpha);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (counter%3) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(fab), true);
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.toolbar)), true);
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.action_settings)), true);
                break;
//
//            case 2:
//                showcaseView.setTarget(Target.NONE);
//                showcaseView.setContentTitle("Check it out");
//                showcaseView.setContentText("You don't always need a target to showcase");
//                showcaseView.setButtonText(getString(R.string.close));
//                setAlpha(0.4f, textView1, textView2, textView3);
//                break;
//
            case 4:
                showcaseView.hide();
//                setAlpha(1.0f, textView1, textView2, textView3);
                break;
        }
        counter++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onClick() {
//        DialogPlus dialogPuls = DialogPlus.newDialog(this)
//                .setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"asdfa"}))
//                .setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//                    }
//                })
//                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
//                .create();
//        dialogPuls.show();
        Intent intent = new Intent();
        intent.setClass(this, PostActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        final String uid = auth.getCurrentUser().getUid();

        FirebaseRecyclerAdapter<Post, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Post, ItemViewHolder>(
                        Post.class,
                        ItemViewHolder.layoutResId,
                        ItemViewHolder.class,
                        dbRef.child("users").child(uid).child("main")
                ) {
                    @Override
                    protected void populateViewHolder(ItemViewHolder viewHolder, Post post, int position) {
                        viewHolder.userName.setText(post.userName);
                        Glide.with(MainActivity.this)
                                .load(post.userImg)
                                .crossFade()
                                .into(viewHolder.imgUser);
                        viewHolder.bookName.setText(post.bookName);

                        //  取得這個item的key
//                        getRef(i).getKey()
                    }
                };
        recyclerView.setAdapter(adapter);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_post;

        CircleImageView imgUser;
        TextView userName;
        TextView bookName;
        Button btnLike;
        Button btnHide;

        public ItemViewHolder(View view) {
            super(view);
            imgUser = (CircleImageView) view.findViewById(R.id.imgUser_main);
            userName = (TextView) view.findViewById(R.id.userName_main);
            bookName = (TextView) view.findViewById(R.id.bookName_main);
            btnLike = (Button) view.findViewById(R.id.btnLike_main);
            btnHide = (Button) view.findViewById(R.id.btnHide_main);

        }
    }
}
