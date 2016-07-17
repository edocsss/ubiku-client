package com.prys.authentication;

/**
 * Created by edocsss on 10/7/16.
 */
public interface IFirebaseRegistrationCallbacks {
    public void onFirebaseRegistrationSuccessful ();
    public void onFirebaseRegistrationFailed (Exception e);
}
