package com.example.android.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private static final int GALLERY_INTENT = 2;
    private static final String TAG = "MainActivity";

    @BindView(R.id.grp_messages)
    ViewGroup grpMessages;
    @BindView(R.id.message_text)
    EditText edtMessage;
    @BindView(R.id.rec_view)
    RecyclerView recViewUsers;
    @BindView(R.id.admin_rec_view)
    RecyclerView recViewAdmins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //If no user is logged in, return to LogIn screen
        if (FirebaseHelper.getInstance().getUserId() == null) {
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        }

        setUpUserRecyclerView();
        setUpAdminRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            FirebaseHelper.getInstance().sendMessageWithPhoto(uri, edtMessage.getText().toString().trim(), new FirebaseHelper.OnResultListener() {
                @Override
                public void onSuccess() {
                    edtMessage.setText("");
                }

                @Override
                public void onError() {
                    showError(R.string.error_photo_sent);
                }
            });
        }
    }

    private void setUpAdminRecyclerView() {
        recViewAdmins.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.admin_message_layout,
                MessageViewHolder.class,
                FirebaseHelper.getInstance().getAdminDataReference()
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message msg, int position) {
                viewHolder.setContent(msg.getContent());
                viewHolder.setImage(MainActivity.this, msg.getImagePath());
                viewHolder.setUsername(msg.getUsername());
                viewHolder.setTime(new Date().getTime());
            }
        };

        recViewAdmins.setAdapter(adapter);
    }

    private void setUpUserRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recViewUsers.setHasFixedSize(true);
        recViewUsers.setLayoutManager(linearLayoutManager);
        FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message_layout,
                MessageViewHolder.class,
                FirebaseHelper.getInstance().getUserDataReference()
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message msg, int position) {
                viewHolder.setContent(msg.getContent());
                viewHolder.setImage(MainActivity.this, msg.getImagePath());
                viewHolder.setUsername(msg.getUsername());
                viewHolder.setTime(new Date().getTime());
            }
        };

        recViewUsers.setAdapter(adapter);
    }


    @OnClick(R.id.btn_signout)
    public void signOut() {
        FirebaseHelper.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LogInActivity.class));
        finish();
    }

    @OnClick(R.id.image_button)
    public void onSelectImageClicked() {
        if (checkPermissionForReadExtertalStorage()) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, GALLERY_INTENT);
        } else {
            requestPermissionForReadExtertalStorage();
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE },
                10);
    }

    @OnClick(R.id.message_button)
    public void sendMessage(View view) {
        final String messageValue = edtMessage.getText().toString().trim();

        FirebaseHelper.getInstance().sendMessage(messageValue, new FirebaseHelper.OnResultListener() {
            @Override
            public void onSuccess() {
                edtMessage.setText("");
                recViewUsers.scrollToPosition(recViewUsers.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onError() {
                showError(R.string.error_message_sent);
            }
        });
    }

    private void showError(int stringRes) {
        Snackbar.make(grpMessages, stringRes, Snackbar.LENGTH_SHORT).show();
    }
}