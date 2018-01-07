package com.example.android.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText messageText;
    private FloatingActionButton mImgButton;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseMessages;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrUser;
    private RecyclerView mMessageList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int GALLERY_INTENT = 2;
    private Uri downloadUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        downloadUri = null;
        mStorage = FirebaseStorage.getInstance().getReference();
        mImgButton = (FloatingActionButton) findViewById(R.id.img_fab);
        messageText = (EditText) findViewById(R.id.message_text);
        mDatabaseMessages = FirebaseDatabase.getInstance().getReference().child("Messages");
        mMessageList = (RecyclerView) findViewById(R.id.rec_view);
        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);
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

        mCurrUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrUser.getUid());

        if (!TextUtils.isEmpty(messageValue) || downloadUri != null) {
            final DatabaseReference NEW_POST = mDatabaseMessages.push();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("MainActivity",dataSnapshot.child("Username").getValue().toString());
                    final String USERNAME = dataSnapshot.child("Username").getValue().toString();
                    NEW_POST.child("content").setValue(messageValue);
                    if(downloadUri != null) {
                        NEW_POST.child("imagePath").setValue(downloadUri.toString());
                        downloadUri = null;
                    }
                    NEW_POST.child("username").setValue(USERNAME);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mMessageList.scrollToPosition(mMessageList.getAdapter().getItemCount());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUri = taskSnapshot.getDownloadUrl();
                    Log.v("MainActivity", downloadUri.toString());
                    Toast.makeText(MainActivity.this, "Upload finished", Toast.LENGTH_LONG).show();
                    sendMessage(findViewById(R.id.img_fab));
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message_layout,
                MessageViewHolder.class,
                mDatabaseMessages
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message msg, int position) {
                viewHolder.setContent(msg.getContent());
                viewHolder.setImage(MainActivity.this, msg.getImagePath());
                viewHolder.setUsername(msg.getUsername());
                viewHolder.setTime(new Date().getTime());
            }
        };

        mMessageList.setAdapter(adapter);
    }


    public void signOut(View view) {
        startActivity(new Intent(MainActivity.this, LogInActivity.class));
    }
}
