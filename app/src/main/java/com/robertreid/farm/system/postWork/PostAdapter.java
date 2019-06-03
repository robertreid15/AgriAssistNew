package com.robertreid.farm.system.postWork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.profile.ProfileActivity;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostViewHolders> {

    private List<PostObject> postList;
    //private List<String> following;     //don't think i'll need this

    private Context context;

    private String currentUserId;

    public PostAdapter(String id, List<PostObject> postList, Context context) {
        this.currentUserId = id;
        this.postList = postList;
        this.context = context;

    }

    @Override
    public PostViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post_item, parent, false);
        return new PostViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final PostViewHolders holder, int position) {
        holder.mEmail.setText(postList.get(position).getEmail());
        holder.name.setText(postList.get(position).getName());
        holder.description.setText(postList.get(position).getDescription());
        Glide.with(context).load(postList.get(position).getProfile_url()).into(holder.imageView);


        holder.imageView.setOnClickListener(view -> {


            if (!currentUserId.equals(postList.get(position).getUid())) {
                //go to profile activty and set user_id
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.putExtra("user_id", postList.get(position).getUid());
                context.startActivity(profileIntent);
            }
        });

        holder.itemView.setOnClickListener(view -> {
            //go user location
        });
    }

    @Override
    public int getItemCount() {
        return this.postList.size();
    }


}
