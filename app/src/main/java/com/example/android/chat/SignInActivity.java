package com.example.android.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignInActivity extends AppCompatActivity {

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
                    Log.v(TAG, "sign in successful");
                    startActivity(new Intent(SignInActivity.this, LogInActivity.class));
                }

                @Override
                public void onError() {

                }
            });
        } else{
            Toast.makeText(SignInActivity.this, "invalid username/password", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_login)
    public void openLogin() {
        startActivity(new Intent(SignInActivity.this, LogInActivity.class));
    }


}
