package com.example.flirtandroid.SnackBar;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flirtandroid.R;
import com.google.android.material.snackbar.Snackbar;

public class WarningCustomSnackbar {
    public static void showSnackbar(Activity activity, String message) {
        View parentView = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG);

        // Inflate custom layout
        View customSnackbarView = activity.getLayoutInflater().inflate(R.layout.warning_custom_snackbar, null);
        ImageView iconImageView = customSnackbarView.findViewById(R.id.snackbar_icon1);
        TextView textView = customSnackbarView.findViewById(R.id.snackbar_text1);
        ImageView dismissImageView = customSnackbarView.findViewById(R.id.snackbar_dismiss1);

        // Set message and icon
        textView.setText(message);
        iconImageView.setImageResource(R.drawable.baseline_error_24);

        // Set dismiss listener
        dismissImageView.setOnClickListener(v -> snackbar.dismiss());

        // Set custom view
        snackbar.getView().setPadding(45, 0, 45, 100);
        ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        snackbar.getView().setLayoutParams(params);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        ((ViewGroup) snackbar.getView()).removeAllViews();
        ((ViewGroup) snackbar.getView()).addView(customSnackbarView);

        snackbar.show();
    }
}
