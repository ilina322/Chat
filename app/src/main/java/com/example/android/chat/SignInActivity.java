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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {

    private EditText email, username, password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        username = (EditText) findViewById(R.id.username_signin);
        password = (EditText) findViewById(R.id.password_signin);
        email = (EditText) findViewById(R.id.email_signin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void signIn(View view) {
        final String USERNAME_CONTENT, PASSWORD_CONTENT, EMAIL_CONTENT;
        USERNAME_CONTENT = username.getText().toString().trim();
        PASSWORD_CONTENT = password.getText().toString().trim();
        EMAIL_CONTENT = email.getText().toString().trim();

        if (!TextUtils.isEmpty(USERNAME_CONTENT) && !TextUtils.isEmpty(PASSWORD_CONTENT) && !TextUtils.isEmpty(EMAIL_CONTENT)) {
            mAuth.createUserWithEmailAndPassword(EMAIL_CONTENT, PASSWORD_CONTENT).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference curr_user_database = mDatabase.child(user_id);
                                curr_user_database.child("Username").setValue(USERNAME_CONTENT);
                                Log.v("SignInActivity", "sign in successful");
                                startActivity(new Intent(SignInActivity.this, LogInActivity.class));
                    } else {
                                Toast.makeText(SignInActivity.this, "invalid username/password", Toast.LENGTH_LONG).show();
                            };
                }
            });
        }
    }

    public void openLogIn(View view){
        startActivity(new Intent(SignInActivity.this, LogInActivity.class));
    }
}
