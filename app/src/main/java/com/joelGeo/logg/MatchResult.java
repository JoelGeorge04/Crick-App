package com.joelGeo.logg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MatchResult extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private DatabaseReference matchDatabaseRef;
    private AppCompatButton back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), Choice.class);
            startActivity(i);
        });

        // Reference to the RelativeLayout
        relativeLayout = findViewById(R.id.relativeLayout);

        // Firebase reference to the match data
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        matchDatabaseRef = database.getReference("match");

        // Fetch data from Firebase and add dynamic card views
        fetchMatchDataFromFirebase();
    }

    private void fetchMatchDataFromFirebase() {
        matchDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int matchCount = 1;

                for (DataSnapshot matchSnapshot : dataSnapshot.getChildren()) {
                    String matchKey = matchSnapshot.getKey();
                    String tossResult = matchSnapshot.child("toss").getValue(String.class);
                    String matchResult = matchSnapshot.child("winner").getValue(String.class);

                    // Add a new card view for each match in Firebase
                    addDynamicCardView(matchCount, matchKey, tossResult, matchResult);

                    matchCount++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchResult.this, "Failed to load match data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDynamicCardView(int matchNumber, String matchKey, String tossResult, String matchResult) {
        CardView cardView = new CardView(this);
        cardView.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (matchNumber == 1) {
            params.addRule(RelativeLayout.BELOW, R.id.matchResultTitle);
        } else {
            params.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(relativeLayout.getChildCount() - 1).getId());
        }

        params.setMargins(0, 16, 0, 0);
        cardView.setLayoutParams(params);
        cardView.setCardElevation(8f);
        cardView.setRadius(12f);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

        TextView textView = new TextView(this);
        textView.setText("Match " + matchNumber + "\nWinner: " + matchResult + "\nToss: " + tossResult);
        textView.setTextSize(18);
        textView.setPadding(20, 20, 20, 20);
        textView.setTextColor(getResources().getColor(R.color.black));

        cardView.addView(textView);
        relativeLayout.addView(cardView);

        // Only pass the matchKey to MatchStatusPage via Intent
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(MatchResult.this, MatchStatusPage.class);
            intent.putExtra("matchKey", matchKey); // Pass the matchKey to the next activity
            startActivity(intent);
        });
    }
}
