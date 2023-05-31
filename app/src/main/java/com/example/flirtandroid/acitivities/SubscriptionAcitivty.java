package com.example.flirtandroid.acitivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flirtandroid.R;
import com.example.flirtandroid.SnackBar.WarningCustomSnackbar;

public class SubscriptionAcitivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_acitivty);
        Button subscriptionBtn = findViewById(R.id.subscribe_btn);
        subscriptionBtn.setAllCaps(false);
        ImageView backBtn = findViewById(R.id.back_btn);
        LinearLayout boxA = findViewById(R.id.box_a);
        LinearLayout boxB = findViewById(R.id.box_b);
        LinearLayout boxC = findViewById(R.id.box_c);

        boxA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick 1: ");
                // Show toast message
                Toast.makeText(SubscriptionAcitivty.this, "You pressed one week plan", Toast.LENGTH_SHORT).show();
            }
        });
        boxB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick 2: ");
                Toast.makeText(SubscriptionAcitivty.this, "You pressed one month plan", Toast.LENGTH_SHORT).show();
            }
        });
        boxC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick 3: ");
                Toast.makeText(SubscriptionAcitivty.this, "You pressed one month plan", Toast.LENGTH_SHORT).show();
            }
        });

        subscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WarningCustomSnackbar.showSnackbar(SubscriptionAcitivty.this, "This feature is under development process.");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView subscriptionText = findViewById(R.id.subscription_text);
        String subscriptionInfo = "1. Subscription will be charged to your credit card through your google account at confirmation of purchase.\n\n" +
                "2. Your subscription will automatically renew unless you cancelled at least 24 hours before the end of the current valid period.\n\n" +
                "3. To end a subscription, disable auto-renewal and allow the current period to end. It will not be possible to immediately cancel a subscription.\n\n" +
                "4. Refunds are not available for unused portions if a subscription.";
        subscriptionText.setText(subscriptionInfo);

    }
}