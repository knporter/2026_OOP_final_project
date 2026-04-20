package com.spacecolony.game.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.game.R;
import com.spacecolony.game.adapter.CrewAdapter;
import com.spacecolony.game.model.CrewMember;
import com.spacecolony.game.model.Storage;
import java.util.List;

// Simulator Screen — train crew, gain XP (XP=2 → skill +2)
public class SimulatorActivity extends AppCompatActivity {

    private CrewAdapter adapter;
    private TextView tvTrainSuccessStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewAdapter(Storage.getInstance().getSimulatorCrew(), true);
        rv.setAdapter(adapter);
        tvTrainSuccessStar = findViewById(R.id.tvTrainSuccessStar);

        // Train Selected: costs 2 XP and randomly upgrades one stat by +1.
        findViewById(R.id.btnTrain).setOnClickListener(v -> trainSelected());

        // Move back to Quarters (restores energy — crew recovery requirement)
        findViewById(R.id.btnToQuarters).setOnClickListener(v -> moveToQuarters());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateData(Storage.getInstance().getSimulatorCrew());
    }

    private void trainSelected() {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(this, "Select crew to train", Toast.LENGTH_SHORT).show();
            return;
        }
        Storage store = Storage.getInstance();

        for (int id : ids) {
            CrewMember m = store.getCrew(id);
            if (m != null && !m.canTrain()) {
                Toast.makeText(this, "You don't have enough XP value", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int id : ids) {
            CrewMember m = store.getCrew(id);
            if (m != null) {
                if (!m.consumeTrainingXp()) {
                    Toast.makeText(this, "You don't have enough XP value", Toast.LENGTH_SHORT).show();
                    return;
                }
                String stat = m.trainRandomStat();
                sb.append(m.getName())
                        .append(": +1 ")
                        .append(stat)
                        .append(" (XP: ")
                        .append(m.getExperience())
                        .append(")\n");
            }
        }

        Toast.makeText(this, "Trained!\n" + sb, Toast.LENGTH_LONG).show();
        playTrainSuccessAnimation();
        adapter.updateData(store.getSimulatorCrew());
    }

    private void moveToQuarters() {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(this, "Select crew to send home", Toast.LENGTH_SHORT).show();
            return;
        }
        Storage store = Storage.getInstance();
        for (int id : ids) store.moveToQuarters(id);
        Toast.makeText(this, "Crew sent to Quarters (energy restored)", Toast.LENGTH_SHORT).show();
        adapter.updateData(store.getSimulatorCrew());
    }

    private void playTrainSuccessAnimation() {
        tvTrainSuccessStar.setVisibility(View.VISIBLE);
        tvTrainSuccessStar.setAlpha(0f);
        tvTrainSuccessStar.setScaleX(0.3f);
        tvTrainSuccessStar.setScaleY(0.3f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tvTrainSuccessStar, View.ALPHA, 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvTrainSuccessStar, View.SCALE_X, 0.3f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvTrainSuccessStar, View.SCALE_Y, 0.3f, 1.2f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(tvTrainSuccessStar, View.ALPHA, 1f, 0f);
        fadeOut.setStartDelay(280);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fadeIn, scaleX, scaleY, fadeOut);
        set.setDuration(700);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvTrainSuccessStar.setVisibility(View.GONE);
            }
        });
        set.start();
    }
}
