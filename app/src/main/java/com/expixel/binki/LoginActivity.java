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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
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
                    nextActivity();
                    Log.d(TAG, "LoginActivity: onAuthStateChanged: signed_IN: " + user.getUid());
                } else {
                    Log.d(TAG, "LoginActivity: onAuthStateChanged: signed_OUT " + firebaseAuth.toString());
                }
            }
        };

    }

    @OnClick(R.id.btn_login_google_sign_in)
    public void onClick() {
        Log.d(TAG, "LoginActivity: onClickSignIn: ");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            LoginActivity.this.startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Log.d(TAG, "LoginActivity: onClick: mGoogleApiClient is null ? " + googleApiClient.toString());
            Log.d(TAG, "LoginActivity: onClickSignin: mGoogleApiClient connect ? " + googleApiClient.isConnected());
        }
    }


    protected void nextActivity() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
                Toast.makeText(this, "Oops...Try again plz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog("Loading...");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Firebase: signInWithCredential: onComplete: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            String name = getUserName();
                            String imgUrl = getUserImgeUrl();
                            User user = new User(name, imgUrl);
                            final String uid = getUid();
                            dbRef.child("users").child(uid).setValue(user);

//                            初始化客製化書單
                            dbRef.child("post").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "MainActivity: Download and Upload: data have " + dataSnapshot.getChildrenCount() + "children");
                                    dbRef.child("users").child(uid).child("main").setValue(dataSnapshot.getValue());

                                    dbRef.child("users").child(uid).child("main").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postData : dataSnapshot.getChildren()) {
                                                Post post = postData.getValue(Post.class);
                                                dbRef.child("users").child(uid).child("lastLoad").setValue(post.postTime);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e(TAG, "LoginActivity: Save lastLoad: " + databaseError.getMessage());
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "LoginActivity: Save main post: " + databaseError.getMessage());
                                }
                            });


                            LoginActivity.this.nextActivity();
                            LoginActivity.this.finish();
                        } else {
                            Log.w(TAG, "signInWithCredential", task.getException());
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

