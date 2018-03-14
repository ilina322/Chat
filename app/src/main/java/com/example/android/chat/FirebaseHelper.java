package com.example.android.chat;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

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

/**
 * Created by Me on 3.2.2018 г..
 */

public class FirebaseHelper {

    private static final String TABLE_NAME_USERS = "Users";
    private static final String TABLE_NAME_MESSAGES = "Messages";
    private static final String TABLE_PHOTOS = "Photos";

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_NAME_IS_ADMIN = "isAdmin";
    private static final String FIELD_IMAGE_PATH = "imagePath";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_IS_SENT_BY_ADMIN = "isSentByAdmin";

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
        Query databaseMessagesOnlyUser = messagesDatabaseReference.orderByChild(FIELD_IS_SENT_BY_ADMIN).equalTo(false);
        return databaseMessagesOnlyUser;

    }

    public Query getAdminDataReference() {
        Query databaseMessagesOnlyAdmin = messagesDatabaseReference.orderByChild(FIELD_IS_SENT_BY_ADMIN).equalTo(true).limitToLast(3);
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
        StorageReference filepath = storage.child(TABLE_PHOTOS).child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                sendMessage(downloadUrl, messageValue, listener);
            }
        });
    }

    private void sendMessage(final Uri downloadUri, final String messageValue, final OnResultListener listener) {

        if (downloadUri != null || !TextUtils.isEmpty(messageValue)) {
            DatabaseReference userReference = usersDatabaseReference.child(getUserId());
            final DatabaseReference newPostReference = messagesDatabaseReference.push();
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object isAdminRaw = dataSnapshot.child(FIELD_NAME_IS_ADMIN).getValue();
                    final String USERNAME = dataSnapshot.child(FIELD_USERNAME).getValue().toString();
                    if (isAdminRaw != null) {
                        boolean isAdmin = Integer.parseInt(isAdminRaw.toString()) == 1;
                        newPostReference.child(FIELD_IS_SENT_BY_ADMIN).setValue(isAdmin);
                    } else {
                        newPostReference.child(FIELD_IS_SENT_BY_ADMIN).setValue(false);
                    }
                    if (downloadUri != null) {
                        newPostReference.child(FIELD_IMAGE_PATH).setValue(downloadUri.toString());
                    }
                    newPostReference.child(FIELD_CONTENT).setValue(messageValue);
                    newPostReference.child(FIELD_USERNAME).setValue(USERNAME);
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
