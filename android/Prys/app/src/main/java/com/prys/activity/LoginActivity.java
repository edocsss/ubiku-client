package com.prys.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.prys.R;
import com.prys.authentication.IFirebaseLoginCallbacks;
import com.prys.authentication.UserAuthenticator;
import com.prys.util.ActivityUtil;
import com.prys.util.FormUtil;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    private EditText emailField;
    private EditText passwordField;
    private TextInputLayout emailTextInputLayout;
    private Button loginButton;
    private FormUtil formUtil;
    private ActivityUtil activityUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setupReference();
        this.setupToolbar();
        this.setupFormFieldListener();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        activityUtil.finishActivityWithSlideAnimation(this);
    }

    private void setupToolbar () {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.login_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setupReference () {
        this.emailField = (EditText) this.findViewById(R.id.login_email_field);
        this.passwordField = (EditText) this.findViewById(R.id.login_password_field);
        this.emailTextInputLayout = (TextInputLayout) this.findViewById(R.id.login_email_text_input_layout);
        this.loginButton = (Button) this.findViewById(R.id.login_button);
        this.formUtil = FormUtil.getInstance();
        this.activityUtil = ActivityUtil.getInstance();
    }

    private void setupFormFieldListener () {
        // http://stackoverflow.com/questions/31808157/textinputlayout-error-after-enter-value-into-edittext
        this.emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                formUtil.unsetErrorMessage(LoginActivity.this, emailTextInputLayout);
                enableLoginButton();
            }
        });

        this.passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                enableLoginButton();
            }
        });
    }

    private void enableLoginButton () {
        String email = this.emailField.getText().toString();
        String password = this.passwordField.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            this.loginButton.setEnabled(true);
        } else {
            this.loginButton.setEnabled(false);
        }
    }

    private boolean validateEmail (String email) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.formUtil.unsetErrorMessage(this, this.emailTextInputLayout);
            return true;
        } else {
            this.formUtil.setErrorMessage(this.emailTextInputLayout, this.getString(R.string.invalid_email_error));
            return false;
        }
    }

    public void loginUser (View v) {
        String email = this.emailField.getText().toString();
        String password = this.passwordField.getText().toString();

        // Invalid email
        if (!this.validateEmail(email)) {
            return;
        }

        // Hide keyboard
        this.activityUtil.hideKeyboardOnButtonClicked(this);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getString(R.string.logging_in_message));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.login_activity_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Logging in
        UserAuthenticator.getInstance().loginUserByEmail(email, password, new IFirebaseLoginCallbacks() {
            @Override
            public void onFirebaseLoginSuccessful() {
                progressDialog.dismiss();
                gotoHomeActivity();
            }

            @Override
            public void onFirebaseLoginFailed(Exception e) {
                progressDialog.dismiss();
                alertDialogBuilder.setMessage(e.getMessage()).show();
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void gotoHomeActivity () {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.activityUtil.startActivityWithSlideAnimation(this, homeIntent);
    }

    public void gotoRegisterActivity (View v) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        this.activityUtil.startActivityWithSlideAnimation(this, registerIntent);
    }

    public void gotoForgotPasswordActivity (View v) {
        Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
        this.activityUtil.startActivityWithSlideAnimation(this, forgotPasswordIntent);
    }
}
