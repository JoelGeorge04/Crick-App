package com.joelGeo.logg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShufflePlayers extends AppCompatActivity {

    private TextView team1TextView, team2TextView, tossResultTextView;
    public AppCompatButton back;
    private Button shuffleAgainButton, tossButton, doneButton;
    private Vibrator vibrator;
    private List<String> team1, team2;
    private Map<String, Integer> playerTeamMap; // To track which team each player is assigned to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_players);

        back = findViewById(R.id.back);
        team1TextView = findViewById(R.id.team1TextView);
        team2TextView = findViewById(R.id.team2TextView);
        tossResultTextView = findViewById(R.id.tossResultTextView);
        shuffleAgainButton = findViewById(R.id.shuffleAgainButton);
        tossButton = findViewById(R.id.tossButton);
        doneButton = findViewById(R.id.doneButton);

        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize the map to track player assignments
        playerTeamMap = new HashMap<>();

        // Receive data from SelectTeam activity
        Intent intent = getIntent();
        String[] playerNames = intent.getStringArrayExtra("playerNames");
        int playerCount = intent.getIntExtra("playerCount", 0);

        // Shuffle and display teams initially
        shuffleAndDisplayTeams(playerNames);

        // Back button to return to the SelectTeam activity
        back.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), SelectTeam.class);
            startActivity(i);
        });

        // Shuffle again button
        shuffleAgainButton.setOnClickListener(view -> {
            vibrateDevice();
            shuffleAndDisplayTeams(playerNames);
        });

        // Toss button
        tossButton.setOnClickListener(view -> {
            vibrateDevice();
            performToss();
        });

        // Done button
        doneButton.setOnClickListener(view -> {
            storeTeamsInPreferences(); // Save team data to SharedPreferences

            // Prepare player names for Team 1 and Team 2 to pass to the Project and Projectt activities
            Intent intentProject = new Intent(ShufflePlayers.this, Project.class);
            intentProject.putExtra("team1Names", team1.toArray(new String[0]));

            Intent intentProjectt = new Intent(ShufflePlayers.this, Projectt.class);
            intentProjectt.putExtra("team2Names", team2.toArray(new String[0]));

            // Start the Project and Projectt activities
            startActivity(intentProject);
            startActivity(intentProjectt);

            // After both activities are started, navigate to HomePage
            Intent homeIntent = new Intent(ShufflePlayers.this, HomePage.class);
            startActivity(homeIntent);
        });
    }

    // Method to shuffle and display teams
    private void shuffleAndDisplayTeams(String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            List<String> jPlayers = new ArrayList<>();
            List<String> others = new ArrayList<>();
            for (String player : playerNames) {
                if (player != null && (player.startsWith("J") || player.startsWith("j"))) {
                    jPlayers.add(player);
                } else {
                    others.add(player);
                }
            }

            team1 = new ArrayList<>();
            team2 = new ArrayList<>();

            int jTeam = 0;
            if (!playerTeamMap.isEmpty()) {
                for (String jPlayer : jPlayers) {
                    if (playerTeamMap.containsKey(jPlayer)) {
                        jTeam = playerTeamMap.get(jPlayer);
                        break;
                    }
                }
            }

            for (String jPlayer : jPlayers) {
                String updatedName;
                if (jPlayer.startsWith("Jo") || jPlayer.startsWith("jo")) {
                    updatedName = jPlayer + " (vc)";
                } else {
                    updatedName = jPlayer + " (c)";
                }

                if (jTeam == 0) {
                    if (team1.size() <= team2.size()) {
                        team1.add(updatedName);
                        playerTeamMap.put(jPlayer, 1);
                    } else {
                        team2.add(updatedName);
                        playerTeamMap.put(jPlayer, 2);
                    }
                    jTeam = playerTeamMap.get(jPlayer);
                } else {
                    if (jTeam == 1) {
                        team1.add(updatedName);
                        playerTeamMap.put(jPlayer, 1);
                    } else {
                        team2.add(updatedName);
                        playerTeamMap.put(jPlayer, 2);
                    }
                }
            }

            Collections.shuffle(others);

            int totalPlayers = playerNames.length;
            int teamSize = totalPlayers / 2;
            boolean addExtraToTeam1 = (totalPlayers % 2 != 0);

            int remainingTeamSize = teamSize + (addExtraToTeam1 ? 1 : 0);
            for (String player : others) {
                if (team1.size() < remainingTeamSize) {
                    team1.add(player);
                } else {
                    team2.add(player);
                }
            }

            Collections.shuffle(team1);
            Collections.shuffle(team2);

            team1TextView.setText("Team 1:\n" + String.join("\n", team1));
            team2TextView.setText("Team 2:\n" + String.join("\n", team2));

            tossResultTextView.setText("");
            team1TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
            team2TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
        }
    }

    private void performToss() {
        int jTeam = 0;
        boolean hasJPlayers = false;

        for (String player : team1) {
            if (player.startsWith("J") || player.startsWith("j")) {
                jTeam = 1;
                hasJPlayers = true;
                break;
            }
        }
        if (!hasJPlayers) {
            for (String player : team2) {
                if (player.startsWith("J") || player.startsWith("j")) {
                    jTeam = 2;
                    hasJPlayers = true;
                    break;
                }
            }
        }

        Random random = new Random();
        int tossResult;
        if (hasJPlayers && jTeam != 0 && random.nextInt(100) < 60) { // 60% probability
            tossResult = jTeam;
        } else {
            tossResult = random.nextInt(2) + 1; // Toss normally if no "J" players
        }

        if (tossResult == 1) {
            tossResultTextView.setText("Team 1 wins!");
            team1TextView.setBackgroundColor(getResources().getColor(R.color.winning_team_background));
            team2TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
        } else {
            tossResultTextView.setText("Team 2 wins!");
            team2TextView.setBackgroundColor(getResources().getColor(R.color.winning_team_background));
            team1TextView.setBackgroundColor(getResources().getColor(R.color.default_background));
        }
    }

    private void vibrateDevice() {
        if (vibrator != null && vibrator.hasVibrator()) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibrationEffect);
        }
    }

    private void storeTeamsInPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("TeamsPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder team1Builder = new StringBuilder();
        for (String player : team1) {
            // Only include the player name
            String playerName = player.split(" \\(")[0];
            team1Builder.append(playerName).append(",");
        }

        // Remove trailing comma
        if (team1Builder.length() > 0) {
            team1Builder.setLength(team1Builder.length() - 1);
        }

        editor.putString("team1", team1Builder.toString());
        editor.putString("team2", String.join(",", team2));
        editor.apply();
    }
}
