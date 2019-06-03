package com.robertreid.farm.system.findusers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.profile.ProfileActivity;

import java.util.List;


public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> usersList;
    private List<String> following;

    private Context context;

    public FollowAdapter(List<FollowObject> usersList, List<String> following, Context context) {
        this.usersList = usersList;
        this.context = context;
        this.following = following;

    }

    @Override
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_followers_item, parent, false);
        return new FollowViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final FollowViewHolders holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());
        holder.name.setText(usersList.get(position).getName());

        if (following.contains(usersList.get(position).getUid())) {
            holder.mFollow.setText("following");
        } else {
            holder.mFollow.setText("follow");
        }

        holder.mFollow.setOnClickListener(view -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (holder.mFollow.getText().toString().equals("follow") || !following.contains(usersList.get(holder.getLayoutPosition()).getUid())) {
                holder.mFollow.setText("following");
                FirebaseDatabase.getInstance().getReference().child("all_users").child(userId).child("contacts").child(usersList.get(holder.getLayoutPosition()).getUid()).setValue(true);
            } else {
                holder.mFollow.setText("follow");
                FirebaseDatabase.getInstance().getReference().child("all_users").child(userId).child("contacts").child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();
            }
        });

        holder.itemView.setOnClickListener(view -> {
            //go to profile activty and set user_id
            Intent profileIntent = new Intent(context, ProfileActivity.class);
            profileIntent.putExtra("user_id", usersList.get(position).getUid());
            context.startActivity(profileIntent);
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }


}
