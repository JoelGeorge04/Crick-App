package com.joelGeo.logg;

import static com.joelGeo.logg.R.id.backButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectTeam extends AppCompatActivity {

    private EditText playerCountEditText;
    private LinearLayout playerNamesContainer;
    private Button createButton;
    private AppCompatButton back;
    private Vibrator vibrator;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);

        playerCountEditText = findViewById(R.id.playerCountEditText);
        playerNamesContainer = findViewById(R.id.playerNamesContainer);
        createButton = findViewById(R.id.createButton);
        back = (AppCompatButton) findViewById(R.id.backButton);

        // Initialize the Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initially disable the "Create Team" button
        createButton.setEnabled(false);

        // Set up the "Back" button click listener
        back.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), Choice.class);
            startActivity(i);
        });

        // Add a TextWatcher to the player count EditText
        playerCountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                playerNamesContainer.removeAllViews();

                String playerCountStr = playerCountEditText.getText().toString();
                int playerCount;
                try {
                    playerCount = Integer.parseInt(playerCountStr);
                } catch (NumberFormatException e) {
                    playerCount = 0;
                }

                for (int i = 0; i < playerCount; i++) {
                    EditText playerNameEditText = new EditText(SelectTeam.this);
                    playerNameEditText.setHint("Enter name for player " + (i + 1));
                    playerNameEditText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    playerNamesContainer.addView(playerNameEditText);
                }

                createButton.setEnabled(playerCount > 0);
            }
        });

        // Set up the "Create" button click listener
        createButton.setOnClickListener(view -> {
            // Vibrate on button click (for 100 milliseconds)
            vibrator.vibrate(100);

            sendPlayerData();
        });
    }

    private void sendPlayerData() {
        int playerCount = playerNamesContainer.getChildCount();
        List<String> playerNames = new ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            EditText playerNameEditText = (EditText) playerNamesContainer.getChildAt(i);
            String playerName = playerNameEditText.getText().toString().trim();
            if (!playerName.isEmpty()) {
                playerNames.add(playerName);
            }
        }

        if (playerNames.size() <= 1) {
            Toast.makeText(SelectTeam.this, "Please enter two player names.", Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.shuffle(playerNames);

        String[] playerNamesArray = playerNames.toArray(new String[0]);

        Intent intent = new Intent(SelectTeam.this, ShufflePlayers.class);
        intent.putExtra("playerNames", playerNamesArray);
        intent.putExtra("playerCount", playerNames.size());
        startActivity(intent);
    }
}
