package com.robertreid.farm.system.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robertreid.farm.system.Chat.ChatActivity;
import com.robertreid.farm.system.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private TextView userName, userStatus;
    private CircleImageView profilePic;
    private Button sendMessage;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.profile_display_name);
        userStatus = findViewById(R.id.profile_status);
        profilePic = findViewById(R.id.profile_image);
        sendMessage = findViewById(R.id.profile_send_message);

        userId = getIntent().getStringExtra("user_id");
        //get user data
        FirebaseDatabase.getInstance().getReference().child("all_users")
                .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userName.setText(dataSnapshot.child("name").getValue().toString());
                    Glide.with(ProfileActivity.this).load(dataSnapshot.child("userpic").getValue().toString()).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendMessage.setOnClickListener(view -> {
            //navigate to chat
            checkChatId();
        });


    }


    private void checkChatId() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //PUSH CHAT ID IF NOT EXIST
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("chats");
        chatRef.child(currentUserId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    setChatIdToFirebase(chatRef, currentUserId, userId);
                } else {
                    //GET CHAT ID IF EXIST
                    getChatIdFromFirebase(chatRef, currentUserId, userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void navigateToChatActivity(String userId, String chatId) {
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("chat_id", chatId);
        b.putString("user_id", userId);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void setChatIdToFirebase(DatabaseReference chatRef, String currentUserId, String personId) {
        String chat_id = FirebaseDatabase.getInstance().getReference().push().getKey();
        chatRef.child(currentUserId).child(personId).child(chat_id).setValue(true);
        chatRef.child(personId).child(currentUserId).child(chat_id).setValue(true).addOnSuccessListener(aVoid -> navigateToChatActivity(personId, chat_id));
    }


    private void getChatIdFromFirebase(DatabaseReference chatRef, String currentUserId, String personId) {
        chatRef.child(currentUserId).child(personId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chatId = "";
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        chatId = ds.getKey();

                    }
                    navigateToChatActivity(personId, chatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
