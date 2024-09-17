package com.joelGeo.logg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

        // Retrieve the current user from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Display the user's name with additional text
            String userName = currentUser.getDisplayName();
            if (userName != null) {
                if (userName.equals("Joel George")) {
                    userNameTextView.setText(userName + " (vc)");
                } else if (userName.equals("Joyal Babu")) {
                    userNameTextView.setText(userName + " (C)");
                } else {
                    userNameTextView.setText(userName + "!");
                }
            } else {
                userNameTextView.setText("User!");
            }
        } else {
            userNameTextView.setText("Guest!");
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HomePage.class);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choice.this, MatchResult.class);
                startActivity(intent);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choice.this, SelectTeam.class);
                startActivity(intent);
            }
        });
    }
}
