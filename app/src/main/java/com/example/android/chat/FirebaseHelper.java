package com.example.android.chat;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

/**
 * Created by Me on 3.2.2018 г..
 */

public class FirebaseHelper {

    public static final String TABLE_NAME_USERS = "Users";
    public static final String FIELD_USERNAME = "Username";
    public static final String TABLE_NAME_MESSAGES = "Messages";
    public static final String CHILD_NAME_IS_ADMIN = "isAdmin";
    public static final String CHILD_NAME_USERNAME = "Username";
    private final FirebaseUser currentUser;
    private static FirebaseHelper instance;
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference usersDatabaseReference;
    private final DatabaseReference messagesDatabaseReference;
    private final StorageReference storage;

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    private FirebaseHelper() {
        storage = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME_USERS);
        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME_MESSAGES);
    }

    public Query getUserDataReference() {
        Query databaseMessagesOnlyUser = messagesDatabaseReference.orderByChild("isSentByAdmin").equalTo(false);
        return databaseMessagesOnlyUser;

    }

    public Query getAdminDataReference() {
        Query databaseMessagesOnlyAdmin = messagesDatabaseReference.orderByChild("isSentByAdmin").equalTo(true);
        return databaseMessagesOnlyAdmin;
    }

    public void createUser(String email, String password, final OnResultListener listener) {
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

    public String getUserId() {
        if (firebaseAuth.getCurrentUser() == null) {
            return null;
        }
        return firebaseAuth.getCurrentUser().getUid();
    }

    public void signOut() {
        firebaseAuth.signOut();
    }


    public void setUsername(String username) {
        DatabaseReference curr_user_database = usersDatabaseReference.child(getUserId());
        curr_user_database.child(FIELD_USERNAME).setValue(username);
    }

    public void loginUser(String login_email_content, String login_password_content, final OnResultListener onResultListener) {
        firebaseAuth.signInWithEmailAndPassword(login_email_content, login_password_content).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    onResultListener.onSuccess();
                } else {
                    onResultListener.onError();
                }
            }
        });
    }

    public void sendMessage(String messageValue, OnResultListener listener) {
        sendMessage(null, messageValue, listener);
    }

    public void sendMessageWithPhoto(final Uri uri, final String messageValue, final OnResultListener listener) {
        StorageReference filepath = storage.child("Photos").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {;
                sendMessage(uri, messageValue, listener);
            }
        });
    }

    private void sendMessage(final Uri downloadUri, final String messageValue, final OnResultListener listener) {

        if (downloadUri != null || !TextUtils.isEmpty(messageValue)) {
            DatabaseReference userReference = usersDatabaseReference.child(currentUser.getUid());
            final DatabaseReference newPostReference = messagesDatabaseReference.push();
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object isAdminRaw = dataSnapshot.child(CHILD_NAME_IS_ADMIN).getValue();
                    Log.v("FirebaseHelper", dataSnapshot.child(CHILD_NAME_USERNAME).getValue().toString());
                    final String USERNAME = dataSnapshot.child(CHILD_NAME_USERNAME).getValue().toString();
                    if (isAdminRaw != null) {
                        boolean isAdmin = Integer.parseInt(isAdminRaw.toString()) == 1;
                        newPostReference.child("isSentByAdmin").setValue(isAdmin);
                    } else {
                        newPostReference.child("isSentByAdmin").setValue(false);
                    }
                    if (downloadUri != null) {
                        newPostReference.child("imagePath").setValue(downloadUri.toString());
                    }
                    newPostReference.child("content").setValue(messageValue);
                    newPostReference.child("username").setValue(USERNAME);
                    listener.onSuccess();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onError();
                }
            });
        }
    }


    public interface OnResultListener {
        void onSuccess();

        void onError();
    }
}
