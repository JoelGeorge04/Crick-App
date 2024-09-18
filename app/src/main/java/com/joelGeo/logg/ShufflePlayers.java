package com.joelGeo.logg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ShufflePlayers extends AppCompatActivity {

    private TextView team1TextView, team2TextView, tossResultTextView;
    private AppCompatButton back, shuffleAgainButton, tossButton;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_players);

        back = findViewById(R.id.back);
        team1TextView = findViewById(R.id.team1TextView);
        team2TextView = findViewById(R.id.team2TextView);
        tossResultTextView = findViewById(R.id.tossResultTextView); // New TextView for toss result
        shuffleAgainButton = findViewById(R.id.shuffleAgainButton);
        tossButton = findViewById(R.id.tossButton); // New Button for toss

        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Receive data from SelectTeam activity
        Intent intent = getIntent();
        String[] playerNames = intent.getStringArrayExtra("playerNames");
        int playerCount = intent.getIntExtra("playerCount", 0);

        // Shuffle and display teams initially
        shuffleAndDisplayTeams(playerNames);

        // Back button to return to the SelectTeam activity
        back.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), SelectTeam.class);
            startActivity(i);
        });

        // Shuffle again button
        shuffleAgainButton.setOnClickListener(view -> {
            vibrateDevice();
            shuffleAndDisplayTeams(playerNames);
        });

        // Toss button
        tossButton.setOnClickListener(view -> {
            vibrateDevice();
            performToss();
        });
    }

    // Method to shuffle and display teams
    private void shuffleAndDisplayTeams(String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            List<String> playerList = new ArrayList<>();
            Collections.addAll(playerList, playerNames);
            Collections.shuffle(playerList);

            // Split players into two teams
            List<String> team1 = new ArrayList<>();
            List<String> team2 = new ArrayList<>();

            for (int i = 0; i < playerList.size(); i++) {
                if (i % 2 == 0) {
                    team1.add(playerList.get(i));
                } else {
                    team2.add(playerList.get(i));
                }
            }

            // Display teams
            team1TextView.setText("Team 1:\n" + String.join("\n", team1));
            team2TextView.setText("Team 2:\n" + String.join("\n", team2));

            // Clear toss result and reset backgrounds when teams are shuffled
            tossResultTextView.setText("");
            team1TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
            team2TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
        }
    }

    // Method to perform toss and display the result
    private void performToss() {
        Random random = new Random();
        int tossResult = random.nextInt(2); // 0 for Team 1, 1 for Team 2

        if (tossResult == 0) {
            tossResultTextView.setText("Team 1 wins!");
            team1TextView.setBackgroundColor(getResources().getColor(R.color.winning_team_background));
            team2TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
        } else {
            tossResultTextView.setText("Team 2 wins!");
            team2TextView.setBackgroundColor(getResources().getColor(R.color.winning_team_background));
            team1TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
        }
    }

    // Method to vibrate the device
    private void vibrateDevice() {
        if (vibrator != null && vibrator.hasVibrator()) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibrationEffect);
        }
    }
}
