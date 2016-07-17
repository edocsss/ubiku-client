package com.prys.authentication;

/**
 * Created by edocsss on 10/7/16.
 */
public interface IFirebaseLoginCallbacks {
    public void onFirebaseLoginSuccessful ();
    public void onFirebaseLoginFailed (Exception e);
}
