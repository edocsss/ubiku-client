package com.prys.authentication;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.prys.PrysApp;

public class UserAuthenticator {
    private final String TAG = "UserAuthenticator";
    private static UserAuthenticator instance = null;
    private FirebaseAuth mFirebaseAuthenticator = null;

    private UserAuthenticator () {
        this.mFirebaseAuthenticator = FirebaseAuth.getInstance();
    }

    private static String getDeviceId () {
        return Settings.Secure.getString(PrysApp.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static UserAuthenticator getInstance () {
        if (instance == null) instance = new UserAuthenticator();
        return instance;
    }

    public void registerUserByEmail (final String fullName,
                                     final String email,
                                     final String password,
                                     final IFirebaseRegistrationCallbacks callbackHandler) {

        this.mFirebaseAuthenticator.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    callbackHandler.onFirebaseRegistrationFailed(task.getException());
                } else {
                    // Firebase Registration DOES NOT have initial display name
                    FirebaseUser user = mFirebaseAuthenticator.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build();

                    if (user != null) {
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callbackHandler.onFirebaseRegistrationSuccessful();
                                } else {
                                    callbackHandler.onFirebaseRegistrationFailed(task.getException());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void loginUserByEmail (final String email,
                                  final String password,
                                  final IFirebaseLoginCallbacks callbackHandler) {

        this.mFirebaseAuthenticator.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    callbackHandler.onFirebaseLoginFailed(task.getException());
                } else {
                    callbackHandler.onFirebaseLoginSuccessful();
                }
            }
        });

    }

    public void handleFacebookAccessToken (AccessToken accessToken, final IFirebaseLoginCallbacks callbackHandler) {
        Log.d(TAG, "handleFacebookAccessToken: " + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        this.mFirebaseAuthenticator
                .signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callbackHandler.onFirebaseLoginSuccessful();
                        } else {
                            callbackHandler.onFirebaseLoginFailed(task.getException());
                        }
                    }
                });
    }

    public void logoutUser () {
        this.mFirebaseAuthenticator.signOut();
        LoginManager.getInstance().logOut(); // Otherwise, the Facebook Login Button will have the "Logout" text (which is weird)
    }
}
