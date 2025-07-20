package com.example.tourbooking.view.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Booking;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private static final String BACKEND_URL = "https://tourbooking-api-huy.onrender.com/create-payment-intent";

    private MaterialToolbar toolbar;
    private TextView tvTourName, tvBookingDate, tvGuestCount;
    private TextView tvBasePrice, tvTax, tvServiceFee, tvPromoSavings;
    private TextInputEditText etCoupon, etBillingName;
    private Button btnApplyCoupon, btnPayNow;
    private View layoutPromoSavings;
    private CardInputWidget cardInputWidget;

    private Stripe stripe;
    private RequestQueue volleyQueue;
    private double currentTotalAmount = 0;
    private double basePriceFromIntent = 0;
    private String tourId, tourName, guestCount, selectedDate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        tourId = intent.getStringExtra("TOUR_ID");
        tourName = intent.getStringExtra("TOUR_NAME");
        guestCount = intent.getStringExtra("GUEST_COUNT");
        selectedDate = intent.getStringExtra("SELECTED_DATE");
        basePriceFromIntent = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        populateData();
        setupClickListeners();

        volleyQueue = Volley.newRequestQueue(this);
        stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar_payment);
        tvTourName = findViewById(R.id.tvTourName);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvGuestCount = findViewById(R.id.tvGuestCount);
        tvBasePrice = findViewById(R.id.tvBasePrice);
        tvTax = findViewById(R.id.tvTax);
        tvServiceFee = findViewById(R.id.tvServiceFee);
        btnPayNow = findViewById(R.id.btnPayNow);
        cardInputWidget = findViewById(R.id.stripe_card_widget);
        etCoupon = findViewById(R.id.etCoupon);
        btnApplyCoupon = findViewById(R.id.btnApplyCoupon);
        layoutPromoSavings = findViewById(R.id.layoutPromoSavings);
        tvPromoSavings = findViewById(R.id.tvPromoSavings);
        etBillingName = findViewById(R.id.etBillingName);

        cardInputWidget.setPostalCodeEnabled(false);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void populateData() {
        tvTourName.setText(tourName);
        tvBookingDate.setText("Ngày đi: " + selectedDate);
        tvGuestCount.setText(guestCount);

        double tax = basePriceFromIntent * 0.1;
        double serviceFee = 50000;
        updatePrice(basePriceFromIntent, tax, serviceFee, 0); // Ban đầu không có giảm giá
    }

    private void setupClickListeners() {
        btnPayNow.setOnClickListener(v -> startCheckout());
        btnApplyCoupon.setOnClickListener(v -> applyCoupon()); // Thêm lại listener
    }

    private void updatePrice(double base, double tax, double fee, double discount) {
        currentTotalAmount = base + tax + fee - discount;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvBasePrice.setText(currencyFormatter.format(base));
        tvTax.setText(currencyFormatter.format(tax));
        tvServiceFee.setText(currencyFormatter.format(fee));

        if (discount > 0) {
            layoutPromoSavings.setVisibility(View.VISIBLE);
            tvPromoSavings.setText(String.format("-%s", currencyFormatter.format(discount)));
        } else {
            layoutPromoSavings.setVisibility(View.GONE);
        }
        btnPayNow.setText(String.format("Thanh toán ngay (%s)", currencyFormatter.format(currentTotalAmount)));
    }

    // === HÀM APPLYCOUPON ĐÃ ĐƯỢC KHÔI PHỤC VÀ MỞ RỘNG ===
    private void applyCoupon() {
        String couponCode = etCoupon.getText().toString().trim().toUpperCase();
        double discount = 0;

        switch (couponCode) {
            case "GIAM50K":
                discount = 50000;
                break;
            case "GIAM100K":
                discount = 100000;
                break;
            case "GIAM200K":
                discount = 200000;
                break;
            default:
                Toast.makeText(this, "Mã giảm giá không hợp lệ!", Toast.LENGTH_SHORT).show();
                return; // Thoát khỏi hàm nếu mã sai
        }

        Toast.makeText(this, "Áp dụng mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
        double tax = basePriceFromIntent * 0.1;
        double serviceFee = 50000;
        updatePrice(basePriceFromIntent, tax, serviceFee, discount);
    }

    // ... (Các hàm còn lại không thay đổi)

    private void startCheckout() {
        btnPayNow.setEnabled(false);
        btnPayNow.setText("Đang xử lý...");
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params == null) {
            onPaymentFailed("Thông tin thẻ không hợp lệ.");
            return;
        }
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("amount", (long) currentTotalAmount);
        } catch (JSONException e) {
            onPaymentFailed("Lỗi tạo yêu cầu thanh toán.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL, requestBody,
                response -> {
                    try {
                        String clientSecret = response.getString("clientSecret");
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, clientSecret);
                        stripe.confirmPayment(this, confirmParams);
                    } catch (JSONException e) {
                        onPaymentFailed("Lỗi đọc phản hồi từ server.");
                    }
                },
                error -> onPaymentFailed("Không thể kết nối đến máy chủ thanh toán: " + error.getMessage())
        );
        volleyQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (stripe != null) {
            stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
                @Override
                public void onSuccess(@NonNull PaymentIntentResult result) {
                    PaymentIntent paymentIntent = result.getIntent();
                    if (paymentIntent != null && paymentIntent.getStatus() == PaymentIntent.Status.Succeeded) {
                        Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        saveBookingToDatabase(paymentIntent);
                    } else {
                        onPaymentFailed("Trạng thái thanh toán không thành công.");
                    }
                }
                @Override
                public void onError(@NonNull Exception e) {
                    onPaymentFailed("Lỗi thanh toán: " + e.getMessage());
                }
            });
        }
    }

    private void saveBookingToDatabase(PaymentIntent paymentIntent) {
        String currentUserId;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            currentUserId = "DUMMY_USER_ID_123";
        }

        Booking newBooking = new Booking();
        newBooking.id = paymentIntent.getId();
        newBooking.userId = currentUserId;
        newBooking.tourId = this.tourId;
        newBooking.tourName = this.tourName;
        newBooking.totalPrice = this.currentTotalAmount;
        newBooking.status = "UPCOMING";
        newBooking.bookingDate = new Date();

        db.collection("bookings").document(newBooking.id).set(newBooking)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Lưu đơn hàng thành công vào Firestore với ID: " + newBooking.id);
                    navigateToConfirmation(newBooking);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi khi lưu đơn hàng vào Firestore", e);
                    navigateToConfirmation(newBooking);
                });
    }

    private void navigateToConfirmation(Booking booking) {
        Intent intent = new Intent(PaymentActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra(PaymentConfirmationActivity.EXTRA_BOOKING_ID, booking.getId());
        intent.putExtra(PaymentConfirmationActivity.EXTRA_TOUR_NAME, booking.getTourName());
        intent.putExtra(PaymentConfirmationActivity.EXTRA_BOOKING_DATE, this.selectedDate);
        startActivity(intent);
        finishAffinity();
    }

    private void onPaymentFailed(@Nullable String message) {
        if (message != null) {
            Log.e(TAG, "Payment failed: " + message);
        }
        Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
        btnPayNow.setEnabled(true);
        populateData();
    }
}