package com.prys.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.prys.R;

public class ActivityUtil {
    private static ActivityUtil instance;

    public static ActivityUtil getInstance () {
        if (instance == null) {
            instance = new ActivityUtil();
        }

        return instance;
    }

    // http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    public void hideKeyboardOnButtonClicked (Activity a) {
        View v = a.getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) a.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void startActivityWithSlideAnimation (Activity a, Intent intent) {
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void finishActivityWithSlideAnimation (Activity a) {
        a.finish();
        a.overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
