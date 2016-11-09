package com.expixel.binki;

/**
 * Created by cellbody on 2016/10/7.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseDialogActivity extends Activity {
    public ProgressDialog progressDialog;
    protected static final String TAG = InitApp.TAG;

    protected FirebaseAuth auth;
    protected DatabaseReference dbRef;

    protected FirebaseAnalytics analytics;

    protected Bundle analyticParams;



    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.i(TAG, this.getClass().getSimpleName() + ": hideProgressDialog");

        }
    }


    protected String getUserName(){
        if(auth.getCurrentUser() == null){
            Log.e(TAG, this.getClass().getSimpleName()+": getUserName(): CurrentUser == NULL");
        }else if (auth.getCurrentUser().getDisplayName() == null){
            Log.e(TAG, this.getClass().getSimpleName()+": getUserName(): DisplayName == NULL");
        }

        return auth.getCurrentUser().getDisplayName();
    }

    protected String getUid(){
        if(auth.getCurrentUser() == null){
            Log.e(TAG, this.getClass().getSimpleName()+": getUid(): CurrentUser == NULL");
        }

        return auth.getCurrentUser().getUid();
    }
    protected String getUserImgeUrl(){
        if(auth.getCurrentUser() == null){
            Log.e(TAG, this.getClass().getSimpleName()+": getUserImgUrl(): CurrentUser == NULL");
        }else if (auth.getCurrentUser().getPhotoUrl() == null){
            Log.e(TAG, this.getClass().getSimpleName()+": getUserImgUrl(): ImgUrl == NULL");
        }

        return auth.getCurrentUser().getPhotoUrl().toString();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, this.getClass().getSimpleName() + ": onCreate");

        auth = FirebaseAuth.getInstance();
        if (auth == null) Log.w(TAG, this.getClass().getSimpleName() + ": onCreate: auth == null");
        dbRef = FirebaseDatabase.getInstance().getReference();
        if(dbRef == null) Log.w(TAG, this.getClass().getSimpleName() + ": onCreate: dbRef == null");

        analytics = FirebaseAnalytics.getInstance(this);
        if(analytics == null) Log.w(TAG, this.getClass().getSimpleName() + ": onCreate: analytics == null");


        analyticParams = new Bundle();
        analyticParams.putString(FirebaseAnalytics.Param.START_DATE, String.valueOf(System.currentTimeMillis()));


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, this.getClass().getSimpleName() + ": onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, this.getClass().getSimpleName() + ": onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, this.getClass().getSimpleName() + ": onPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, this.getClass().getSimpleName() + ": onStop");
//
        hideProgressDialog();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, this.getClass().getSimpleName() + ": onDestroy");
    }


}