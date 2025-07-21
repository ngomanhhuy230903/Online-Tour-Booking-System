package com.example.tourbooking.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.model.User;
import com.example.tourbooking.utils.SessionManager; // Import mới
import com.example.tourbooking.view.info.LogoutConfirmationActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvFullName, tvUserName, tvPhoneNumber, tvDateOfBirth, tvGender;
    private Button btnEditProfile, btnLogout;
    private ProgressBar progressBar;
    private ScrollView profileContent;
    private FirebaseFirestore db;
    private User currentUserProfile;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private SessionManager sessionManager; // Import mới

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
        sessionManager = new SessionManager(this); // Khởi tạo SessionManager

        initializeViews();
        registerEditProfileLauncher();
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

    private void registerEditProfileLauncher() {
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Kiểm tra xem EditProfileActivity có trả về kết quả RESULT_OK không
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Nếu có, nghĩa là người dùng đã lưu thay đổi -> tải lại hồ sơ
                        Toast.makeText(this, "Đang cập nhật hồ sơ...", Toast.LENGTH_SHORT).show();
                        loadUserProfile();
                    }
                }
        );
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            if (currentUserProfile != null) {
                Intent intent = new Intent(this, EditProfileActivity.class);
                // Gửi toàn bộ đối tượng User sang màn hình chỉnh sửa
                intent.putExtra("USER_PROFILE", currentUserProfile);
                editProfileLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Không thể tải dữ liệu để chỉnh sửa.", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LogoutConfirmationActivity.class));
        });
    }


    private void loadUserProfile() {
        setLoadingState(true);
        // SỬA LẠI LOGIC LẤY USER ID
        String userIdToQuery = sessionManager.getUserId();

        if (userIdToQuery == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem hồ sơ.", Toast.LENGTH_SHORT).show();
            // Có thể chuyển về màn hình Login ở đây
            finish();
            return;
        }

        db.collection("users").document(userIdToQuery)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    setLoadingState(false);
                    if (documentSnapshot.exists()) {
                        currentUserProfile = documentSnapshot.toObject(User.class);
                        if (currentUserProfile != null) {
                            populateUi(currentUserProfile);
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
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
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