package com.expixel.binki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cellbody on 2016/10/27.
 */

public class HidedListActivity extends BaseActivity {
    @BindView(R.id.toolbar_hided)
    Toolbar toolbar;
    @BindView(R.id.appBar_hided)
    AppBarLayout appBar;
    @BindView(R.id.recyclerView_hided)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_hided_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hided List");

    }
}
