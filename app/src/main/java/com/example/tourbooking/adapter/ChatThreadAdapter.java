package com.example.tourbooking.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.tourbooking.R;
import com.example.tourbooking.model.ChatThread;
import java.util.List;

public class ChatThreadAdapter extends BaseAdapter {
    private final Context context;
    private final List<ChatThread> threads;

    public ChatThreadAdapter(Context context, List<ChatThread> threads) {
        this.context = context;
        this.threads = threads;
    }

    @Override
    public int getCount() { return threads.size(); }
    @Override
    public Object getItem(int position) { return threads.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatThread item = threads.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_thread, parent, false);
        }
        ImageView imgAvatar = convertView.findViewById(R.id.imgAvatar);
        TextView tvAgentName = convertView.findViewById(R.id.tvAgentName);
        TextView tvLastMessage = convertView.findViewById(R.id.tvLastMessage);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        imgAvatar.setImageResource(item.avatarRes);
        tvAgentName.setText(item.agentName);
        tvLastMessage.setText(item.lastMessage);
        tvTime.setText(item.time);

        return convertView;
    }
}