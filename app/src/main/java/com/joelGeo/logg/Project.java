package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
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

public class Project extends AppCompatActivity {

    ActivityProjectBinding binding;
    String name,age,dob,status;
    FirebaseDatabase db;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name=binding.nam.getText().toString();
                age=binding.age.getText().toString();
                dob=binding.dob.getText().toString();

                if(!name.isEmpty() && !age.isEmpty() && !dob.isEmpty());{

                    users u = new users(name,age,dob);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("name");// name of the path in firebase
                    reference.child(name).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            binding.nam.setText("");
                            binding.age.setText("");
                            binding.dob.setText("");
                            Toast.makeText(Project.this,"Updated to Database!",Toast.LENGTH_LONG).show();
                        }
                    });

                }


            }
        });

    }
}
