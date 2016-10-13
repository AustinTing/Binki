package com.expixel.binki;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cellbody on 2016/10/06.
 */

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    protected static final String TAG = InitApp.TAG;

    protected FirebaseAuth auth;
    protected DatabaseReference dbRef;


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, this.getClass().getSimpleName() + ": onCreate");

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

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


