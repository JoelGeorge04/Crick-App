package com.joelGeo.logg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MatchResult extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private DatabaseReference matchDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        // Reference to the RelativeLayout
        relativeLayout = findViewById(R.id.relativeLayout);

        // Firebase reference to the match data
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        matchDatabaseRef = database.getReference("match");

        // Add static card views for Match 1 and Match 2
        setupStaticCardViews();

        // Fetch data from Firebase and add dynamic card views
        fetchMatchDataFromFirebase();
    }

    private void setupStaticCardViews() {
        // Setting up click listeners for static CardView1 and CardView2
        CardView cardView1 = findViewById(R.id.cardView1);
        CardView cardView2 = findViewById(R.id.cardView2);

        cardView1.setOnClickListener(v -> {
            Intent intent = new Intent(MatchResult.this, MatchStatusPage.class);
            intent.putExtra("matchNumber", 1); // Send match number to MatchStatusPage
            startActivity(intent);
        });

        cardView2.setOnClickListener(v -> {
            Intent intent = new Intent(MatchResult.this, MatchStatusPage.class);
            intent.putExtra("matchNumber", 2); // Send match number to MatchStatusPage
            startActivity(intent);
        });
    }

    private void fetchMatchDataFromFirebase() {
        matchDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int matchCount = 3; // Starting index for dynamically added matches

                for (DataSnapshot matchSnapshot : dataSnapshot.getChildren()) {
                    String matchKey = matchSnapshot.getKey();
                    String tossResult = matchSnapshot.child("toss").getValue(String.class);

                    // Add a new card view for each match in Firebase
                    addDynamicCardView(matchCount, matchKey, tossResult);

                    matchCount++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchResult.this, "Failed to load match data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDynamicCardView(int matchNumber, String matchKey, String tossResult) {
        // Create a new CardView dynamically
        CardView cardView = new CardView(this);
        cardView.setId(View.generateViewId());

        // Set layout parameters for the CardView
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Place the CardView below the last element
        if (matchNumber == 3) {
            params.addRule(RelativeLayout.BELOW, R.id.cardView2); // First dynamic card below CardView2
        } else {
            params.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(relativeLayout.getChildCount() - 1).getId());
        }

        params.setMargins(0, 16, 0, 0);
        cardView.setLayoutParams(params);
        cardView.setCardElevation(8f);
        cardView.setRadius(12f);

        // Add TextView inside the CardView to display match info
        TextView textView = new TextView(this);
        textView.setText("Match " + matchNumber + ": " + tossResult);
        textView.setTextSize(18);
        textView.setPadding(20, 20, 20, 20);
        textView.setTextColor(getResources().getColor(R.color.black));

        cardView.addView(textView);

        // Add the CardView to the RelativeLayout
        relativeLayout.addView(cardView);

        // Set OnClickListener to navigate to match status page
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(MatchResult.this, MatchStatusPage.class);
            intent.putExtra("matchKey", matchKey); // Pass match key to the next page
            startActivity(intent);
        });
    }
}
