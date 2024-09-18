package com.joelGeo.logg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Choice extends AppCompatActivity {

    Button b1, b2, b3;
    TextView userNameTextView;
    Vibrator vibrator;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choice);

        b1 = findViewById(R.id.startMatchButton);
        b2 = findViewById(R.id.viewResultsButton);
        b3 = findViewById(R.id.selectTeamButton);
        userNameTextView = findViewById(R.id.userNameTextView);

        // Initialize the vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Retrieve the current user from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            if (userName != null) {
                if (userName.equals("Joel George")) {
                    userNameTextView.setText(userName + " (vc)");
                } else if (userName.equals("Joyal Babu") || userName.equalsIgnoreCase("jbt") || userName.equalsIgnoreCase("joyal babu")) {
                    userNameTextView.setText(userName + " (C)");
                } else if (userName.equalsIgnoreCase("Dinil Francis")) {
                    userNameTextView.setText(userName + "\n       (⭐)");
                } else {
                    userNameTextView.setText(userName + "!");
                }
            } else {
                userNameTextView.setText("User!");
            }
        } else {
            userNameTextView.setText("Guest!");
        }

        // Add vibration to button clicks
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                Intent i = new Intent(getApplicationContext(), HomePage.class);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                Intent intent = new Intent(Choice.this, MatchResult.class);
                startActivity(intent);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                Intent intent = new Intent(Choice.this, SelectTeam.class);
                startActivity(intent);
            }
        });
    }

    // Vibration method
    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for 100 milliseconds
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibrationEffect);
        }
    }
}
