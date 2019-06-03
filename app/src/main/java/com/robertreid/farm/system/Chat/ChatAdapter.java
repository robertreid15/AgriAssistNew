package com.robertreid.farm.system.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robertreid.farm.system.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<Message> chatList;
    private String currentUserId;
    private SimpleDateFormat dateFormat;


    public ChatAdapter(List<Message> matchesList, String currentUserId) {
        this.chatList = matchesList;
        this.currentUserId = currentUserId;
        dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single, null, false);
        return new ChatViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolders holder, int position) {
        holder.messageText.setText(chatList.get(position).getMessage());

        if (chatList.get(position).getFromid().equals(currentUserId)) {
            holder.layout.setGravity(Gravity.END);
            holder.messageText.setBackgroundResource(R.drawable.message_text_background_me);
        } else {
            holder.layout.setGravity(Gravity.START);
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
        }
        holder.timeText.setText(dateFormat.format(chatList.get(position).getTime()));
        // holder.timeText.setText(chatList.get(position).getTime().toString());

    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
