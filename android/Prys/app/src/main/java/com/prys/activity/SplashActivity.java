package com.prys.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prys.R;
import com.prys.authentication.UserAuthenticator;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuthenticator;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private boolean firstCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserAuthenticator.getInstance().logoutUser();
        this.setupFirebase();
    }

    @Override
    protected void onStart () {
        super.onStart();
        this.mFirebaseAuthenticator.addAuthStateListener(this.mFirebaseAuthListener);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (this.mFirebaseAuthListener != null) {
            this.mFirebaseAuthenticator.removeAuthStateListener(this.mFirebaseAuthListener);
        }
    }

    private void setupFirebase () {
        this.mFirebaseAuthenticator = FirebaseAuth.getInstance();
        this.mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuthenticator.getCurrentUser();
                Intent intent;

                if (user != null) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LandingActivity.class);
                }

                // Somehow this callback is called more than once
                if (firstCheck) {
                    firstCheck = false;
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
}