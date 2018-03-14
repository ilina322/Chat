package com.example.android.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignInActivity extends AppCompatActivity {

    public static final String INVALID_USERNAME_PASSWORD = "invalid username/password";
    public static final String ON_SUCCESS_MESSAGE = "sign in successful";
    public static final String ON_ERROR_MESSAGE = "sign in error";
    private final String TAG = "SignInActivity";

    @BindView(R.id.email_signin)
    EditText email;
    @BindView(R.id.username_signin)
    EditText username;
    @BindView(R.id.password_signin)
    EditText password;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_signin)
    public void signIn(View view) {
        final String USERNAME_CONTENT, PASSWORD_CONTENT, EMAIL_CONTENT;
        USERNAME_CONTENT = username.getText().toString().trim();
        PASSWORD_CONTENT = password.getText().toString().trim();
        EMAIL_CONTENT = email.getText().toString().trim();

        if (!TextUtils.isEmpty(USERNAME_CONTENT) && !TextUtils.isEmpty(PASSWORD_CONTENT) && !TextUtils.isEmpty(EMAIL_CONTENT)) {
            FirebaseHelper.getInstance().createUser(EMAIL_CONTENT, PASSWORD_CONTENT, new FirebaseHelper.OnResultListener() {
                @Override
                public void onSuccess() {
                    FirebaseHelper.getInstance().setUsername(USERNAME_CONTENT);
                    Log.v(TAG, ON_SUCCESS_MESSAGE);
                    startActivity(new Intent(SignInActivity.this, LogInActivity.class));
                }

                @Override
                public void onError() {
                    Log.v(TAG, ON_ERROR_MESSAGE);
                }
            });
        } else{
            Toast.makeText(SignInActivity.this, INVALID_USERNAME_PASSWORD, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_login)
    public void openLogin() {
        startActivity(new Intent(SignInActivity.this, LogInActivity.class));
    }


}
