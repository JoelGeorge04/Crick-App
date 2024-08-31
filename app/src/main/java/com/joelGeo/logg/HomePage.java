package com.joelGeo.logg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HomePage extends AppCompatActivity {

    private Button team1Button, team2Button, flipButton, addMatchButton;
    private TextView resultTextView;
    private boolean isFlipping = false;
    AppCompatButton back;
    // Firebase References
    private DatabaseReference databaseRef;
    private String tossResult;
    private String currentMatchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        back=(AppCompatButton)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Choice.class);
                startActivity(i);
            }
        });

        // Initialize UI elements
        team1Button = findViewById(R.id.team1);
        team2Button = findViewById(R.id.team2);
        flipButton = findViewById(R.id.flipButton);
        addMatchButton = findViewById(R.id.addMatch);
        resultTextView = findViewById(R.id.toss);

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("match");

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
                generateNewMatchKey();
                addTossResultToFirebase();
                addTeamDataToFirebase();
                clearTossResult(); // Clear the toss result after adding match
                clearLocalData();  // Clear local data from SharedPreferences
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

    private void generateNewMatchKey() {
        currentMatchKey = databaseRef.push().getKey();
    }

    private void addTossResultToFirebase() {
        if (currentMatchKey != null) {
            DatabaseReference tossRef = databaseRef.child(currentMatchKey).child("toss");
            tossRef.setValue(tossResult)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomePage.this, "Toss result added to Firebase successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomePage.this, "Failed to add toss result to Firebase!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void addTeamDataToFirebase() {
        if (currentMatchKey != null) {
            // Retrieve player data from SharedPreferences
            SharedPreferences team1Prefs = getSharedPreferences("Team1Players", MODE_PRIVATE);
            SharedPreferences team2Prefs = getSharedPreferences("Team2Players", MODE_PRIVATE);

            Map<String, Object> team1Data = new HashMap<>();
            Map<String, Object> team2Data = new HashMap<>();

            // Retrieve all players for Team 1
            for (int i = 1; i <= 12; i++) {
                String playerData = team1Prefs.getString("player" + i, null);
                if (playerData != null) {
                    String[] playerDetails = playerData.split(",");
                    if (playerDetails.length == 5) {
                        team1Data.put("player" + i, new users(playerDetails[0], playerDetails[1], playerDetails[2], playerDetails[3], playerDetails[4]));
                    }
                }
            }

            // Retrieve all players for Team 2
            for (int i = 1; i <= 12; i++) {
                String playerData = team2Prefs.getString("player" + i, null);
                if (playerData != null) {
                    String[] playerDetails = playerData.split(",");
                    if (playerDetails.length == 5) {
                        team2Data.put("player" + i, new userss(playerDetails[0], playerDetails[1], playerDetails[2], playerDetails[3], playerDetails[4]));
                    }
                }
            }

            // Add team data to Firebase
            DatabaseReference team1Ref = databaseRef.child(currentMatchKey).child("team1");
            DatabaseReference team2Ref = databaseRef.child(currentMatchKey).child("team2");

            team1Ref.setValue(team1Data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomePage.this, "Team 1 data added to Firebase successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomePage.this, "Failed to add Team 1 data to Firebase!", Toast.LENGTH_SHORT).show();
                    });

            team2Ref.setValue(team2Data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomePage.this, "Team 2 data added to Firebase successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomePage.this, "Failed to add Team 2 data to Firebase!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearTossResult() {
        tossResult = null;
        resultTextView.setText("");
    }

    private void clearLocalData() {
        // Clear SharedPreferences data
        SharedPreferences team1Prefs = getSharedPreferences("Team1Players", MODE_PRIVATE);
        SharedPreferences.Editor team1Editor = team1Prefs.edit();
        team1Editor.clear();
        team1Editor.apply();

        SharedPreferences team2Prefs = getSharedPreferences("Team2Players", MODE_PRIVATE);
        SharedPreferences.Editor team2Editor = team2Prefs.edit();
        team2Editor.clear();
        team2Editor.apply();
    }
}
