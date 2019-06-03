package com.robertreid.farm.system.barcodes.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.robertreid.farm.system.R;
import com.robertreid.farm.system.barcodes.model.Item;
import com.robertreid.farm.system.barcodes.ui.Edit_Item;
import com.robertreid.farm.system.profile.ProfileActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder> {

    private List<Item> itemList;

    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());


    public ItemsAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_barcode, parent, false);
        return new ItemsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemsViewHolder holder, int position) {
        holder.value.setText(itemList.get(position).getValue());
        holder.date.setText(dateFormat.format(new Date(itemList.get(position).getDate())));

        holder.itemView.setOnClickListener(view -> {
            //go to edit_item intent

            /*Intent profileIntent = new Intent(context, Edit_Item.class);
            profileIntent.putExtra("item_id", itemList.get(position).getValue());
            context.startActivity(profileIntent);*/
        });

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }




}
