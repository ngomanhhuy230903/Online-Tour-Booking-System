package com.example.tourbooking.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.tourbooking.R;
import com.example.tourbooking.model.ChatMessage;
import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {
    private final Context context;
    private final List<ChatMessage> messages;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() { return messages.size(); }
    @Override
    public Object getItem(int position) { return messages.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage item = messages.get(position);
        if (convertView == null) {
            int layout = item.isUser ? R.layout.item_message_right : R.layout.item_message_left;
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);
        }
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        tvMessage.setText(item.text);
        return convertView;
    }
}