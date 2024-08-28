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

import com.joelGeo.logg.databinding.ActivityProjecttBinding;

public class Projectt extends AppCompatActivity {

    private ActivityProjecttBinding binding;
    private String name, sixs, fours, role, wickets;
    private int playerIndex;
    private TextView playerIndexTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjecttBinding.inflate(getLayoutInflater());
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
                Toast.makeText(Projectt.this, "Please fill all the fields and select a role!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("TeamData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("playerName" + (playerIndex + 4), name);
            editor.putString("playerSixs" + (playerIndex + 4), sixs);
            editor.putString("playerFours" + (playerIndex + 4), fours);
            editor.putString("playerWickets" + (playerIndex + 4), wickets);
            editor.putString("playerRole" + (playerIndex + 4), role);
            editor.apply();

            // Move to next player or finish if done
            if (playerIndex < 3) {
                Intent intent = new Intent(Projectt.this, Projectt.class);
                intent.putExtra("playerIndex", playerIndex + 1);
                startActivity(intent);
            } else {
                Toast.makeText(Projectt.this, "Team 2 players added successfully!", Toast.LENGTH_LONG).show();

                // Clear SharedPreferences for next match
                SharedPreferences.Editor editorClear = sharedPreferences.edit();
                editorClear.clear();
                editorClear.apply();

                // Return to HomePage
                Intent intent = new Intent(Projectt.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
