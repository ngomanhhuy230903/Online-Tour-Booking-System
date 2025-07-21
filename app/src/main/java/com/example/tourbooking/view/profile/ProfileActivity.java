package com.example.tourbooking.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.model.User;
import com.example.tourbooking.view.info.LogoutConfirmationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvFullName, tvUserName, tvPhoneNumber, tvDateOfBirth, tvGender;
    private Button btnEditProfile, btnLogout;
    private ProgressBar progressBar;
    private ScrollView profileContent;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupClickListeners();
        loadUserProfile();
    }

    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvUserName = findViewById(R.id.tvUserName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvGender = findViewById(R.id.tvGender);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBarProfile);
        profileContent = findViewById(R.id.profileContent);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            // startActivity(new Intent(this, EditProfileActivity.class));
            Toast.makeText(this, "Mở màn hình Chỉnh sửa hồ sơ (M23)", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
             startActivity(new Intent(this, LogoutConfirmationActivity.class));
//            Toast.makeText(this, "Mở màn hình Xác nhận Đăng xuất (M34)", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        setLoadingState(true);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userIdToQuery;

        if (currentUser != null) {
            userIdToQuery = currentUser.getUid();
        } else {
            // Dùng User ID của document "users" mà bạn đã tạo để test
            userIdToQuery = "hehe";
        }

        db.collection("users").document(userIdToQuery)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    setLoadingState(false);
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            populateUi(user);
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy hồ sơ người dùng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Lỗi khi tải hồ sơ.", Toast.LENGTH_SHORT).show();
                });
    }

    private void populateUi(User user) {
        tvFullName.setText(user.getFullName());
        tvUserName.setText("@" + user.getUserName());
        tvPhoneNumber.setText(user.getPhoneNumber());
        tvDateOfBirth.setText(user.getDob());
        tvGender.setText(user.getGender());

        Glide.with(this)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_profile) // Ảnh mặc định
                .circleCrop() // Bo tròn ảnh
                .into(ivAvatar);
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            profileContent.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            profileContent.setVisibility(View.VISIBLE);
        }
    }
}