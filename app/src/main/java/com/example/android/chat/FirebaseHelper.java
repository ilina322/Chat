package com.example.android.chat;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Me on 3.2.2018 г..
 */

public class FirebaseHelper {

    public static final String TABLE_NAME_USERS = "Users";
    public static final String FIELD_USERNAME = "Username";
    private static FirebaseHelper instance;
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference usersReference;

    public static FirebaseHelper getInstance(){
        if(instance == null){
            instance = new FirebaseHelper();
        }
        return instance;
    }
    private FirebaseHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME_USERS);
    }

    public void createUser(String email, String password, final OnResultListener listener){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   listener.onSuccess();
                } else {
                    listener.onError();
                }
            }
        });
    }

    public String getUserId(){
        return firebaseAuth.getCurrentUser().getUid();
    }

    public interface OnResultListener{
        void onSuccess();
        void onError();
    }

    public void setUsername(String username){
        DatabaseReference curr_user_database = usersReference.child(getUserId());
        curr_user_database.child(FIELD_USERNAME).setValue(username);
    }
}
