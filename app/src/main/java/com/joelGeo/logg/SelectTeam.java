package com.joelGeo.logg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.joelGeo.logg.R;

public class SelectTeam extends AppCompatActivity {

    private EditText team1PlayersEditText;
    private EditText team2PlayersEditText;
    private LinearLayout playerInputsContainer;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);

        team1PlayersEditText = findViewById(R.id.team1PlayersEditText);
        team2PlayersEditText = findViewById(R.id.team2PlayersEditText);
        playerInputsContainer = findViewById(R.id.playerInputsContainer);

        sharedPreferences = getSharedPreferences("TeamPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Add TextWatcher to dynamically update fields when input changes
        team1PlayersEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePlayerInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        team2PlayersEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePlayerInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button tossButton = findViewById(R.id.tossButton);
        tossButton.setOnClickListener(v -> savePlayerNames());
    }

    private void updatePlayerInputs() {
        playerInputsContainer.removeAllViews(); // Clear previous inputs

        int team1Players = getIntFromEditText(team1PlayersEditText);
        int team2Players = getIntFromEditText(team2PlayersEditText);

        // Add input fields for Team 1
        for (int i = 1; i <= team1Players; i++) {
            EditText playerInput = new EditText(this);
            playerInput.setHint("Team 1 - Player " + i);
            playerInput.setTag("team1_player_" + i);
            playerInput.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            playerInputsContainer.addView(playerInput);
        }

        // Add input fields for Team 2
        for (int i = 1; i <= team2Players; i++) {
            EditText playerInput = new EditText(this);
            playerInput.setHint("Team 2 - Player " + i);
            playerInput.setTag("team2_player_" + i);
            playerInput.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            playerInputsContainer.addView(playerInput);
        }
    }

    private int getIntFromEditText(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    private void savePlayerNames() {
        int team1Players = getIntFromEditText(team1PlayersEditText);
        int team2Players = getIntFromEditText(team2PlayersEditText);

        // Save player names for Team 1
        for (int i = 1; i <= team1Players; i++) {
            EditText playerInput = findViewById(getResources().getIdentifier("team1_player_" + i, "id", getPackageName()));
            String playerName = playerInput != null ? playerInput.getText().toString() : "";
            editor.putString("team1_player_" + i, playerName);
        }

        // Save player names for Team 2
        for (int i = 1; i <= team2Players; i++) {
            EditText playerInput = findViewById(getResources().getIdentifier("team2_player_" + i, "id", getPackageName()));
            String playerName = playerInput != null ? playerInput.getText().toString() : "";
            editor.putString("team2_player_" + i, playerName);
        }

        editor.apply();
    }
}
