package com.joelGeo.logg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joelGeo.logg.databinding.ActivityProjecttBinding;

public class Projectt extends AppCompatActivity {

    private ActivityProjecttBinding binding;
    private String name, sixs, fours, role, wickets;
    private int playerIndex;
    private TextView playerIndexTextView;
    private Button stopButton;
    private boolean isAddingPlayers = true;

    // Firebase Reference
    private DatabaseReference team2Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjecttBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerIndexTextView = findViewById(R.id.playerIndexTextView);
        stopButton = findViewById(R.id.stopButton);

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        team2Ref = database.getReference("match/team2");

        // Retrieve player index from intent
        playerIndex = getIntent().getIntExtra("playerIndex", 0);

        // Display the current player index
        playerIndexTextView.setText("Player " + (playerIndex + 1));

        // Get the RadioGroup and set an onCheckedChangeListener
        binding.roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRole = findViewById(checkedId);
            if (selectedRole != null) {
                role = selectedRole.getText().toString();
            } else {
                role = null;
            }
        });

        binding.add.setOnClickListener(view -> {
            if (!isAddingPlayers) {
                return; // Do nothing if adding players is stopped
            }

            // Retrieve input values
            name = binding.nam.getText().toString();
            sixs = binding.sixs.getText().toString();
            fours = binding.fours.getText().toString();
            wickets = binding.wickets.getText().toString();

            // Validate inputs
            if (name.isEmpty() || sixs.isEmpty() || fours.isEmpty() || role == null || role.isEmpty()) {
                Toast.makeText(Projectt.this, "Please fill all the fields and select a role!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save player data to SharedPreferences
            savePlayerData(playerIndex);

            // Move to next player or finish if done
            if (playerIndex < 11) { // Allow up to 12 players
                Intent intent = new Intent(Projectt.this, Projectt.class);
                intent.putExtra("playerIndex", playerIndex + 1);
                startActivity(intent);
                finish(); // Ensure to finish the current activity to prevent multiple instances
            } else {
                Toast.makeText(Projectt.this, "Team 2 players added successfully!", Toast.LENGTH_LONG).show();

                // Return to HomePage
                Intent intent = new Intent(Projectt.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        stopButton.setOnClickListener(view -> {
            if (isAddingPlayers) {
                // Save the current player data if adding players is stopped
                name = binding.nam.getText().toString();
                sixs = binding.sixs.getText().toString();
                fours = binding.fours.getText().toString();
                wickets = binding.wickets.getText().toString();

                // Validate inputs before saving
                if (!name.isEmpty() && !sixs.isEmpty() && !fours.isEmpty() && role != null && !role.isEmpty()) {
                    savePlayerData(playerIndex);
                }

                isAddingPlayers = false; // Stop adding players
                Toast.makeText(Projectt.this, "Stopped adding players.", Toast.LENGTH_SHORT).show();

                // Redirect to HomePage or another activity
                Intent intent = new Intent(Projectt.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void savePlayerData(int playerIndex) {
        SharedPreferences sharedPreferences = getSharedPreferences("Team2Players", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String playerData = name + "," + sixs + "," + fours + "," + wickets + "," + role;
        editor.putString("player" + (playerIndex + 1), playerData);
        editor.apply();
    }
}
