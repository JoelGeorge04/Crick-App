package com.joelGeo.logg;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joelGeo.logg.databinding.ActivityProjectBinding;

public class Project extends AppCompatActivity {

    ActivityProjectBinding binding;
    String name, sixs, fours, role = null, wickets;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Team1");

        // Get the RadioGroup and set an onCheckedChangeListener
        binding.roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRole = findViewById(checkedId);
            if (selectedRole != null) {
                role = selectedRole.getText().toString();
                Log.d("Project", "Selected role: " + role);
            } else {
                role = null;
                Log.d("Project", "No role selected or invalid ID");
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
                return; // Exit the method to prevent further execution
            }

            // Create user object and save to Firebase
            users u = new users(name, sixs, fours, role, wickets);
            reference.child(name).setValue(u).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Clear input fields and selections on success
                    binding.nam.setText("");
                    binding.sixs.setText("");
                    binding.fours.setText("");
                    binding.wickets.setText("");
                    binding.roleRadioGroup.clearCheck(); // Clear RadioGroup selection

                    Toast.makeText(Project.this, "Updated to Database!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Project.this, "Failed to update database!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
