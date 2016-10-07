package com.expixel.binki;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

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
        DialogPlus dialogPuls = DialogPlus.newDialog(this)
                .setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"asdfa"}))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogPuls.show();
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseRecyclerAdapter<Event, ItemViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Event, ItemViewHolder>(
//                        Event.class,
//                        ItemViewHolder.layoutResId,
//                        ItemViewHolder.class,
//                        dbRef.child("post")
//                ) {
//                    @Override
//                    protected void populateViewHolder(ItemViewHolder viewHolder, Event event, int position) {
//                        ImageLoader.getInstance().displayImage(event.getUserImgUrl(), viewHolder.userImage);
//                        //  取得這個item的key
//                        getRef(i).getKey()
//                    }
//                };
//        recyclerView.setAdapter(adapter);
//    }
//
//
//    public static class ItemViewHolder extends RecyclerView.ViewHolder {
//
//        public final static int layoutResId = R.layout.ac_main_item;
//        CircleImageView userImg;
//
//        public ItemViewHolder(View view) {
//            super(view);
//            userImg = (CircleImageView) view.findViewById(R.id.userImage);
//        }
//}
}
