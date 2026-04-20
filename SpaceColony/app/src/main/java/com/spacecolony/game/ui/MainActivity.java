package com.spacecolony.game.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.spacecolony.game.R;
import com.spacecolony.game.model.Storage;

// Home Screen / Colony Overview
public class MainActivity extends AppCompatActivity {

    private TextView tvStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStats = findViewById(R.id.tvStats);

        findViewById(R.id.btnRecruit).setOnClickListener(v ->
                startActivity(new Intent(this, RecruitActivity.class)));
        findViewById(R.id.btnQuarters).setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class)));
        findViewById(R.id.btnSimulator).setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));
        findViewById(R.id.btnMission).setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));
        findViewById(R.id.btnStats).setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Storage s = Storage.getInstance();
        tvStats.setText("Total Crew: " + s.getTotalCrew()
                + "   Missions: " + s.getTotalMissions()
                + "   Wins: " + s.getWins());
    }
}
