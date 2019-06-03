package com.robertreid.farm.system.postWork;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.robertreid.farm.system.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolders extends RecyclerView.ViewHolder {

    public TextView mEmail;
    public TextView name;
    public CircleImageView imageView;
    public TextView description;

    public PostViewHolders(View itemView){
        super(itemView);

        name = itemView.findViewById(R.id.text_name);
        mEmail = itemView.findViewById(R.id.text_email);
        imageView = itemView.findViewById(R.id.image_userpic);
        description = itemView.findViewById(R.id.editText);

    }



}
