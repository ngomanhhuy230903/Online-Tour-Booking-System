package com.example.tourbooking.view.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tourbooking.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput, otpInputField, newPasswordInput, confirmPasswordInput;
    private Button sendOTPButton, resendOTPButton, submitButton;
    private TextView countdownTimer, errorMessageDisplay;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private CountDownTimer timer;
    private String userPhone;
    private String currentUserDocId; // để lưu ID document để cập nhật sau

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        otpInputField = findViewById(R.id.otpInputField);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        sendOTPButton = findViewById(R.id.sendOTPButton);
        resendOTPButton = findViewById(R.id.resendOTPButton);
        submitButton = findViewById(R.id.submitButton);
        countdownTimer = findViewById(R.id.countdownTimer);
        errorMessageDisplay = findViewById(R.id.errorMessageDisplay);

        resendOTPButton.setEnabled(false);

        sendOTPButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                showError("Please enter your username.");
                return;
            }

            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("userName", email)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            currentUserDocId = snapshot.getDocuments().get(0).getId();
                            userPhone = snapshot.getDocuments().get(0).getString("phoneNumber");

                            if (userPhone != null && !userPhone.isEmpty()) {
                                if (!userPhone.startsWith("+")) {
                                    if (userPhone.startsWith("0")) {
                                        userPhone = "+84" + userPhone.substring(1);
                                    } else {
                                        showError("Invalid phone number format.");
                                        return;
                                    }
                                }
                                sendOTP(userPhone);
                            } else {
                                showError("Phone number not found for this email.");
                            }
                        } else {
                            showError("User not found.");
                        }
                    })
                    .addOnFailureListener(e -> showError("Error: " + e.getMessage()));
        });

        resendOTPButton.setOnClickListener(v -> {
            if (userPhone != null && mResendToken != null) {
                resendOTP(userPhone);
            }
        });

        submitButton.setOnClickListener(v -> {
            String otp = otpInputField.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (TextUtils.isEmpty(otp) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                showError("Please fill in all fields.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("Passwords do not match.");
                return;
            }

            if (mVerificationId == null) {
                showError("OTP verification not started.");
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        if (currentUserDocId != null) {
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(currentUserDocId)
                                    .update("password", newPassword)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> showError("Failed to update password: " + e.getMessage()));
                        } else {
                            showError("User not found to update password.");
                        }
                    })
                    .addOnFailureListener(e -> showError("Invalid OTP or expired."));
        });
    }

    private void sendOTP(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        startTimer();
        Toast.makeText(this, "OTP sent to " + phone, Toast.LENGTH_SHORT).show();
    }

    private void resendOTP(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .setForceResendingToken(mResendToken)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        startTimer();
        Toast.makeText(this, "OTP resent to " + phone, Toast.LENGTH_SHORT).show();
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // Optional: Auto verification
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            showError("Verification failed: " + e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private void startTimer() {
        resendOTPButton.setEnabled(false);
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdownTimer.setText("00:" + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                countdownTimer.setText("00:00");
                resendOTPButton.setEnabled(true);
            }
        };
        timer.start();
    }

    private void showError(String message) {
        errorMessageDisplay.setText(message);
        errorMessageDisplay.setVisibility(TextView.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
