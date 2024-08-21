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

        flipButton.setOnClickListener(v -> {
            if (!isFlipping) {
                flipCoin();
            }
        });
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



                isFlipping = false;
                flipButton.setEnabled(true);
            });
        }).start();
    }

}
