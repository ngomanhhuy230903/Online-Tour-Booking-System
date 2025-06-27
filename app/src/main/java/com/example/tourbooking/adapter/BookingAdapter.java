// File: adapter/BookingAdapter.java (Cập nhật để xử lý click)
package com.example.tourbooking.adapter;

import android.content.Context;
import android.content.Intent; // Import mới
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Booking;
import com.example.tourbooking.view.booking.BookingDetailActivity; // Import mới

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;

    public BookingAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingCode, tvBookingStatus, tvTourName, tvBookingDate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingCode = itemView.findViewById(R.id.tvBookingCode);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);

            // === THÊM SỰ KIỆN CLICK VÀO ĐÂY ===
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Booking clickedBooking = bookingList.get(position);
                    Intent intent = new Intent(context, BookingDetailActivity.class);
                    // Truyền ID của đơn hàng được click sang màn hình chi tiết
                    intent.putExtra(BookingDetailActivity.BOOKING_ID_EXTRA, clickedBooking.getId());
                    context.startActivity(intent);
                }
            });
        }

        void bind(Booking booking) {
            String shortId = booking.getId().length() > 7 ? booking.getId().substring(booking.getId().length() - 7) : booking.getId();
            tvBookingCode.setText("Mã đơn: #" + shortId.toUpperCase());
            tvTourName.setText(booking.getTourName());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvBookingDate.setText("Ngày đặt: " + sdf.format(booking.getBookingDate()));

            tvBookingStatus.setText(booking.getStatus());
            // Đổi màu nền dựa vào trạng thái
            switch (booking.getStatus().toUpperCase()) {
                case "COMPLETED":
                    tvBookingStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_status_completed));
                    break;
                case "UPCOMING":
                    tvBookingStatus.setBackgroundColor(Color.BLUE); // Thay bằng drawable nếu muốn
                    break;
                case "CANCELED":
                    tvBookingStatus.setBackgroundColor(Color.RED); // Thay bằng drawable nếu muốn
                    break;
            }
        }
    }
}