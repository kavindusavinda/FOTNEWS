package com.example.fotnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity implements EditUserDialog.EditUserListener, SignOutDialog.OnSignOutListener {

    private TextView txtUserName, txtUserEmail;
    private Button btnEdit, btnSignOut;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Variables to hold current user data, retrieved from Firebase
    private String currentUsername;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnEdit = findViewById(R.id.btnEdit);
        btnSignOut = findViewById(R.id.btnSignOut);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        loadUserProfile();

        btnSignOut.setOnClickListener(v -> {
            SignOutDialog signOutDialog = new SignOutDialog(UserProfileActivity.this, this);
            signOutDialog.show();
        });

        btnEdit.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String currentUserName = txtUserName.getText().toString().replace("User Name : ", "");
                String currentUserEmail = txtUserEmail.getText().toString().replace("Email : ", "");

                EditUserDialog dialog = new EditUserDialog(UserProfileActivity.this,
                        currentUserName,
                        currentUserEmail,
                        UserProfileActivity.this); // this implements EditUserListener
                dialog.show();
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String retrievedUsername = null;
                    String retrievedEmail = null;

                    if (snapshot.exists()) {
                        // Try to get from Realtime Database first
                        retrievedUsername = snapshot.child("username").getValue(String.class);
                        retrievedEmail = snapshot.child("email").getValue(String.class);
                    }

                    // Fallback to Firebase Auth user object if not found in Realtime DB or if DB values are null/empty
                    if (retrievedUsername == null || retrievedUsername.trim().isEmpty()) {
                        retrievedUsername = user.getDisplayName(); // Can be null
                    }
                    if (retrievedEmail == null || retrievedEmail.trim().isEmpty()) {
                        retrievedEmail = user.getEmail(); // Should not be null if logged in
                    }

                    // Update the TextViews, providing default messages if data is still null/empty
                    // This ensures the TextViews are always updated, overriding the XML placeholders.
                    currentUsername = (retrievedUsername != null && !retrievedUsername.trim().isEmpty()) ? retrievedUsername : "N/A";
                    currentUserEmail = (retrievedEmail != null && !retrievedEmail.trim().isEmpty()) ? retrievedEmail : "N/A";

                    txtUserName.setText("User Name : " + currentUsername);
                    txtUserEmail.setText("Email : " + currentUserEmail);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("UserProfileActivity", "loadUserProfile:onCancelled", error.toException());
                    Toast.makeText(UserProfileActivity.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();

                    // Even on cancellation, try to display at least the email if user is logged in
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        currentUserEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "N/A";
                        txtUserEmail.setText("Email : " + currentUserEmail);
                        txtUserName.setText("User Name : N/A"); // Default name if DB load failed
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please log in to view your profile.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onUserInfoUpdated(String newName, String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update email
                    user.updateEmail(newEmail).addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            // Update Realtime Database
                            mDatabase.child(user.getUid()).child("username").setValue(newName);
                            mDatabase.child(user.getUid()).child("email").setValue(newEmail);

                            Toast.makeText(UserProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                            loadUserProfile(); // Reload profile to display updated info
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Failed to update email: " + emailTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to update username: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onSignOutConfirmed() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(UserProfileActivity.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}