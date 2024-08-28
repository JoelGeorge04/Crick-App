package com.joelGeo.logg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.joelGeo.logg.databinding.ActivityProjectBinding;

public class Project extends AppCompatActivity {

    private ActivityProjectBinding binding;
    private String name, sixs, fours, role, wickets;
    private int playerIndex;
    private TextView playerIndexTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerIndexTextView = findViewById(R.id.playerIndexTextView); // Make sure to add this TextView to your layout

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

            // Save data to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("TeamData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("playerName" + playerIndex, name);
            editor.putString("playerSixs" + playerIndex, sixs);
            editor.putString("playerFours" + playerIndex, fours);
            editor.putString("playerWickets" + playerIndex, wickets);
            editor.putString("playerRole" + playerIndex, role);
            editor.apply();

            // Move to next player or finish if done
            if (playerIndex < 3) {
                Intent intent = new Intent(Project.this, Project.class);
                intent.putExtra("playerIndex", playerIndex + 1);
                startActivity(intent);
            } else {
                Toast.makeText(Project.this, "Team 1 players added successfully!", Toast.LENGTH_LONG).show();

                // Clear SharedPreferences for next match
                SharedPreferences.Editor editorClear = sharedPreferences.edit();
                editorClear.clear();
                editorClear.apply();

                // Return to HomePage
                Intent intent = new Intent(Project.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
