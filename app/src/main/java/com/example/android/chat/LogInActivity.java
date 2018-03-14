package com.example.android.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.email_login) EditText email;
    @BindView(R.id.password_login) EditText password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        if(FirebaseHelper.getInstance().getUserId() != null){
            openMain();
        }
    }

    @OnClick(R.id.login_btn)
    public void logIn(View view) {
        final String LOGIN_EMAIL_CONTENT, LOGIN_PASSWORD_CONTENT;
        LOGIN_EMAIL_CONTENT = email.getText().toString().trim();
        LOGIN_PASSWORD_CONTENT = password.getText().toString().trim();

        if (!TextUtils.isEmpty(LOGIN_EMAIL_CONTENT) && !TextUtils.isEmpty(LOGIN_PASSWORD_CONTENT)) {
            FirebaseHelper.getInstance().loginUser(LOGIN_EMAIL_CONTENT, LOGIN_PASSWORD_CONTENT, new FirebaseHelper.OnResultListener(){

                @Override
                public void onSuccess() {
                    checkUserExist();
                }

                @Override
                public void onError() {
                    Toast.makeText(LogInActivity.this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        Log.v("LogInActivity", user_id);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    openMain();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LogInActivity.this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openSignIn(View view) {
        startActivity(new Intent(LogInActivity.this, SignInActivity.class));
    }

    public void openMain(){
        startActivity(new Intent(LogInActivity.this, MainActivity.class));
    }

    @OnClick(R.id.signin_btn)
    public void openGoogleSignin(View view){
        startActivity(new Intent(LogInActivity.this, GoogleSigninActivity.class));
    }


}
