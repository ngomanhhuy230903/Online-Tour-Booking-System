package com.example.tourbooking.view.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ShapeableImageView ivEditAvatar;
    private ImageButton btnUploadAvatar;
    private TextInputEditText etFullName, etPhoneNumber, etDateOfBirth;
    private Button btnSaveChanges;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SessionManager sessionManager; // Dùng SessionManager

    private User userProfile;
    private Uri newAvatarUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userProfile = (User) getIntent().getSerializableExtra("USER_PROFILE");
        if (userProfile == null) {
            Toast.makeText(this, "Lỗi: Không có dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        sessionManager = new SessionManager(this); // Khởi tạo SessionManager

        initializeViews();
        registerImagePicker();
        populateInitialData();
        setupClickListeners();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivEditAvatar = findViewById(R.id.ivEditAvatar);
        btnUploadAvatar = findViewById(R.id.btnUploadAvatar);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        progressBar = findViewById(R.id.progressBarEdit);
    }

    private void registerImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        newAvatarUri = uri;
                        Glide.with(this).load(newAvatarUri).circleCrop().into(ivEditAvatar);
                    }
                });
    }

    private void populateInitialData() {
        etFullName.setText(userProfile.getFullName());
        etPhoneNumber.setText(userProfile.getPhoneNumber());
        etDateOfBirth.setText(userProfile.getDob());

        Glide.with(this)
                .load(userProfile.getAvatarUrl())
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(ivEditAvatar);
    }

    private void setupClickListeners() {
        btnUploadAvatar.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    etDateOfBirth.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveChanges() {
        String fullName = etFullName.getText().toString().trim();
        if (fullName.isEmpty()) {
            etFullName.setError("Họ và tên không được để trống");
            return;
        }
        setLoadingState(true);

        if (newAvatarUri != null) {
            uploadAvatarAndSaveChanges(fullName);
        } else {
            updateFirestoreProfile(userProfile.getAvatarUrl(), fullName);
        }
    }

    private void uploadAvatarAndSaveChanges(String fullName) {
        String userId = sessionManager.getUserId(); // Lấy ID từ Session
        if (userId == null) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn.", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }
        StorageReference fileRef = storage.getReference().child("avatars/" + userId);

        fileRef.putFile(newAvatarUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        updateFirestoreProfile(downloadUri.toString(), fullName);
                    } else {
                        setLoadingState(false);
                        Toast.makeText(EditProfileActivity.this, "Lỗi khi tải ảnh lên.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFirestoreProfile(String avatarUrl, String fullName) {
        String userId = sessionManager.getUserId(); // Lấy ID từ Session
        if (userId == null) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn.", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();

        Map<String, Object> updatedUser = new HashMap<>();
        updatedUser.put("fullName", fullName);
        updatedUser.put("phoneNumber", phoneNumber);
        updatedUser.put("dob", dob);
        updatedUser.put("avatarUrl", avatarUrl);
        updatedUser.put("userName", fullName); // Cập nhật cả userName để đồng bộ

        db.collection("users").document(userId)
                .update(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSaveChanges.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSaveChanges.setEnabled(true);
        }
    }
}