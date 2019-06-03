package com.robertreid.farm.system.barcodes.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.barcodes.model.EditItemObject;
import com.robertreid.farm.system.barcodes.model.Item;

public class Edit_Item extends AppCompatActivity {
    EditText title, manufacturer, price, category;
    Button mAdd, mFindUsers, mFindItems, completeOrder, mFindReviews;

    private DatabaseReference mDatabase;
    private String currrentUserID;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        currrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAdd = (Button) findViewById(R.id.add);
            //mFindUsers = (Button) findViewById(R.id.findUsers);
            //mFindItems = (Button) findViewById(R.id.findItems);
            //completeOrder = (Button) findViewById(R.id.completeOrderAdmin);
            //mFindReviews = (Button) findViewById(R.id.viewReviews);


            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText editTitle = (EditText) findViewById(R.id.title);
                    String title = editTitle.getText().toString();

                    EditText editmanufacturer = (EditText) findViewById(R.id.manufacturer);
                    String manufacturer = editmanufacturer.getText().toString();

                    EditText editprice = (EditText) findViewById(R.id.price);
                    String price = editprice.getText().toString();

                    EditText editcategory = (EditText) findViewById(R.id.category);
                    String category = editcategory.getText().toString();


                    EditItemObject item = new EditItemObject(title, manufacturer, price, category);
                    //Item item = new ItemBuilder().setTitle(title).setManufacturer(manufacturer).setPrice(price).setCategory(category).createItem();
                    mDatabase.child("barcodes").child(currrentUserID).push().setValue(item);
                }
            });
    }
}
