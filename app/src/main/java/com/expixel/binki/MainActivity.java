package com.expixel.binki;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

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
    @BindView(R.id.tabs_main)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ShowcaseView showcaseView;
    private int counter = 0;
    Long lastLoadTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("BINKI");
        setSupportActionBar(toolbar);

        tabLayout.addTab(tabLayout.newTab().setText("Main"));
        tabLayout.addTab(tabLayout.newTab().setText("My Shelf"));
        tabLayout.addTab(tabLayout.newTab().setText("Liked"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadMainList();
                }
                if (tab.getPosition() == 1) {//GridView效果
                    loadShelfList();
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

        linearLayoutManager = new LinearLayoutManager(this);
        // 讓列表資料反轉 THIS ALSO SETS setStackFromBottom to true
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

//        Tutorial
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

        //  fab animation
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 20); // Use bounce interpolator with amplitude 0.2 and frequency 20
        myAnim.setInterpolator(interpolator);
        fab.startAnimation(myAnim);

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
//                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.toolbar)), true);
//                showcaseView.setShowcaseX(10);
                setAlpha(1.0f, findViewById(R.id.appBar_main));
                showcaseView.setContentTitle("Book added in Here");
                showcaseView.setContentText("");
                showcaseView.setShowcase(new Target() {
                    @Override
                    public Point getPoint() {
                        // Get approximate position of home icon's center
                        int actionBarHeight = toolbar.getHeight();
                        int actionBarWidth = toolbar.getWidth();
                        int x = actionBarWidth / 2;
                        int y = actionBarHeight * 2;
                        return new Point(x, y);
                    }
                }, true);
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
        switch (id) {
            case R.id.message_menu:
                Toast.makeText(this, "message", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.hide_menu:
                Toast.makeText(this, "hide", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings_menu:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu:
                auth.signOut();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        fab.startAnimation(myAnim);

        Intent intent = new Intent();
        intent.setClass(this, PostActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadMainList();

    }

    private void loadShelfList() {
        FirebaseRecyclerAdapter<Long, ShelfItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, ShelfItemViewHolder>(
                        Long.class,
                        ShelfItemViewHolder.layoutResId,
                        ShelfItemViewHolder.class,
                        dbRef.child("users").child(getUid()).child("shelf")
                ) {
                    @Override
                    protected void populateViewHolder(final ShelfItemViewHolder viewHolder, Long postTime, int position) {
                        dbRef.child("post").orderByKey().equalTo(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Log.i(TAG, "MainActivity: loadShelf: item: " + postSnapshot.getKey());
                                    Post post = postSnapshot.getValue(Post.class);
                                    viewHolder.bookName.setText(post.bookName);
                                    viewHolder.likeCount.setText(String.valueOf(post.starCount));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "MainActivity: loadMainList(): DB: onCancelled: " + databaseError.getMessage());

                            }
                        });
                        viewHolder.showLikedLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                viewHolder.showLikedLayout.startAnimation(myAnim);

                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);
    }

    private void loadMainList() {
        FirebaseRecyclerAdapter<Long, MainItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, MainItemViewHolder>(
                        Long.class,
                        MainItemViewHolder.layoutResId,
                        MainItemViewHolder.class,
                        dbRef.child("users").child(getUid()).child("main")
                ) {
                    @Override
                    protected void populateViewHolder(final MainItemViewHolder viewHolder, Long postTime, int position) {
                        //  取得這個item的key
                        //  getRef(position).getKey()
                        final String key = getRef(position).getKey();
                        //  撈這個post的資料
                        dbRef.child("post").orderByKey().equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Log.i(TAG, "MainActivity: loadMain: item: " + postSnapshot.getKey());
                                        Post post = postSnapshot.getValue(Post.class);
                                        viewHolder.userName.setText(post.userName);
                                        Glide.with(MainActivity.this)
                                                .load(post.userImg)
                                                .crossFade()
                                                .into(viewHolder.imgUser);
                                        viewHolder.bookName.setText(post.bookName);
                                    }
                                } else { //  可能被刪掉了
                                    dbRef.child("users").child(getUid()).child("main").child(key).removeValue();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "MainActivity: loadMainList(): DB: onCancelled: " + databaseError.getMessage());

                            }
                        });

                        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                viewHolder.btnLike.startAnimation(myAnim);
                            }

                        });

                        viewHolder.btnHide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                viewHolder.btnHide.startAnimation(myAnim);
                            }
                        });

                    }
                };
        recyclerView.setAdapter(adapter);

        //  把之前紀錄最後一筆更新的時間之後的書單再撈進main
        dbRef.child("users").child(getUid()).child("lastLoadTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastLoadTime = dataSnapshot.getValue(Long.class);
                dbRef.child("post").orderByChild("postTime").startAt(lastLoadTime).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //  把post裡面，自己的書踢掉
                            if (!getUserName().equals(postSnapshot.child("userName").getValue())) {
                                Log.i(TAG, "MainActivity: loadMainList: refresh main: " + postSnapshot.getKey());
                                childUpdates.put(postSnapshot.getKey(), postSnapshot.child("postTime").getValue(Long.class));
                                lastLoadTime = postSnapshot.child("postTime").getValue(Long.class);
                            }
                        }
                        dbRef.child("users").child(getUid()).child("main").updateChildren(childUpdates);
                        dbRef.child("users").child(getUid()).child("lastLoadTime").setValue(lastLoadTime);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "MainActivity: loadLastTime: refresh main: " + databaseError.getMessage());

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "MainActivity: loadLastTime: " + databaseError.getMessage());

            }
        });

    }


    public static class MainItemViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_post;

        CircleImageView imgUser;
        TextView userName;
        TextView bookName;
        Button btnLike;
        Button btnHide;

        public MainItemViewHolder(View view) {
            super(view);
            imgUser = (CircleImageView) view.findViewById(R.id.imgUser_main);
            userName = (TextView) view.findViewById(R.id.userName_main);
            bookName = (TextView) view.findViewById(R.id.bookName_main);
            btnLike = (Button) view.findViewById(R.id.btnLike_main);
            btnHide = (Button) view.findViewById(R.id.btnHide_main);
        }
    }

    public static class ShelfItemViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_shelf;


        TextView bookName;
        RelativeLayout showLikedLayout;
        TextView likeCount;

        public ShelfItemViewHolder(View view) {

            super(view);
            bookName = (TextView) view.findViewById(R.id.bookName_shelf);
            showLikedLayout = (RelativeLayout) view.findViewById(R.id.showLikedLayout_shelf);
            likeCount = (TextView) view.findViewById(R.id.likeCount_shelf);
        }
    }


}


