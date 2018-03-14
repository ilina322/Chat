package com.example.android.chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

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

/**
 * Created by Me on 2.3.2018 г..
 */

public class AuthenticationHelper {

    private static final String TAG = "AuthenticationHelper";
    public static final String ON_ERROR_MESSAGE = "Google sign in failed";
    private static AuthenticationHelper instance;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public static AuthenticationHelper getInstance(Context context) {
        if(instance == null) {
            instance = new AuthenticationHelper(context);
        }
        return instance;
    }

    private AuthenticationHelper(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public Intent getGoogleSigninIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

    public void handleGoogleSignin(Intent data, OnResultListener listener) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account, listener);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, ON_ERROR_MESSAGE, e);
            listener.onError();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final OnResultListener listener) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseHelper.getInstance().setUsername(acct.getDisplayName());
                            listener.onSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            listener.onError();
                        }

                        // ...
                    }
                });
    }

    public interface OnResultListener{
        void onSuccess();
        void onError();
    }
}
