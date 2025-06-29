package com.example.tourbooking.view.review;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.SelectedPhotoAdapter;
import com.example.tourbooking.model.Review;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewFormActivity extends AppCompatActivity {
    // ... (Khai báo biến)
    private RatingBar ratingBar;
    private TextInputEditText etReviewComment;
    private Button btnUploadPhotos, btnSubmitReview;
    private RecyclerView rvSelectedPhotos;
    private MaterialSwitch switchAnonymous;
    private ProgressBar progressBar;
    private SelectedPhotoAdapter photoAdapter;
    private ArrayList<Uri> selectedPhotoUris = new ArrayList<>();
    private String tourId; // Nhận từ Intent

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);

        // tourId = getIntent().getStringExtra("TOUR_ID_EXTRA");

        initializeViews();
        registerImagePicker();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_review_form);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        ratingBar = findViewById(R.id.ratingBar);
        etReviewComment = findViewById(R.id.etReviewComment);
        btnUploadPhotos = findViewById(R.id.btnUploadPhotos);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        rvSelectedPhotos = findViewById(R.id.rvSelectedPhotos);
        switchAnonymous = findViewById(R.id.switchAnonymous);
        progressBar = findViewById(R.id.progressBarReview);
    }

    private void registerImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedPhotoUris.addAll(uris);
                        photoAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupRecyclerView() {
        photoAdapter = new SelectedPhotoAdapter(selectedPhotoUris, this, position -> {
            selectedPhotoUris.remove(position);
            photoAdapter.notifyItemRemoved(position);
        });
        rvSelectedPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelectedPhotos.setAdapter(photoAdapter);
    }

    private void setupClickListeners() {
        btnUploadPhotos.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        if (ratingBar.getRating() == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        if (selectedPhotoUris.isEmpty()) {
            saveReviewToFirestore(new ArrayList<>());
        } else {
            uploadImagesAndSaveReview();
        }
    }

    private void uploadImagesAndSaveReview() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        List<StorageReference> photoRefs = new ArrayList<>();
        List<Task<Uri>> uploadTasks = new ArrayList<>();

        for (Uri uri : selectedPhotoUris) {
            StorageReference fileRef = storage.getReference().child("review_images/" + UUID.randomUUID().toString());
            photoRefs.add(fileRef);
            uploadTasks.add(fileRef.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }));
        }

        Tasks.whenAllSuccess(uploadTasks).addOnSuccessListener(objects -> {
            List<String> downloadUrls = new ArrayList<>();
            for(Object obj : objects) {
                downloadUrls.add(obj.toString());
            }
            saveReviewToFirestore(downloadUrls);
        }).addOnFailureListener(e -> {
            setLoadingState(false);
            Toast.makeText(this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveReviewToFirestore(List<String> photoUrls) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }

        Review review = new Review();
        review.userId = user.getUid();
        review.userName = switchAnonymous.isChecked() ? "Người dùng ẩn danh" : user.getDisplayName();
        review.tourId = "some_tour_id_123"; // Thay bằng tourId thật
        review.rating = ratingBar.getRating();
        review.comment = etReviewComment.getText().toString();
        review.photoUrls = photoUrls;

        FirebaseFirestore.getInstance().collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Lỗi khi gửi đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        if(isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSubmitReview.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSubmitReview.setEnabled(true);
        }
    }
}