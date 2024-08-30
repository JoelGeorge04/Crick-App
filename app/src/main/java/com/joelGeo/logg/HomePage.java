package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class HomePage extends AppCompatActivity {

    private Button team1Button, team2Button, flipButton, addMatchButton;
    private TextView resultTextView;
    private boolean isFlipping = false;

    // Firebase References
    private DatabaseReference tossRef;
    private String tossResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize UI elements
        team1Button = findViewById(R.id.team1);
        team2Button = findViewById(R.id.team2);
        flipButton = findViewById(R.id.flipButton);
        addMatchButton = findViewById(R.id.addMatch);
        resultTextView = findViewById(R.id.toss);

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tossRef = database.getReference("match/toss");

        // Handle team buttons
        team1Button.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Project.class);
            intent.putExtra("playerIndex", 0); // Start with player index 0
            startActivity(intent);
        });

        team2Button.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Projectt.class);
            intent.putExtra("playerIndex", 0); // Start with player index 0
            startActivity(intent);
        });

        // Handle flip coin button
        flipButton.setOnClickListener(v -> {
            if (!isFlipping) {
                flipCoin();
            }
        });

        // Handle add match button
        addMatchButton.setOnClickListener(v -> {
            if (tossResult == null || tossResult.isEmpty()) {
                Toast.makeText(HomePage.this, "Please flip the coin to add toss result!", Toast.LENGTH_SHORT).show();
            } else {
                addTossResultToFirebase();
            }
        });
    }

    private void flipCoin() {
        isFlipping = true;
        flipButton.setEnabled(false);

        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate coin flipping delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                boolean team1Won = new Random().nextBoolean();
                tossResult = team1Won ? "Team 1 Won the Toss" : "Team 2 Won the Toss";
                resultTextView.setText(tossResult);
                isFlipping = false;
                flipButton.setEnabled(true);
            });
        }).start();
    }

    private void addTossResultToFirebase() {
        // Generate a new key for the toss result
        String tossId = tossRef.push().getKey();

        if (tossId != null) {
            tossRef.child(tossId).setValue(tossResult)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomePage.this, "Toss result added to Firebase successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomePage.this, "Failed to add toss result to Firebase!", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
