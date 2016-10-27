package com.expixel.binki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

public class HiddenListActivity extends BaseActivity {
    @BindView(R.id.toolbar_hided)
    Toolbar toolbar;
    @BindView(R.id.appBar_hided)
    AppBarLayout appBar;
    @BindView(R.id.recyclerView_hided)
    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_hided_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hidden Books");
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

        FirebaseRecyclerAdapter<Long, HidedItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Long, HidedItemViewHolder>(
                        Long.class,
                        HidedItemViewHolder.layoutResId,
                        HidedItemViewHolder.class,
                        dbRef.child("users").child(getUid()).child("hided")

                ) {
                    @Override
                    protected void populateViewHolder(final HidedItemViewHolder viewHolder, final Long postTime, int position) {
                        final String bookKey = getRef(position).getKey();
                        Log.d(TAG, "HidedListActivity: populateViewHolder: key"+bookKey);
                        dbRef.child("post").orderByKey().equalTo(bookKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Post post = postSnapshot.getValue(Post.class);
                                        viewHolder.userName.setText(post.userName);
                                        //  http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0823/3353.html
                                        Glide.with(HiddenListActivity.this.getApplicationContext())
                                                .load(post.userImg)
                                                .crossFade()
                                                .into(viewHolder.imgUser);
                                        viewHolder.bookName.setText(post.bookName);
                                    }
                                } else { //  可能被刪掉了
                                    dbRef.child("users").child(getUid()).child("hided").child(bookKey).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Animation myAnim = AnimationUtils.loadAnimation(HiddenListActivity.this, R.anim.bounce);
                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                viewHolder.btnRemove.startAnimation(myAnim);

                                dbRef.child("users").child(getUid()).child("hided").child(bookKey).removeValue();
                                dbRef.child("users").child(getUid()).child("main").child(bookKey).setValue(postTime);


                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);

    }

    public static class HidedItemViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_liked;

        CircleImageView imgUser;
        TextView userName;
        TextView bookName;
        Button btnRemove;

        public HidedItemViewHolder(View view) {

            super(view);
            imgUser = (CircleImageView) view.findViewById(R.id.imgUser_item_like);
            userName = (TextView) view.findViewById(R.id.userName_item_like);
            bookName = (TextView) view.findViewById(R.id.bookName_item_like);
            btnRemove = (Button) view.findViewById(R.id.btnRemove_item_like);
        }
    }
}
