package com.example.fotnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;

    private List<News> fullNewsList = new ArrayList<>();
    private List<News> filteredNewsList = new ArrayList<>();
    private NewsAdapter adapter;

    // Removed the 'isUserLeaving' variable as it's no longer needed for the problematic sign-out logic.
    // private boolean isUserLeaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Hamburger Menu Setup
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_developer_info) {
                startActivity(new Intent(NewsActivity.this, DeveloperInfoActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(NewsActivity.this, UserProfileActivity.class));
            } else if (id == R.id.nav_signout) {
                // This is the correct place to handle sign out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NewsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(filteredNewsList);
        recyclerView.setAdapter(adapter);

        // Firebase: Load News
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("News");
        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullNewsList.clear();
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    News news = newsSnapshot.getValue(News.class);
                    if (news != null) {
                        fullNewsList.add(news);
                    }
                }
                filteredNewsList.clear();
                filteredNewsList.addAll(fullNewsList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewsActivity.this, "Failed to load news.", Toast.LENGTH_SHORT).show();
            }
        });

        // Filter Buttons
        ImageButton btnSport = findViewById(R.id.btnSport);
        ImageButton btnEvent = findViewById(R.id.btnEvent);
        ImageButton btnAcademic = findViewById(R.id.btnAcademic);

        View.OnClickListener filterClickListener = v -> {
            String category = "";
            if (v.getId() == R.id.btnSport) category = "Sport";
            else if (v.getId() == R.id.btnEvent) category = "Event";
            else if (v.getId() == R.id.btnAcademic) category = "Academic";

            filteredNewsList.clear();
            for (News news : fullNewsList) {
                if (news.getCategory() != null &&
                        news.getCategory().equalsIgnoreCase(category)) {
                    filteredNewsList.add(news);
                }
            }
            adapter.notifyDataSetChanged();
        };

        btnSport.setOnClickListener(filterClickListener);
        btnEvent.setOnClickListener(filterClickListener);
        btnAcademic.setOnClickListener(filterClickListener);
    }

    // REMOVED: This method was causing unintended sign-outs when navigating away from NewsActivity.
    // @Override
    // protected void onUserLeaveHint() {
    //     super.onUserLeaveHint();
    //     isUserLeaving = true;
    // }

    // REMOVED: This method was causing unintended sign-outs when NewsActivity went into the background.
    // @Override
    // protected void onStop() {
    //     super.onStop();
    //     if (isUserLeaving) {
    //         FirebaseAuth.getInstance().signOut();
    //     }
    // }
}