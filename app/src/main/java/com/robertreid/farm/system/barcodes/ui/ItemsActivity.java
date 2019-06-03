package com.robertreid.farm.system.barcodes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.barcodes.adapter.ItemsAdapter;
import com.robertreid.farm.system.barcodes.model.Item;

import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {


    private RecyclerView itemsList;
    private ArrayList<Item> items = new ArrayList<>();
    private ItemsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adapter = new ItemsAdapter(items, this);
        initViews();

        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("barcodes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    items.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Item item = ds.getValue(Item.class);
                        items.add(item);

                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initViews() {
        itemsList = findViewById(R.id.rv_items);
        itemsList.setHasFixedSize(true);
        itemsList.setLayoutManager(new LinearLayoutManager(this));
        itemsList.setAdapter(adapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
                    startActivity(new Intent(this, BarcodeScanActivity.class));

                }
        );
    }

}
