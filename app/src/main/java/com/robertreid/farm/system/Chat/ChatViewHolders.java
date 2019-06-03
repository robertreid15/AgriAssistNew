package com.robertreid.farm.system.Chat;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robertreid.farm.system.R;


public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView messageText;
    TextView timeText;
    RelativeLayout layout;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        messageText =  itemView.findViewById(R.id.message_text_layout);
        layout = itemView.findViewById(R.id.mes_rel);
        timeText =  itemView.findViewById(R.id.time_text_layout);
    }

    @Override
    public void onClick(View view) {
    }
}
