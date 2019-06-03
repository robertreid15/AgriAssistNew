package com.robertreid.farm.system.postWork;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robertreid.farm.system.R;

import java.util.ArrayList;

public class FindPostActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> followingIds = new ArrayList<>();
    private ArrayList<PostObject> results = new ArrayList<>();
    EditText mInput;
    private String currentUserID;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_posts);

        mInput = findViewById(R.id.input);
        Button mSearch = findViewById(R.id.search);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference();

        getAllPosts();
        initViews();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PostAdapter(currentUserID, results, FindPostActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mSearch.setOnClickListener(view -> {
            search(mInput.getText().toString());
        });

    }

    private void search(String query) {

        ArrayList<PostObject> searchResults = new ArrayList<>();
        for (PostObject person : results) {
            if (person.getName().toLowerCase().contains(query)) {
                searchResults.add(person);
            }
        }

        mAdapter = new PostAdapter(currentUserID, searchResults, FindPostActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /*retrieve all posts*/

    private void getAllPosts() {

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("all_posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                results.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String email = ds.child("email").getValue().toString();
                        String name = ds.child("name").getValue().toString();
                        String profile_url = ds.child("profile_url").getValue().toString();
                        String description = ds.child("description").getValue().toString();
                        String uid = ds.child("uid").getValue().toString();

                        PostObject obj = new PostObject(uid, name, email, profile_url, description);
                        results.add(obj);


                    }
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void clear() {

    }

    private void initViews() {

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openDialog()
        );
    }

    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(FindPostActivity.this);
        View subView = inflater.inflate(R.layout.dialogue_layout, null);

        EditText inputDescription = subView.findViewById(R.id.input_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(subView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String description = inputDescription.getText().toString();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            PostObject post = new PostObject(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl().toString(), description);
            database.child("all_posts").child(FirebaseDatabase.getInstance().getReference().push().getKey()).setValue(post);
        });

        builder.show();
    }


}
