package com.robertreid.farm.system.findusers;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.robertreid.farm.system.R;

public class FollowViewHolders extends RecyclerView.ViewHolder {
    public TextView mEmail;
    public TextView name;
    public Button mFollow;

    public FollowViewHolders(View itemView){
        super(itemView);

        name = itemView.findViewById(R.id.name_text);
        mEmail = itemView.findViewById(R.id.text_email);
        mFollow = itemView.findViewById(R.id.btn_follow);

    }



}
