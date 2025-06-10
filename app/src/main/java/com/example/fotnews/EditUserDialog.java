package com.example.fotnews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditUserDialog extends Dialog {

    private EditText editUsername, editEmail;
    private Button btnOk, btnCancel;

    private String currentName, currentEmail;
    private EditUserListener listener;

    public interface EditUserListener {
        void onUserInfoUpdated(String newName, String newEmail);
    }

    public EditUserDialog(Context context, String name, String email, EditUserListener listener) {
        super(context);
        this.currentName = name;
        this.currentEmail = email;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_user_dialog);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);

        editUsername.setText(currentName);
        editEmail.setText(currentEmail);

        btnOk.setOnClickListener(v -> {
            String newName = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                listener.onUserInfoUpdated(newName, newEmail);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
}
