package com.example.android.chat;


import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email_login);
        password = (EditText) findViewById(R.id.password_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void logIn(View view) {
        final String LOGIN_EMAIL_CONTENT, LOGIN_PASSWORD_CONTENT;
        LOGIN_EMAIL_CONTENT = email.getText().toString().trim();
        LOGIN_PASSWORD_CONTENT = password.getText().toString().trim();

        if (!TextUtils.isEmpty(LOGIN_EMAIL_CONTENT) && !TextUtils.isEmpty(LOGIN_PASSWORD_CONTENT)) {
            mAuth.signInWithEmailAndPassword(LOGIN_EMAIL_CONTENT, LOGIN_PASSWORD_CONTENT).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserExist();
                    }
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
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(LogInActivity.this, "invalid username/password", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void openSignIn(View view) {
        startActivity(new Intent(LogInActivity.this, SignInActivity.class));
    }


}
