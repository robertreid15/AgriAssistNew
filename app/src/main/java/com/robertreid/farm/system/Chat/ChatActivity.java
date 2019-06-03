package com.robertreid.farm.system.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robertreid.farm.system.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;
    private String personId;
    private String chatId;
    private String currentUserID;

    private CircleImageView userPic;
    private TextView userName;

    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userPic = findViewById(R.id.image_userpic);
        userName = findViewById(R.id.text_username);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        personId = getIntent().getStringExtra("user_id");
        chatId = getIntent().getStringExtra("chat_id");

        getUserData(personId);

        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(false);
        chatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(chatLayoutManager);
        chatAdapter = new ChatAdapter(messages,  currentUserID);
        recyclerView.setAdapter(chatAdapter);

        loadMessages();

        EditText inputMessage = findViewById(R.id.input_message);
        Button sendBtn = findViewById(R.id.send);
        //Put message in chat

        sendBtn.setOnClickListener(view -> {
            if (!inputMessage.getText().toString().isEmpty()) {
                Message message = new Message();
                message.setFromid(currentUserID);
                message.setToId(personId);
                message.setTime(new Date());
                message.setMessage(inputMessage.getText().toString());
                DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("messages").child(chatId);
                String messageId = FirebaseDatabase.getInstance().getReference().push().getKey();
                chatRef.child(messageId).setValue(message).addOnSuccessListener(aVoid -> inputMessage.setText(""));
            }
        });

    }

    private void getUserData(String personId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("all_users").child(personId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String image = dataSnapshot.child("userpic").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    Glide.with(getApplicationContext()).load(image).into(userPic);
                    userName.setText(name);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {
        //Check if new message appears
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("messages").child(chatId);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messages.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Message message = ds.getValue(Message.class);
                        messages.add(message);
                    }

                    Collections.sort(messages, (o1, o2) -> o1.getTime().compareTo(o2.getTime()));
                    chatAdapter.notifyDataSetChanged();
                    if (messages.size() > 1) {
                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}