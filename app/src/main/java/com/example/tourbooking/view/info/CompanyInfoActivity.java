package com.example.tourbooking.view.info;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tourbooking.R;

import java.util.Arrays;
import java.util.List;

/**
 * M30 – Company Introduction screen.
 */
public class CompanyInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        // Toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ViewPager for team photos
        ViewPager2 viewPager = findViewById(R.id.teamPhotosCarousel);
        List<Integer> images = Arrays.asList(
                R.drawable.ic_people,
                R.drawable.ic_calendar,
                R.drawable.ic_tour
        );
        viewPager.setAdapter(new TeamPhotoAdapter(images));

        // Back to home button simply finishes activity for now
        Button back = findViewById(R.id.backToHome);
        back.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------- Adapter -----------------
    private static class TeamPhotoAdapter extends RecyclerView.Adapter<TeamPhotoAdapter.Holder> {
        private final List<Integer> drawables;
        TeamPhotoAdapter(List<Integer> list) { this.drawables = list; }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_team_photo, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.imageView.setImageResource(drawables.get(position % drawables.size()));
        }

        @Override
        public int getItemCount() { return drawables.size(); }

        static class Holder extends RecyclerView.ViewHolder {
            android.widget.ImageView imageView;
            Holder(@NonNull android.view.View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image);
            }
        }
    }
} 