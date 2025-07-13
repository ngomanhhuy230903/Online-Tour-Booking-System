package com.example.tourbooking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.tourbooking.model.ChatThread;
import com.example.tourbooking.model.ChatMessage;
import com.example.tourbooking.adapter.ChatThreadAdapter;
import com.example.tourbooking.adapter.ChatMessageAdapter;

public class ChatSupportActivity extends AppCompatActivity {

    private ListView chatThreadsList, messageListView;
    private SearchView searchThreadInput;
    private Button newChatButton, sendButton, attachmentButton;
    private EditText messageInputField;
    private TextView typingIndicator;

    private List<ChatThread> chatThreads = new ArrayList<>();
    private List<ChatThread> allChatThreads = new ArrayList<>();
    private List<ChatMessage> messages = new ArrayList<>();
    private ChatThreadAdapter threadsAdapter;
    private ChatMessageAdapter messagesAdapter;

    // Các câu trả lời mẫu cho bot
    private final String[] botReplies = {
            "Xin chào! Tôi có thể giúp gì cho bạn?",
            "Bạn cần hỗ trợ về vấn đề gì?",
            "Vui lòng cung cấp thêm thông tin.",
            "Tôi sẽ kiểm tra thông tin cho bạn ngay.",
            "Cảm ơn bạn đã liên hệ, tôi sẽ hỗ trợ bạn sớm nhất.",
            "Bạn có thể mô tả chi tiết hơn không?",
            "Tôi đã nhận được yêu cầu của bạn.",
            "Bạn cần hỗ trợ về đặt tour, thanh toán hay vấn đề khác?",
            "Chúng tôi luôn sẵn sàng hỗ trợ bạn.",
            "Bạn vui lòng chờ trong giây lát nhé."
    };

    private String getRandomBotReply() {
        return botReplies[new Random().nextInt(botReplies.length)];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_support), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatThreadsList = findViewById(R.id.chat_threads_list);
        messageListView = findViewById(R.id.message_list_view);
        searchThreadInput = findViewById(R.id.search_thread_input);
        newChatButton = findViewById(R.id.new_chat_button);
        sendButton = findViewById(R.id.send_button);
        attachmentButton = findViewById(R.id.attachment_button);
        messageInputField = findViewById(R.id.message_input_field);
        typingIndicator = findViewById(R.id.typing_indicator);

        threadsAdapter = new ChatThreadAdapter(this, chatThreads);
        chatThreadsList.setAdapter(threadsAdapter);
        messagesAdapter = new ChatMessageAdapter(this, messages);
        messageListView.setAdapter(messagesAdapter);

        // Khởi tạo dữ liệu gốc
        initAllChatThreads();
        loadChatThreads(null);

        searchThreadInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadChatThreads(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                loadChatThreads(newText);
                return true;
            }
        });

        chatThreadsList.setOnItemClickListener((parent, view, position, id) -> {
            loadMessages(position);
            typingIndicator.setVisibility(View.VISIBLE);
            messageInputField.setText("");
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        ChatThread thread = chatThreads.get(position);
                        messages.add(new ChatMessage(thread.lastMessage, false));
                        messagesAdapter.notifyDataSetChanged();
                        messageListView.smoothScrollToPosition(messages.size() - 1);
                    });
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                messages.remove(messages.size() - 1);
                                messages.add(new ChatMessage(getRandomBotReply(), false));
                                messagesAdapter.notifyDataSetChanged();
                                messageListView.smoothScrollToPosition(messages.size() - 1);
                                typingIndicator.setVisibility(View.GONE);
                            });
                        }
                    }, 2000);
                }
            }, 1000);
        });

        newChatButton.setOnClickListener(v -> {
            ChatThread newThread = new ChatThread("Agent", "New Chat", "Just now", R.drawable.ic_agent_avatar);
            allChatThreads.add(0, newThread);
            loadChatThreads(searchThreadInput.getQuery().toString());
        });

        sendButton.setOnClickListener(v -> {
            String message = messageInputField.getText().toString().trim();
            if (!message.isEmpty()) {
                messages.add(new ChatMessage(message, true));
                messagesAdapter.notifyDataSetChanged();
                messageListView.smoothScrollToPosition(messages.size() - 1);
                messageInputField.setText("");
                typingIndicator.setVisibility(View.VISIBLE);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            messages.add(new ChatMessage("Agent is typing...", false));
                            messagesAdapter.notifyDataSetChanged();
                            messageListView.smoothScrollToPosition(messages.size() - 1);
                        });
                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    messages.remove(messages.size() - 1);
                                    messages.add(new ChatMessage(getRandomBotReply(), false));
                                    messagesAdapter.notifyDataSetChanged();
                                    messageListView.smoothScrollToPosition(messages.size() - 1);
                                    typingIndicator.setVisibility(View.GONE);
                                });
                            }
                        }, 2000);
                    }
                }, 1000);
            }
        });

        attachmentButton.setOnClickListener(v -> {
            // Implement attachment picker
        });
    }

    private void initAllChatThreads() {
        allChatThreads.clear();
        allChatThreads.add(new ChatThread("Agent", "Hello! How can I assist you?", "9:41", R.drawable.ic_agent_avatar));
        allChatThreads.add(new ChatThread("Agent", "Text message chat coming", "Yesterday", R.drawable.ic_agent_avatar));
        allChatThreads.add(new ChatThread("Agent", "Last chat on the number", "Yesterday", R.drawable.ic_agent_avatar));
        allChatThreads.add(new ChatThread("Agent", "I need help with my account", "9:41", R.drawable.ic_agent_avatar));
    }

    private void loadChatThreads(String query) {
        chatThreads.clear();
        if (query == null || query.isEmpty()) {
            chatThreads.addAll(allChatThreads);
        } else {
            String lowerQuery = query.toLowerCase();
            for (ChatThread thread : allChatThreads) {
                if (thread.agentName.toLowerCase().contains(lowerQuery)
                        || thread.lastMessage.toLowerCase().contains(lowerQuery)) {
                    chatThreads.add(thread);
                }
            }
        }
        threadsAdapter.notifyDataSetChanged();
    }

    private void loadMessages(int position) {
        messages.clear();
        ChatThread thread = chatThreads.get(position);
        messages.add(new ChatMessage(thread.lastMessage, false));
        messagesAdapter.notifyDataSetChanged();
        messageListView.smoothScrollToPosition(messages.size() - 1);
    }
}