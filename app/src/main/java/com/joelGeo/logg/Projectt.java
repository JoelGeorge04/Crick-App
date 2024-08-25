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
import com.joelGeo.logg.databinding.ActivityProjecttBinding;

public class Projectt extends AppCompatActivity {

    ActivityProjecttBinding binding;
    String name, sixs, fours, role,wickets;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_projectt);
        binding = ActivityProjecttBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the RadioGroup and set an onCheckedChangeListener
        binding.roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRole = findViewById(checkedId);
                if (selectedRole != null) {
                    role = selectedRole.getText().toString();
                    Log.d("Project", "Selected role: " + role);
                } else {
                    Log.d("Project", "No role selected or invalid ID");
                }
            }

        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = binding.nam.getText().toString();
                sixs = binding.sixs.getText().toString();
                fours = binding.fours.getText().toString();
                wickets = binding.wickets.getText().toString();

                if (!name.isEmpty() && !sixs.isEmpty() && !fours.isEmpty() && role != null) {
                    users u = new users(name, sixs, fours, role, wickets);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Team2"); // Changed the path to "users"
                    reference.child(name).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.nam.setText("");
                            binding.sixs.setText("");
                            binding.fours.setText("");
                            binding.wickets.setText("");

                            Toast.makeText(Projectt.this, "Updated to Database!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(Projectt.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
