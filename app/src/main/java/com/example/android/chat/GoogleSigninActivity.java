package com.example.android.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoogleSigninActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.email_signin_btn)
    public void openSignin()  {
        startActivity(new Intent(GoogleSigninActivity.this, SignInActivity.class));
    }

    @OnClick(R.id.btn_google_signin)
    public void signIn() {
        Intent signInIntent = AuthenticationHelper.getInstance(this).getGoogleSigninIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            AuthenticationHelper.getInstance(this).handleGoogleSignin(data, new AuthenticationHelper.OnResultListener() {
                @Override
                public void onSuccess() {
                    openMain();
                }

                @Override
                public void onError() {
                    Snackbar.make(findViewById(R.id.reg_form), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void openMain() {
        startActivity(new Intent(GoogleSigninActivity.this, MainActivity.class));
    }
}
