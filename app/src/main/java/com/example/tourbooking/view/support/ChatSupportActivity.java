package com.example.tourbooking.view.support;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.ChatMessageAdapter;
import com.example.tourbooking.model.ChatMessage;
import com.example.tourbooking.utils.SessionManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatSupportActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private EditText etChatMessage;
    private ImageButton btnSendMessage;
    private ChatMessageAdapter adapter;
    private List<ChatMessage> messageList;

    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private String currentUserId;
    private String chatThreadId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        currentUserId = sessionManager.getUserId();
        if (currentUserId == null) {
            currentUserId = "hehe"; // Dùng user test nếu chưa đăng nhập
        }
        chatThreadId = currentUserId; // ID của cuộc hội thoại chính là ID của người dùng

        initializeViews();
        setupRecyclerView();
        setupSendButton();
        listenForMessages();
    }

    private void initializeViews() {
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new ChatMessageAdapter(messageList, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(adapter);
    }

    private void listenForMessages() {
        db.collection("chat_threads").document(chatThreadId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi khi tải tin nhắn.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        messageList.clear();
                        messageList.addAll(snapshots.toObjects(ChatMessage.class));
                        adapter.notifyDataSetChanged();
                        rvChatMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void setupSendButton() {
        btnSendMessage.setOnClickListener(v -> {
            String messageText = etChatMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
            }
        });
    }

    private void sendMessage(String messageText) {
        etChatMessage.setText(""); // Xóa tin nhắn trong ô nhập liệu ngay lập tức

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", currentUserId);
        message.put("text", messageText);
        message.put("timestamp", FieldValue.serverTimestamp());

        db.collection("chat_threads").document(chatThreadId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    // Thành công, listener sẽ tự động cập nhật UI
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gửi tin nhắn thất bại.", Toast.LENGTH_SHORT).show();
                    etChatMessage.setText(messageText); // Trả lại tin nhắn nếu gửi lỗi
                });
    }
}