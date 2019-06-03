package com.robertreid.farm.system.barcodes.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.robertreid.farm.system.R;


public class ItemsViewHolder extends RecyclerView.ViewHolder{

    TextView value;
    TextView date;

    public ItemsViewHolder(View itemView) {
        super(itemView);
        value = itemView.findViewById(R.id.text_value);
        date = itemView.findViewById(R.id.text_date);
    }


}
