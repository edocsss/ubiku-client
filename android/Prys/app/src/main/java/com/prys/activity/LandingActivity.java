package com.prys.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.prys.R;
import com.prys.authentication.IFirebaseLoginCallbacks;
import com.prys.authentication.UserAuthenticator;
import com.prys.util.ActivityUtil;

public class LandingActivity extends AppCompatActivity {
    private final String TAG = "LandingActivity";
    private ActivityUtil activityUtil;
    private CallbackManager callbackManager;
    LoginButton loginButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        this.setupReference();
        this.setupFacebookLogin();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupReference () {
        this.activityUtil = ActivityUtil.getInstance();
        this.callbackManager = CallbackManager.Factory.create();
        this.loginButton = (LoginButton) this.findViewById(R.id.login_with_facebook_button);
    }

    private void setupFacebookLogin () {
        this.loginButton.setReadPermissions("email", "public_profile");
        this.loginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Facebook Login Successful! " + loginResult);
                UserAuthenticator.getInstance().handleFacebookAccessToken(loginResult.getAccessToken(), new IFirebaseLoginCallbacks() {
                    @Override
                    public void onFirebaseLoginSuccessful() {
                        gotoHomeActivity();
                    }

                    @Override
                    public void onFirebaseLoginFailed(Exception e) {
                        handleLoginWithFacebookError();
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook Login Cancelled!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook Login Error!", error);
                handleLoginWithFacebookError();
            }
        });
    }

    public void gotoLoginActivity (View v) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        this.activityUtil.startActivityWithSlideAnimation(this, loginIntent);
    }

    private void gotoHomeActivity () {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.activityUtil.startActivityWithSlideAnimation(this, homeIntent);
    }

    private void handleLoginWithFacebookError () {
        new AlertDialog.Builder(LandingActivity.this)
                .setCancelable(false)
                .setTitle(R.string.login_with_facebook)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage(R.string.login_with_facebook_error_message)
                .show();
    }
}
