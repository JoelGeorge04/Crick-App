package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joelGeo.logg.databinding.ActivityProjectBinding;

public class Project extends AppCompatActivity {

    private ActivityProjectBinding binding;
    private String name, sixs, fours, role, wickets;
    private int playerIndex;
    private TextView playerIndexTextView;
    private Button stopButton;
    private boolean isAddingPlayers = true;

    // Firebase Reference
    private DatabaseReference team1Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerIndexTextView = findViewById(R.id.playerIndexTextView);
        stopButton = findViewById(R.id.stopButton);

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        team1Ref = database.getReference("match/team1");

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
                Toast.makeText(Project.this, "Please fill all the fields and select a role!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add player data to Firebase
            addPlayerToFirebase(playerIndex);

            // Move to next player or finish if done
            if (playerIndex < 11) { // Allow up to 12 players
                Intent intent = new Intent(Project.this, Project.class);
                intent.putExtra("playerIndex", playerIndex + 1);
                startActivity(intent);
            } else {
                Toast.makeText(Project.this, "Team 1 players added successfully!", Toast.LENGTH_LONG).show();

                // Return to HomePage
                Intent intent = new Intent(Project.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        stopButton.setOnClickListener(view -> {
            isAddingPlayers = false; // Stop adding players
            Toast.makeText(Project.this, "Stopped adding players.", Toast.LENGTH_SHORT).show();

            // Optionally, redirect to HomePage or another activity
            Intent intent = new Intent(Project.this, HomePage.class);
            startActivity(intent);
            finish();
        });
    }

    private void addPlayerToFirebase(int playerIndex) {
        // Create a map of the player's data
        String playerId = "player" + (playerIndex + 1);
        users player = new users(name, sixs, fours, wickets, role);

        // Add the player to Firebase under match/team1/playerIndex
        team1Ref.child(playerId).setValue(player)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Project.this, "Player added to Firebase successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Project.this, "Failed to add player to Firebase!", Toast.LENGTH_SHORT).show();
                });
    }
}
