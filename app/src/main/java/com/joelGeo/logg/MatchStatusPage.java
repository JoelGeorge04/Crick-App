package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.AlertDialog;

public class MatchStatusPage extends AppCompatActivity {

    private LinearLayout team1Layout, team2Layout;
    private DatabaseReference matchDatabaseRef;
    private String matchKey;
    private AppCompatButton back, deleteMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_status_page);

        team1Layout = findViewById(R.id.team1Layout);
        team2Layout = findViewById(R.id.team2Layout);
        back = findViewById(R.id.back);
        deleteMatch = findViewById(R.id.deleteMatch);

        back.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MatchResult.class);
            startActivity(i);
        });

        // Get matchKey from the Intent
        Intent intent = getIntent();
        matchKey = intent.getStringExtra("matchKey");

        // Firebase reference to the match data
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        matchDatabaseRef = database.getReference("match");

        // Fetch match details from Firebase
        if (matchKey != null) {
            fetchMatchDetailsFromFirebase();
        } else {
            Toast.makeText(MatchStatusPage.this, "Match key not found!", Toast.LENGTH_SHORT).show();
        }

        // Delete match functionality
        deleteMatch.setOnClickListener(view -> {
            if (matchKey != null) {
                showDeleteConfirmationDialog();
            } else {
                Toast.makeText(MatchStatusPage.this, "No match key to delete!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Match")
                .setMessage("Are you sure you want to delete this match?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteMatchFromFirebase())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteMatchFromFirebase() {
        matchDatabaseRef.child(matchKey).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MatchStatusPage.this, "Match deleted successfully", Toast.LENGTH_SHORT).show();

                // Intent to navigate to the page of your choice after deletion
                Intent intent = new Intent(MatchStatusPage.this, Choice.class); // Change Choice to any other activity if needed
                startActivity(intent);  // Move to the next page after deletion

                finish(); // Close the current page
            } else {
                Toast.makeText(MatchStatusPage.this, "Failed to delete match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMatchDetailsFromFirebase() {
        matchDatabaseRef.child(matchKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous data
                team1Layout.removeAllViews();
                team2Layout.removeAllViews();

                // Get and display match result
                String tossResult = dataSnapshot.child("toss").getValue(String.class);
                String matchResult = dataSnapshot.child("winner").getValue(String.class);
                TextView resultTextView = findViewById(R.id.resultTextView);
                resultTextView.setText("Toss: " + tossResult + "\nWinner: " + matchResult);

                // Initialize variables to keep track of the highest scores and MVP
                String highScoreBatsman = "";
                int highScore = 0;
                String highWicketBowler = "";
                int highWickets = 0;
                String mvp = "";
                int maxScore = 0;
                int maxWickets = 0;

                // Display Team 1 players
                DataSnapshot team1Snapshot = dataSnapshot.child("team1");
                for (DataSnapshot playerSnapshot : team1Snapshot.getChildren()) {
                    String playerName = playerSnapshot.child("name").getValue(String.class);
                    String playerSixsStr = playerSnapshot.child("sixs").getValue(String.class);
                    String playerFoursStr = playerSnapshot.child("fours").getValue(String.class);
                    String playerWicketsStr = playerSnapshot.child("role").getValue(String.class);
                    String playerRole = playerSnapshot.child("wickets").getValue(String.class);

                    // Initialize default values
                    int playerSixs = 0;
                    int playerFours = 0;
                    int playerWickets = 0;

                    // Parse integer values safely
                    if (playerSixsStr != null && !playerSixsStr.isEmpty()) {
                        try {
                            playerSixs = Integer.parseInt(playerSixsStr);
                        } catch (NumberFormatException e) {
                            Log.e("MatchStatusPage", "Error parsing sixs", e);
                        }
                    }

                    if (playerFoursStr != null && !playerFoursStr.isEmpty()) {
                        try {
                            playerFours = Integer.parseInt(playerFoursStr);
                        } catch (NumberFormatException e) {
                            Log.e("MatchStatusPage", "Error parsing fours", e);
                        }
                    }

                    if (playerWicketsStr != null && !playerWicketsStr.isEmpty()) {
                        try {
                            playerWickets = Integer.parseInt(playerWicketsStr);
                        } catch (NumberFormatException e) {
                            Log.e("MatchStatusPage", "Error parsing wickets", e);
                        }
                    }

                    // Determine high score batsman
                    int playerScore = playerSixs * 6 + playerFours * 4; // Example scoring system
                    if (playerRole != null && playerRole.equals("Batsman")) {
                        if (playerScore > highScore) {
                            highScore = playerScore;
                            highScoreBatsman = playerName;
                        }
                    }

                    // Determine high wicket bowler
                    if (playerRole != null && playerRole.equals("Bowler")) {
                        if (playerWickets > highWickets) {
                            highWickets = playerWickets;
                            highWicketBowler = playerName;
                        }
                    }

                    // Determine MVP based on highest score or wickets
                    if (playerScore > maxScore) {
                        maxScore = playerScore;
                        mvp = playerName;
                    }
                    if (playerWickets > maxWickets) {
                        maxWickets = playerWickets;
                        mvp = playerName;
                    }

                    // Create a TextView for each player and add to layout
                    TextView playerTextView = new TextView(MatchStatusPage.this);
                    playerTextView.setText("Name: " + playerName + "\n" +
                            "Sixes: " + playerSixs + "\n" +
                            "Fours: " + playerFours + "\n" +
                            "Wickets: " + playerWickets + "\n" +
                            "Role: " + playerRole);
                    playerTextView.setPadding(16, 16, 16, 16);
                    playerTextView.setTextSize(16);

                    team1Layout.addView(playerTextView);
                }

                // Display Team 2 players
                DataSnapshot team2Snapshot = dataSnapshot.child("team2");
                for (DataSnapshot playerSnapshot : team2Snapshot.getChildren()) {
                    String playerName = playerSnapshot.child("name").getValue(String.class);
                    String playerSixsStr = playerSnapshot.child("sixs").getValue(String.class);
                    String playerFoursStr = playerSnapshot.child("fours").getValue(String.class);
                    String playerWicketsStr = playerSnapshot.child("role").getValue(String.class);
                    String playerRole = playerSnapshot.child("wickets").getValue(String.class);

                    // Initialize default values
                    int playerSixs = 0;
                    int playerFours = 0;
                    int playerWickets = 0;

                    // Parse integer values safely
                    if (playerSixsStr != null && !playerSixsStr.isEmpty()) {
                        try {
                            playerSixs = Integer.parseInt(playerSixsStr);
                        } catch (NumberFormatException e) {
                            Log.e("MatchStatusPage", "Error parsing sixs", e);
                        }
                    }

                    if (playerFoursStr != null && !playerFoursStr.isEmpty()) {
                        try {
                            playerFours = Integer.parseInt(playerFoursStr);
                        } catch (NumberFormatException e) {
                            Log.e("MatchStatusPage", "Error parsing fours", e);
                        }
                    }

                    if (playerWicketsStr != null && !playerWicketsStr.isEmpty()) {
                        try {
                            playerWickets = Integer.parseInt(playerWicketsStr);
                        } catch (NumberFormatException e) {
                            Log.e("MatchStatusPage", "Error parsing wickets", e);
                        }
                    }

                    // Determine high score batsman
                    int playerScore = playerSixs * 6 + playerFours * 4; // Example scoring system
                    if (playerRole != null && playerRole.equals("Batsman")) {
                        if (playerScore > highScore) {
                            highScore = playerScore;
                            highScoreBatsman = playerName;
                        }
                    }

                    // Determine high wicket bowler
                    if (playerRole != null && playerRole.equals("Bowler")) {
                        if (playerWickets > highWickets) {
                            highWickets = playerWickets;
                            highWicketBowler = playerName;
                        }
                    }

                    // Determine MVP based on highest score or wickets
                    if (playerScore > maxScore) {
                        maxScore = playerScore;
                        mvp = playerName;
                    }
                    if (playerWickets > maxWickets) {
                        maxWickets = playerWickets;
                        mvp = playerName;
                    }

                    // Create a TextView for each player and add to layout
                    TextView playerTextView = new TextView(MatchStatusPage.this);
                    playerTextView.setText("Name: " + playerName + "\n" +
                            "Sixes: " + playerSixs + "\n" +
                            "Fours: " + playerFours + "\n" +
                            "Wickets: " + playerWickets + "\n" +
                            "Role: " + playerRole);
                    playerTextView.setPadding(16, 16, 16, 16);
                    playerTextView.setTextSize(16);

                    team2Layout.addView(playerTextView);
                }

                // Update TextViews for high score batsman, high wicket bowler, and MVP
                TextView highScoreBatsmanTextView = findViewById(R.id.highScoreBatsman);
                highScoreBatsmanTextView.setText("High Score Batsman: " + highScoreBatsman);

                TextView highWicketBowlerTextView = findViewById(R.id.highWicketBowler);
                highWicketBowlerTextView.setText("High Wicket Bowler: " + highWicketBowler);

                TextView mvpTextView = findViewById(R.id.mvp);
                mvpTextView.setText("MVP: " + mvp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchStatusPage.this, "Failed to load match details!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
