package com.expixel.binki;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    DataSnapshot allPost;
    @BindView(R.id.tabs_main)
    TabLayout tabLayout;

    private ShowcaseView showcaseView;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        tabLayout.addTab(tabLayout.newTab().setText("Main"));
        tabLayout.addTab(tabLayout.newTab().setText("My Bookshelf"));
        tabLayout.addTab(tabLayout.newTab().setText("Liked"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {//ListView效果
//                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//                    recyclerView.setAdapter(listAdapter);
                    Toast.makeText(MainActivity.this, "我是一", Toast.LENGTH_SHORT).show();
                }
                if (tab.getPosition() == 1) {//GridView效果
//                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
//                    recyclerView.setAdapter(gridAdapter);
                    Toast.makeText(MainActivity.this, "我是二", Toast.LENGTH_SHORT).show();
                }
                if (tab.getPosition() == 2) {//Flow效果
                    //StaggeredGridLayoutManager.VERTICAL此处表示有多少列
//                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//                    recyclerView.setAdapter(flowViewAdapter);
                    Toast.makeText(MainActivity.this, "我是三", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setSupportActionBar(toolbar);

        linearLayoutManager = new LinearLayoutManager(this);
        // 讓列表資料反轉 THIS ALSO SETS setStackFromBottom to true
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(fab))
                .setOnClickListener(this)
//                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        showcaseView.setContentTitle("Add Button");
        showcaseView.setContentText("Add your book to exchange");
//        showcaseView.forceTextPosition(ShowcaseView.AB);
        showcaseView.setButtonPosition(lps);
        showcaseView.setButtonText(getString(R.string.next));
        setAlpha(0.1f, findViewById(R.id.recyclerView_main));
        setAlpha(0.1f, findViewById(R.id.appBar_main));


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
        switch (counter) {
            case 0:
                setAlpha(0.1f, fab);
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.toolbar)), true);
                break;


//            case 1:
//                showcaseView.setShowcase(new ViewTarget(fab), true);
//                break;

//            case 2:
//                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.liked)), true);
//                break;
//
//            case 2:
//                showcaseView.setTarget(Target.NONE);
//                showcaseView.setContentTitle("Check it out");
//                showcaseView.setContentText("You don't always need a target to showcase");
//                showcaseView.setButtonText(getString(R.string.close));
//                setAlpha(0.4f, textView1, textView2, textView3);
//                break;
//
            case 1:
                setAlpha(1.0f, findViewById(R.id.recyclerView_main));
                setAlpha(1.0f, findViewById(R.id.appBar_main));
                setAlpha(1.0f, fab);
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
