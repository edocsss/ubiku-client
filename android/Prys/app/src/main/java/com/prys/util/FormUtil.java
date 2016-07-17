package com.prys.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.EditText;

import com.prys.R;

public class FormUtil {
    private static FormUtil instance;

    public static FormUtil getInstance () {
        if (instance == null) {
            instance = new FormUtil();
        }

        return instance;
    }

    public void setErrorMessage(TextInputLayout til, String message) {
        til.setError(message);
        til.setErrorEnabled(true);
    }

    public void unsetErrorMessage(Context context, TextInputLayout til) {
        til.setError(null);
        til.setErrorEnabled(false);

        // This is a hack to set back the underline color after error!
        EditText e = til.getEditText();
        if (e != null) {
            Drawable bg = e.getBackground();
            if (bg != null) {
                bg.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void setEditTextFocus (Activity a, EditText e) {
        // Set focus and display keyboard
        if (e.requestFocus()) {
            a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
