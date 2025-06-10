package com.example.fotnews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private TextView txtUserName, txtUserEmail;
    private Button btnEdit, btnSignOut;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnEdit = findViewById(R.id.btnEdit);
        btnSignOut = findViewById(R.id.btnSignOut);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            txtUserName.setText("User Name : " + user.getDisplayName());
            txtUserEmail.setText("Email : " + user.getEmail());
        }

        btnEdit.setOnClickListener(v -> {
            // Navigate to edit screen (e.g., EditUserActivity)
            startActivity(new Intent(UserProfileActivity.this, EditUserDialog.class));
        });

        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}
