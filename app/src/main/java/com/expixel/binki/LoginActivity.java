package com.expixel.binki;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {

    @BindView(R.id.btn_login_google_sign_in)
    SignInButton btnLoginGoogleSignIn;

    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth.AuthStateListener authListener;

    //    記得在網站上啟用google 供應商
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Google Config
        setContentView(R.layout.ac_login);
        ButterKnife.bind(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                        // be available.
                        Log.e(TAG, "onConnectionFailed:" + connectionResult);
                        Toast.makeText(LoginActivity.this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//        Set AuthListener
        authListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "LoginActivity: onAuthStateChanged: signed_IN: " + user.getUid());
                } else {
                    Log.w(TAG, "LoginActivity: onAuthStateChanged: signed_OUT ");
                }
            }
        };



        if (auth.getCurrentUser() != null) {
            Log.v(TAG, "LoginActivity: onAuthStateChanged: signed_IN: " + getUid());
            analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, analyticParams);
            nextActivity();
        } else {
            Log.w(TAG, "LoginActivity: onAuthStateChanged: signed_OUT ");
        }


    }

    protected void nextActivity() {
        Log.i(TAG, "LoginActivity: nextActivity: ");
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_login_google_sign_in)
    public void onClick() {
        Log.d(TAG, "LoginActivity: onClickSignIn: ");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            analytics.logEvent(FirebaseAnalytics.Param.SIGN_UP_METHOD, analyticParams);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            LoginActivity.this.startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Log.w(TAG, "LoginActivity: onClickSignIn: googleApiClient is null ? " + googleApiClient.toString());
            Log.w(TAG, "LoginActivity: onClickSignIn: mGoogleApiClient connect ? " + googleApiClient.isConnected());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "LoginActivity: onActivityResult: FAIL: "+result.toString());
                Toast.makeText(this, "Oops...Try again plz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.v(TAG, "LoginActivity: firebaseAuthWithGoogle: start: " + acct.getId());

        showProgressDialog("Loading...");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "LoginActivity:: signInWithCredential: onComplete: isSuccess? " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            String name = getUserName();
                            String imgUrl = getUserImgeUrl();

                            Map<String, Object> updateProfile = new HashMap<String, Object>();
                            updateProfile.put("name", name);
                            updateProfile.put("imgUrl", imgUrl);

                            dbRef.child("users").child(getUid()).updateChildren(updateProfile);

                            dbRef.child("users").child(getUid()).child("lastLoadTime").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "LoginActivity: lastLoadTime == " + dataSnapshot.getValue());
                                    if (dataSnapshot.getValue() == null) {//    第一次登入
                                        Log.i(TAG, "LoginActivity: start: 初始化客製化書單");
                                        dbRef.child("post").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.i(TAG, "LoginActivity: Download and Upload: data have "+dataSnapshot.getChildrenCount());
                                                Long lastLoadTime = Long.valueOf(0);
                                                Map<String, Long> mainValues = new HashMap<>();
                                                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                                                    // (key, postTime)
                                                    mainValues.put(postData.getKey(), postData.child("postTime").getValue(Long.class));
                                                    //  記錄最後一筆
                                                    lastLoadTime = postData.child("postTime").getValue(Long.class);
                                                }
                                                dbRef.child("users").child(getUid()).child("main").setValue(mainValues);
                                                dbRef.child("users").child(getUid()).child("lastLoadTime").setValue(lastLoadTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.i(TAG, "LoginActivity: set lastLoadTime: onComplete: ");

                                                        analytics.logEvent("finish_sign_up", analyticParams);

                                                        LoginActivity.this.nextActivity();
                                                        hideProgressDialog();
                                                    }
                                                });

                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(LoginActivity.this, "Ops.. there's something wrong.\nPlease try again", Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "LoginActivity: Save main post: onCancelled: " + databaseError.getMessage());
                                            }
                                        });

                                    }else {//   登入過就跳過吧
                                        LoginActivity.this.nextActivity();
                                        hideProgressDialog();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "LoginActivity:loadLastTime: onCancelled: "+databaseError.getMessage());
                                }
                            });




                        } else {
                            Log.e(TAG, "signInWithCredential FAIL: ", task.getException());
                            Toast.makeText(LoginActivity.this, "Ops.. there's something wrong.\nPlease try again ", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}

