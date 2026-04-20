package com.spacecolony.game.ui;

import android.os.Bundle;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.game.R;
import com.spacecolony.game.adapter.CrewAdapter;
import com.spacecolony.game.model.CrewMember;
import com.spacecolony.game.model.MissionControl;
import com.spacecolony.game.model.Storage;
import java.util.List;

/**
 * Mission Control Screen.
 * Select 2 crew -> Launch Mission -> step through rounds -> return survivors to Quarters.
 * Crew Recovery: survivors sent to Quarters have energy fully restored.
 */
public class MissionControlActivity extends AppCompatActivity {

    private CrewAdapter adapter;
    private MissionControl currentMission;
    private TextView tvMissionLog;
    private ImageView ivThreatPreview;
    private Button btnLaunch, btnNextRound, btnReturnCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewAdapter(Storage.getInstance().getMissionControlCrew(), true);
        rv.setAdapter(adapter);

        tvMissionLog = findViewById(R.id.tvMissionLog);
        ivThreatPreview = findViewById(R.id.ivThreatPreview);
        btnLaunch    = findViewById(R.id.btnLaunch);
        btnNextRound = findViewById(R.id.btnNextRound);
        btnReturnCrew = findViewById(R.id.btnReturnCrew);

        btnNextRound.setEnabled(false);
        btnReturnCrew.setEnabled(false);

        btnLaunch.setOnClickListener(v -> launchMission());
        btnNextRound.setOnClickListener(v -> nextRound());
        // Crew Recovery: send survivors to Quarters (energy fully restored)
        btnReturnCrew.setOnClickListener(v -> returnSurvivorsToQuarters());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateData(Storage.getInstance().getMissionControlCrew());
    }

    private void launchMission() {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.size() != 2) {
            Toast.makeText(this, "Select exactly 2 crew members", Toast.LENGTH_SHORT).show();
            return;
        }
        Storage store = Storage.getInstance();
        CrewMember a = store.getCrew(ids.get(0));
        CrewMember b = store.getCrew(ids.get(1));
        if (a == null || b == null) {
            Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
            return;
        }
        currentMission = new MissionControl(a, b);
        updateThreatPreview();
        btnLaunch.setEnabled(false);
        btnNextRound.setEnabled(true);
        updateLog();
    }

    private void nextRound() {
        if (currentMission == null) return;
        boolean done = currentMission.executeRound();
        updateLog();
        if (done) {
            btnNextRound.setEnabled(false);
            // Only show Return button if there are survivors
            boolean hasSurvivors = !currentMission.getMemberA().isDefeated()
                    || !currentMission.getMemberB().isDefeated();
            btnReturnCrew.setEnabled(hasSurvivors);
            if (!hasSurvivors) {
                btnLaunch.setEnabled(true);
                adapter.updateData(Storage.getInstance().getMissionControlCrew());
            }
        }
    }

    /**
     * Crew Recovery: surviving crew are sent to Quarters where energy is fully restored.
     * Experience points are retained (handled in Storage.moveToQuarters).
     */
    private void returnSurvivorsToQuarters() {
        if (currentMission == null) return;
        Storage store = Storage.getInstance();
        CrewMember a = currentMission.getMemberA();
        CrewMember b = currentMission.getMemberB();
        if (!a.isDefeated()) store.moveToQuarters(a.getId());
        if (!b.isDefeated()) store.moveToQuarters(b.getId());
        currentMission = null;
        ivThreatPreview.setVisibility(View.GONE);
        btnLaunch.setEnabled(true);
        btnNextRound.setEnabled(false);
        btnReturnCrew.setEnabled(false);
        adapter.updateData(store.getMissionControlCrew());
        tvMissionLog.setText("Survivors sent to Quarters. Energy restored.\nSelect 2 crew for next mission.");
    }

    private void updateLog() {
        if (currentMission == null) return;
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (String line : currentMission.getLog()) {
            int start = sb.length();
            sb.append(line).append("\n");
            int end = sb.length();
            int color = line.contains("CRITICAL HIT!") ? Color.parseColor("#C62828") : Color.BLACK;
            sb.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvMissionLog.setText(sb);
    }

    private void updateThreatPreview() {
        if (currentMission == null) {
            ivThreatPreview.setVisibility(View.GONE);
            return;
        }

        String threatName = currentMission.getThreat().getName();
        if ("Asteroid Storm".equals(threatName)) {
            ivThreatPreview.setImageResource(R.drawable.threat_asteroid);
            ivThreatPreview.setVisibility(View.VISIBLE);
        } else if ("Alien Probe".equals(threatName)) {
            ivThreatPreview.setImageResource(R.drawable.threat_alien);
            ivThreatPreview.setVisibility(View.VISIBLE);
        } else {
            ivThreatPreview.setVisibility(View.GONE);
        }
    }
}
