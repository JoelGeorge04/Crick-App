package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
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

public class MatchStatusPage extends AppCompatActivity {

    private LinearLayout team1Layout, team2Layout;
    private DatabaseReference matchDatabaseRef;
    private String matchKey;
    private AppCompatButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_status_page);

        team1Layout = findViewById(R.id.team1Layout);
        team2Layout = findViewById(R.id.team2Layout);

        back = findViewById(R.id.back);
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

                // Display Team 1 players
                DataSnapshot team1Snapshot = dataSnapshot.child("team1");
                displayTeamPlayers(team1Snapshot, team1Layout);

                // Display Team 2 players
                DataSnapshot team2Snapshot = dataSnapshot.child("team2");
                displayTeamPlayers(team2Snapshot, team2Layout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchStatusPage.this, "Failed to load match details!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTeamPlayers(DataSnapshot teamSnapshot, LinearLayout teamLayout) {
        for (DataSnapshot playerSnapshot : teamSnapshot.getChildren()) {
            String playerName = playerSnapshot.child("name").getValue(String.class);
            String playerSixes = playerSnapshot.child("sixs").getValue(String.class);
            String playerFours = playerSnapshot.child("fours").getValue(String.class);
            String playerWickets = playerSnapshot.child("wickets").getValue(String.class);
            String playerRole = playerSnapshot.child("role").getValue(String.class);

            // Create a TextView for each player and add to layout
            TextView playerTextView = new TextView(this);
            playerTextView.setText("Name: " + playerName + "\n" +
                    "Sixes: " + playerSixes + "\n" +
                    "Fours: " + playerFours + "\n" +
                    "Wickets: " + playerRole + "\n" +
                    "Role: " + playerWickets);
            playerTextView.setPadding(16, 16, 16, 16);
            playerTextView.setTextSize(16);

            teamLayout.addView(playerTextView);
        }
    }
}
