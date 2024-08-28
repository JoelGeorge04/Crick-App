package com.joelGeo.logg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HomePage extends AppCompatActivity {

    private Button team1Button, team2Button, flipButton, addMatchButton;
    private TextView resultTextView;
    private boolean isFlipping = false;

    // Firebase References
    private FirebaseDatabase db;
    private DatabaseReference matchesRef;
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

        // Initialize Firebase
        db = FirebaseDatabase.getInstance();
        matchesRef = db.getReference("match");

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

        // Handle adding match to Firebase
        addMatchButton.setOnClickListener(v -> {
            if (tossResult == null || tossResult.isEmpty()) {
                Toast.makeText(HomePage.this, "Please flip the coin to add match status!", Toast.LENGTH_SHORT).show();
            } else {
                addMatchToFirebase();
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

    private void addMatchToFirebase() {
        // Generate a new match ID
        String matchId = matchesRef.push().getKey();

        if (matchId != null) {
            // Create a map to store match data
            Map<String, Object> matchData = new HashMap<>();
            matchData.put("TossResult", tossResult);

            // Retrieve Team 1 and Team 2 data from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("TeamData", MODE_PRIVATE);

            // Add Team 1 data to match data
            Map<String, Object> team1Data = new HashMap<>();
            for (int i = 0; i < 4; i++) {
                Map<String, String> playerData = new HashMap<>();
                playerData.put("PlayerName", sharedPreferences.getString("playerName" + i, ""));
                playerData.put("Sixs", sharedPreferences.getString("playerSixs" + i, ""));
                playerData.put("Fours", sharedPreferences.getString("playerFours" + i, ""));
                playerData.put("Wickets", sharedPreferences.getString("playerWickets" + i, ""));
                playerData.put("Role", sharedPreferences.getString("playerRole" + i, ""));
                team1Data.put("Player" + (i + 1), playerData);
            }
            matchData.put("Team1", team1Data);

            // Add Team 2 data to match data
            Map<String, Object> team2Data = new HashMap<>();
            for (int i = 4; i < 8; i++) {
                Map<String, String> playerData = new HashMap<>();
                playerData.put("PlayerName", sharedPreferences.getString("playerName" + i, ""));
                playerData.put("Sixs", sharedPreferences.getString("playerSixs" + i, ""));
                playerData.put("Fours", sharedPreferences.getString("playerFours" + i, ""));
                playerData.put("Wickets", sharedPreferences.getString("playerWickets" + i, ""));
                playerData.put("Role", sharedPreferences.getString("playerRole" + i, ""));
                team2Data.put("Player" + (i - 3), playerData);
            }
            matchData.put("Team2", team2Data);

            // Push match data to Firebase
            matchesRef.child(matchId).setValue(matchData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(HomePage.this, "Match added successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(HomePage.this, "Failed to add match!", Toast.LENGTH_SHORT).show());
        }
    }
}
