package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joelGeo.logg.databinding.ActivityMainBinding;
import com.joelGeo.logg.databinding.ActivityProjectBinding;
import com.joelGeo.logg.databinding.ActivityProjecttBinding;

public class Projectt extends AppCompatActivity {

    ActivityProjecttBinding binding;
    String name, age, dob, role;
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
                    Log.d("Projectt", "Selected role: " + role);
                } else {
                    Log.d("Projectt", "No role selected or invalid ID");
                }
            }

        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = binding.nam.getText().toString();
                age = binding.age.getText().toString();
                dob = binding.dob.getText().toString();

                if (!name.isEmpty() && !age.isEmpty() && !dob.isEmpty() && role != null) {
                    userss u = new userss(name, age, dob, role);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Team2"); // Changed the path to "users"
                    reference.child(name).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.nam.setText("");
                            binding.age.setText("");
                            binding.dob.setText("");

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
