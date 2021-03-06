package com.expixel.binki;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tabs_main)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    Long lastLoadTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(getString(R.string.title_main));
        setSupportActionBar(toolbar);
        //tabs
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.shelf_dark));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.favorite_dark));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                if (tab.getPosition() == 0) {
                    Log.i(TAG, "MainActivity: tab Main :");
                    loadMainList();
                    setTitle(getString(R.string.title_main));
                    fab.setVisibility(View.VISIBLE);
                }
                if (tab.getPosition() == 1) {
                    Log.i(TAG, "MainActivity: tab Shelf :");
                    loadShelfList();
                    setTitle(getString(R.string.myBookshelf_main));
                    fab.setVisibility(View.VISIBLE);
                }
                if (tab.getPosition() == 2) {
                    Log.i(TAG, "MainActivity: tab Liked :");
                    loadLikedList();
                    setTitle(getString(R.string.myFavorite_main));
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
//        RecyclerView
        linearLayoutManager = new LinearLayoutManager(this);
        // 讓列表資料反轉 THIS ALSO SETS setStackFromBottom to true
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        //  fab animation
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20); // Use bounce interpolator with amplitude 0.2 and frequency 20
        myAnim.setInterpolator(interpolator);
        fab.startAnimation(myAnim);

    }

    @Override
    protected void onStart() {
        super.onStart();


        int tabPosition = tabLayout.getSelectedTabPosition();

        tabLayout.getTabAt(0).getIcon().setColorFilter(getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        switch (tabPosition) {

            case 0:
                tabLayout.getTabAt(tabPosition).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                loadMainList();
                break;
            case 1:
                tabLayout.getTabAt(tabPosition).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                loadShelfList();
                break;
            case 2:
                tabLayout.getTabAt(tabPosition).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                loadLikedList();
                break;
            default:
                Log.e(TAG, "MainActivity: onStart: wrong Selected Tab");
        }
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
            case R.id.hide_menu:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HiddenListActivity.class);
                startActivity(intent);
                return true;
            case R.id.feedback_menu:
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle("意見與回饋");
                final EditText editText = new EditText(MainActivity.this);
                dialogBuilder.setView(editText);
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = dialogBuilder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String feedbackContent = editText.getText().toString();
                                if (feedbackContent.equals("")) {
                                    Toast.makeText(MainActivity.this, R.string.toast_recommand_main, Toast.LENGTH_SHORT).show();
                                } else {
                                    String feedbackKey = dbRef.child("feedback").push().getKey();
                                    dbRef.child("feedback").child(feedbackKey).child(getUid()).setValue(feedbackContent);
                                    Toast.makeText(MainActivity.this, R.string.toast_thank_report_main, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });

                dialog.show();

                return true;
            case R.id.logout_menu:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void logout() {
        Log.d(TAG, "MainActivity: logout: ");
        auth.signOut();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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

        analytics.logEvent("click_add", analyticParams);

        Intent intent = new Intent();
        intent.setClass(this, PostActivity.class);
        startActivity(intent);
    }


    private void loadMainList() {
        Log.i(TAG, "MainActivity: loadMainList: ");

        FirebaseRecyclerAdapter<Long, MainItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, MainItemViewHolder>(
                        Long.class,
                        MainItemViewHolder.layoutResId,
                        MainItemViewHolder.class,
                        dbRef.child("users").child(getUid()).child("main")
                ) {

                    @Override
                    protected void populateViewHolder(final MainItemViewHolder viewHolder, final Long postTime, final int position) {
//                        TODO: 刪除的方法重複寫了三次,一樣的動畫也寫了多次
                        //  取得這個item的key
                        //  getRef(position).getKey()
                        final String key = getRef(position).getKey();

                        final Post[] finalArrPost = new Post[1];

                        //  撈這個post的資料
//                        final Post[] finalArrPost = arrPost;
                        dbRef.child("post").orderByKey().equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Post post = postSnapshot.getValue(Post.class);
//                                        finalPost = post;
                                        finalArrPost[0] = post;
//                                        setPost(finalArrPost, post);
                                        viewHolder.userName.setText(post.userName);
                                        Glide.with(getApplicationContext())
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

//                            public void setPost(Post[] gettedPost, Post innnerPost ){
//                                gettedPost[0] = innnerPost;
//
//                            }
                        });

                        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                viewHolder.btnLike.startAnimation(myAnim);

                                if (finalArrPost[0] != null) {
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, LikedMessageActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("userId", finalArrPost[0].userId);
                                    bundle.putString("userImg", finalArrPost[0].userImg);
                                    bundle.putString("userName", finalArrPost[0].userName);
                                    bundle.putString("bookName", finalArrPost[0].bookName);
                                    bundle.putString("key", key);
                                    bundle.putLong("postTime", postTime);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                }

//
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
                                dbRef.child("users").child(getUid()).child("main").child(key).removeValue();
                                dbRef.child("users").child(getUid()).child("hided").child(key).setValue(postTime);


                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(finalArrPost[0].bookName + "\n\n" + finalArrPost[0].bookInfo)
                                        .create().show();
                            }
                        });

                    }
                };

        recyclerView.setAdapter(adapter);

        //  把之前紀錄最後一筆更新的時間之後的書單再撈進main
        dbRef.child("users").child(getUid()).child("lastLoadTime").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        lastLoadTime = dataSnapshot.getValue(Long.class);
                        dbRef.child("post").orderByChild("postTime").startAt(lastLoadTime + 1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> childUpdates = new HashMap<>();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    //  把post裡面，自己的書踢掉
                                    if (!getUserName().equals(postSnapshot.child("userName").getValue())) {
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
                }
        );

    }


    private void loadShelfList() {
        Log.i(TAG, "MainActivity: loadShelfList: ");
        FirebaseRecyclerAdapter<Long, ShelfItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, ShelfItemViewHolder>(
                        Long.class,
                        ShelfItemViewHolder.layoutResId,
                        ShelfItemViewHolder.class,
                        dbRef.child("users").child(getUid()).child("shelf")
                ) {
                    @Override
                    protected void populateViewHolder(final ShelfItemViewHolder viewHolder, Long postTime, final int position) {
                        final String bookKey = getRef(position).getKey();
                        final Post[] arrPost = new Post[1];
                        dbRef.child("post").orderByKey().equalTo(bookKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "MainActivity: onDataChange: ");
                                if (dataSnapshot != null) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Post post = postSnapshot.getValue(Post.class);
                                        arrPost[0] = post;
                                        viewHolder.bookName.setText(post.bookName);
                                        if (post.starCount != 0) {
                                            viewHolder.showLikedLayout.setVisibility(View.VISIBLE);
                                            viewHolder.likeCount.setText(String.valueOf(post.starCount));
                                        } else {
                                            viewHolder.showLikedLayout.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "MainActivity: loadShelfList: load post by key = null");
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
                                Log.d(TAG, "MainActivity: onClick: ");
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, LikerListActivity.class);
                                intent.putExtra("bookKey", bookKey);
                                startActivity(intent);

                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(arrPost[0].bookName + "\n\n" + arrPost[0].bookInfo)
                                        .create().show();
                            }
                        });

                        //  TODO:作短按動畫，但做短按動畫在動畫完成前切換會出狀況
                        // http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/1217/3782.html
                        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                String[] list = new String[]{"Delete"};
                                new AlertDialog.Builder(MainActivity.this)
                                        .setItems(list, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Are you sure ?");
                                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i) {
                                                        // TODO: 記得link那邊也要刪除
                                                        dbRef.child("post").child(bookKey).removeValue();
                                                        dbRef.child("users").child(getUid()).child("shelf").child(bookKey).removeValue();
                                                        Toast.makeText(MainActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();

                                            }
                                        })
                                        .show();
                                return false;
                            }
                        });

                    }
                };

        recyclerView.setAdapter(adapter);
    }


    private void loadLikedList() {
        Log.i(TAG, "MainActivity: loadLikedList: ");
        FirebaseRecyclerAdapter<Long, LikedItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, LikedItemViewHolder>(
                        Long.class,
                        LikedItemViewHolder.layoutResId,
                        LikedItemViewHolder.class,
                        dbRef.child("users").child(getUid()).child("liked").orderByValue()
                ) {
                    @Override
                    protected void populateViewHolder(final LikedItemViewHolder viewHolder, final Long postTime, final int position) {
                        final String bookKey = getRef(position).getKey();
                        final Post[] arrPost = new Post[1];
                        dbRef.child("post").orderByKey().equalTo(bookKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Post post = postSnapshot.getValue(Post.class);
                                        viewHolder.userName.setText(post.userName);
                                        Glide.with(getApplicationContext())
                                                .load(post.userImg)
                                                .crossFade()
                                                .into(viewHolder.imgUser);
                                        viewHolder.bookName.setText(post.bookName);
                                        arrPost[0] = post;
                                    }
                                } else { //  可能被刪掉了
                                    dbRef.child("users").child(getUid()).child("liked").child(bookKey).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "MainActivity: loadMainList(): DB: onCancelled: " + databaseError.getMessage());

                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(arrPost[0].bookName + "\n\n" + arrPost[0].bookInfo)
                                        .create().show();
                            }
                        });
                        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                viewHolder.btnRemove.startAnimation(myAnim);

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(R.string.removeTitle_main);
                                builder.setMessage(R.string.removeMessage_main);
                                builder.setPositiveButton(R.string.txtYes_main, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if (!FastClickSensor.isFastDoubleClick()) {
                                            dbRef.child("post").child(bookKey).runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    Post post = mutableData.getValue(Post.class);
                                                    if (post == null) {
                                                        return Transaction.success(mutableData);
                                                    }
                                                    if (post.likers.containsKey(getUid())) {
                                                        post.starCount = post.starCount - 1;
                                                        post.likers.remove(getUid());
                                                        dbRef.child("users").child(getUid()).child("liked").child(bookKey).removeValue();
                                                        dbRef.child("users").child(getUid()).child("main").child(bookKey).setValue(postTime);

                                                        dbRef.child("users").child(getUid()).child("link").child(arrPost[0].userId).child(bookKey).removeValue();

                                                    } else {
                                                        Log.e(TAG, "MainActivity: loadLikedList: AlertDialog: remove: likers doesn't containsKey");
                                                    }

                                                    mutableData.setValue(post);
                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                    if (databaseError == null) {
                                                        Log.i(TAG, "MainActivity: loadLikedList: doTransaction: onComplete: ");
                                                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.e(TAG, "MainActivity: loadLikedList: AlertDialog: onComplete: error = " + databaseError);
                                                    }
                                                }
                                            });
                                        }

                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }
                        });
                    }
                };

        recyclerView.setAdapter(adapter);


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

    public static class LikedItemViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_liked;

        CircleImageView imgUser;
        TextView userName;
        TextView bookName;
        Button btnRemove;

        public LikedItemViewHolder(View view) {

            super(view);
            imgUser = (CircleImageView) view.findViewById(R.id.imgUser_item_like);
            userName = (TextView) view.findViewById(R.id.userName_item_like);
            bookName = (TextView) view.findViewById(R.id.bookName_item_like);
            btnRemove = (Button) view.findViewById(R.id.btnRemove_item_like);
        }
    }


}


