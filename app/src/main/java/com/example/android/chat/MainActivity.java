package com.example.android.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private TextView messageTextContent;
    private EditText messageText;
    private Button mImgButton;
    private StorageReference mStorage;
    private RecyclerView mUserMessageList;
    private RecyclerView mAdminMessageList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int GALLERY_INTENT = 2;
    private Uri downloadUri;


    private DatabaseReference mDatabaseMessages;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDatabaseMessages = FirebaseDatabase.getInstance().getReference().child("Messages");

        downloadUri = null;
        messageTextContent = findViewById(R.id.message);
        mImgButton = (Button) findViewById(R.id.image_button);
        messageText = (EditText) findViewById(R.id.message_text);
        mUserMessageList = (RecyclerView) findViewById(R.id.rec_view);
        mAdminMessageList = (RecyclerView) findViewById(R.id.admin_rec_view);
        mUserMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mUserMessageList.setLayoutManager(linearLayoutManager);
        mAdminMessageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                }
            }
        };

        mImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }


    public void sendMessage(View view) {
        final String messageValue = messageText.getText().toString().trim();

        FirebaseHelper.getInstance().sendMessage(messageValue, new FirebaseHelper.OnResultListener() {
            @Override
            public void onSuccess() {
                messageText.setText("");
            }

            @Override
            public void onError() {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            FirebaseHelper.getInstance().sendMessageWithPhoto(uri, messageText.getText().toString().trim(), new FirebaseHelper.OnResultListener() {
                @Override
                public void onSuccess() {
                    messageText.setText("");
                }

                @Override
                public void onError() {

                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseHelper.getInstance().setUserRecyclerViewData(this, mUserMessageList);
        FirebaseHelper.getInstance().setAdminRecyclerViewData(this, mAdminMessageList);
    }


    @OnClick(R.id.btn_signout)
    public void signOut() {
        FirebaseHelper.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LogInActivity.class));
        finish();
    }
}