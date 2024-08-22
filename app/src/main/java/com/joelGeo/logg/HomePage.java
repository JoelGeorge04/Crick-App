package com.joelGeo.logg;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Random;

public class HomePage extends AppCompatActivity {

    AppCompatButton addPlayer1, addPlayer2;
    private Button flipButton;
    private TextView resultTextView;
    private TextView bowlerNameTextView, batsman1NameTextView, batsman2NameTextView;
    private boolean isFlipping = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        addPlayer1 = findViewById(R.id.team1);
        addPlayer2 = findViewById(R.id.team2);

        addPlayer1.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), Project.class);
            startActivity(i);
        });

        addPlayer2.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), Project.class);
            startActivity(i);
        });

        flipButton = findViewById(R.id.flipButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Initialize TextViews for bowler and batsmen
        bowlerNameTextView = findViewById(R.id.bowlerNameValue);
        batsman1NameTextView = findViewById(R.id.batsman1Name);
        batsman2NameTextView = findViewById(R.id.batsman2Name);

        // Set bowler and batsmen details
        setCricketGameDetails("James Anderson", "Virat Kohli", "Steve Smith");

        flipButton.setOnClickListener(v -> {
            if (!isFlipping) {
                flipCoin();
            }
        });
    }

    // Method to set the names of bowler and batsmen
    private void setCricketGameDetails(String bowler, String batsman1, String batsman2) {
        bowlerNameTextView.setText(bowler);
        batsman1NameTextView.setText(batsman1);
        batsman2NameTextView.setText(batsman2);
    }

    private void flipCoin() {
        isFlipping = true;
        flipButton.setEnabled(false);

        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                String result = new Random().nextBoolean() ? "Heads" : "Tails";
                if ("Heads".equals(result)) {
                    resultTextView.setText("Team 1 Won the Toss!");
                } else {
                    resultTextView.setText("Team 2 Won the Toss!");
                }

                // Example logic: You can add specific logic here after the coin toss
                updateMatchDetails(result);

                isFlipping = false;
                flipButton.setEnabled(true);
            });
        }).start();
    }

    // Example method to update the match details after coin toss
    private void updateMatchDetails(String tossResult) {
        // Example: You can implement further match logic based on the toss result
        if ("Heads".equals(tossResult)) {
            bowlerNameTextView.setText("Team 2 Bowler: James Anderson");
            batsman1NameTextView.setText("Team 1 Batsman 1: Virat Kohli");
            batsman2NameTextView.setText("Team 1 Batsman 2: Steve Smith");
        } else {
            bowlerNameTextView.setText("Team 1 Bowler: Pat Cummins");
            batsman1NameTextView.setText("Team 2 Batsman 1: David Warner");
            batsman2NameTextView.setText("Team 2 Batsman 2: Joe Root");
        }
    }
}
