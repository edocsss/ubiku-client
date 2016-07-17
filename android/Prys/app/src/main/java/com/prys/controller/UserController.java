package com.prys.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.prys.PrysApp;
import com.prys.model.User;

public class UserController {
    // Handle setUser, getUserInfo, removeUser, deleteUser, updateUserProfile, etc. --> everything is stored in the SharedPreferences --> so it is not removed when the apps is closed
    private static final String USER_EMAIL_SETTING_KEY = "user_email";
    private static final String USER_FULLNAME_SETTING_KEY = "user_fullName";

    private static UserController instance = null;
    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor settingEditor = null;

    private UserController () {
        settings = PrysApp.getContext().getSharedPreferences(PrysApp.APP_NAME, Context.MODE_PRIVATE);
        settingEditor = settings.edit();
    }

    public static UserController getInstance () {
        if (instance == null) instance = new UserController();
        return instance;
    }

    public void setUserInformation (User user) {
        settingEditor.putString(USER_EMAIL_SETTING_KEY, user.getEmail());
        settingEditor.putString(USER_FULLNAME_SETTING_KEY, user.getFullName());
        settingEditor.commit();
    }

    public User getUserInformation () {
        String email = settings.getString(USER_EMAIL_SETTING_KEY, null);
        String fullName = settings.getString(USER_FULLNAME_SETTING_KEY, null);

        return new User(fullName, email);
    }

    public void removeUserInformation () {
        settingEditor.remove(USER_EMAIL_SETTING_KEY);
        settingEditor.remove(USER_FULLNAME_SETTING_KEY);
        settingEditor.commit();
    }
}
