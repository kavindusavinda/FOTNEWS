package com.example.fotnews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import com.example.fotnews.SignOutDialog;


public class SignOutDialog extends Dialog {

    private Button btnSignOut, btnCancel;
    private OnSignOutListener listener;

    public interface OnSignOutListener {
        void onSignOutConfirmed();
    }

    public SignOutDialog(Context context, OnSignOutListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_out_dialog); // We'll define this next

        btnSignOut = findViewById(R.id.btnSignOut);
        btnCancel = findViewById(R.id.btnCancel);

        btnSignOut.setOnClickListener(v -> {
            listener.onSignOutConfirmed();
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
}
