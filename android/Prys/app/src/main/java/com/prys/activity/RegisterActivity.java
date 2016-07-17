package com.prys.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
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

import com.prys.R;
import com.prys.authentication.IFirebaseRegistrationCallbacks;
import com.prys.authentication.UserAuthenticator;
import com.prys.constant.Regex;
import com.prys.util.ActivityUtil;
import com.prys.util.FormUtil;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";
    private TextInputEditText fullNameField;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private TextInputLayout fullNameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private Button registerButton;
    private FormUtil formUtil;
    private ActivityUtil activityUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setup references
        this.setupReference();

        // Setup toolbar
        this.setupToolbar();

        // Setup listeners
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
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.register_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setupReference () {
        this.fullNameField = (TextInputEditText) this.findViewById(R.id.register_fullname_field);
        this.emailField = (TextInputEditText) this.findViewById(R.id.register_email_field);
        this.passwordField = (TextInputEditText) this.findViewById(R.id.register_password_field);
        this.fullNameTextInputLayout = (TextInputLayout) this.findViewById(R.id.register_fullname_text_input_layout);
        this.emailTextInputLayout = (TextInputLayout) this.findViewById(R.id.register_email_text_input_layout);
        this.passwordTextInputLayout = (TextInputLayout) this.findViewById(R.id.register_password_text_input_layout);
        this.registerButton = (Button) this.findViewById(R.id.register_button);
        this.formUtil = FormUtil.getInstance();
        this.activityUtil = ActivityUtil.getInstance();
    }

    private void setupFormFieldListener () {
        this.fullNameField.addTextChangedListener(this.buildTextWatcherForEditTextField(fullNameTextInputLayout));
        this.emailField.addTextChangedListener(this.buildTextWatcherForEditTextField(emailTextInputLayout));
        this.passwordField.addTextChangedListener(this.buildTextWatcherForEditTextField(passwordTextInputLayout));
    }

    private TextWatcher buildTextWatcherForEditTextField (final TextInputLayout til) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                formUtil.unsetErrorMessage(RegisterActivity.this, til);
                enableRegisterButton();
            }
        };
    }

    private boolean validateFullName (String fullName) {
        if (fullName.matches(Regex.USER_FULL_NAME)) {
            this.formUtil.unsetErrorMessage(this, this.fullNameTextInputLayout);
            return true;
        } else {
            this.formUtil.setErrorMessage(this.fullNameTextInputLayout, this.getString(R.string.invalid_fullname));
            return false;
        }
    }

    private boolean validateEmail (String email) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()) {
            this.formUtil.unsetErrorMessage(this, this.emailTextInputLayout);
            return true;
        } else {
            this.formUtil.setErrorMessage(this.emailTextInputLayout, this.getString(R.string.invalid_email));
            return false;
        }
    }

    private boolean validatePassword (String password) {
        if (password.matches(Regex.USER_PASSWORD) && !password.isEmpty()) {
            this.formUtil.unsetErrorMessage(this, this.passwordTextInputLayout);
            return true;
        } else {
            this.formUtil.setErrorMessage(this.passwordTextInputLayout, this.getString(R.string.invalid_password));
            return false;
        }
    }

    private void enableRegisterButton () {
        String fullName = this.fullNameField.getText().toString();
        String email = this.emailField.getText().toString();
        String password = this.passwordField.getText().toString();

        if (!fullName.isEmpty() && !email.isEmpty() && !password.isEmpty()) this.registerButton.setEnabled(true);
        else this.registerButton.setEnabled(false);
    }

    public void registerUser (View v) {
        String fullName = this.fullNameField.getText().toString();
        String email = this.emailField.getText().toString();
        String password = this.passwordField.getText().toString();

        // Make sure that all the error messages are set on the TextInputLayout in one go and then return
        boolean checkValidateFullName = this.validateFullName(fullName);
        boolean checkValidateEmail = this.validateEmail(email);
        boolean checkValidatePassword = this.validatePassword(password);

        // Set focus to the very first field that has the error
        if (!checkValidateFullName) this.formUtil.setEditTextFocus(this, this.fullNameField);
        else if (!checkValidateEmail) this.formUtil.setEditTextFocus(this, this.emailField);
        else if (!checkValidatePassword) this.formUtil.setEditTextFocus(this, this.passwordField);

        // Do nothing if there is an error
        if (!checkValidateFullName || !checkValidateEmail || !checkValidatePassword) return;

        // Hide keyboard
        this.activityUtil.hideKeyboardOnButtonClicked(this);

        // Register user request
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getString(R.string.creating_account));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this)
                .setCancelable(false)
                .setTitle(R.string.register_activity_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        UserAuthenticator.getInstance().registerUserByEmail(fullName, email, password, new IFirebaseRegistrationCallbacks() {
            @Override
            public void onFirebaseRegistrationSuccessful () {
                progressDialog.dismiss();
                Intent homeIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activityUtil.startActivityWithSlideAnimation(RegisterActivity.this, homeIntent);
            }

            @Override
            public void onFirebaseRegistrationFailed(Exception e) {
                progressDialog.dismiss();
                alertDialogBuilder.setMessage(e.getMessage()).show();
                Log.e(TAG, e.getMessage());
            }
        });
    }
}
