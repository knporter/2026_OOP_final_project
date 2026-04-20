package com.spacecolony.game.ui;

import android.os.Bundle;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.game.R;
import com.spacecolony.game.adapter.CrewAdapter;
import com.spacecolony.game.model.CrewMember;
import com.spacecolony.game.model.Storage;
import java.util.List;

/**
 * Statistics Screen (Bonus +1).
 * Shows colony-wide stats and per-crew stats.
 */
public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Storage s = Storage.getInstance();
        List<CrewMember> allCrew = s.getAllCrew();

        TextView tvColony = findViewById(R.id.tvColonyStats);
        tvColony.setText(
                "Total Crew Recruited: " + s.getTotalCrew() + "\n"
                + "Total Missions: " + s.getTotalMissions() + "\n"
                + "Missions Won: " + s.getWins() + "\n"
                + "Missions Lost: " + (s.getTotalMissions() - s.getWins())
        );
        renderHistoryBarChart(allCrew);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CrewAdapter(allCrew, false));
    }

    private void renderHistoryBarChart(List<CrewMember> crew) {
        LinearLayout container = findViewById(R.id.barChartContainer);
        container.removeAllViews();

        if (crew.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew data yet.");
            empty.setTextColor(Color.parseColor("#546E7A"));
            empty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            container.addView(empty);
            return;
        }

        int maxScore = 1;
        for (CrewMember m : crew) {
            maxScore = Math.max(maxScore, m.getHistoricalScore());
        }

        int chartHeightPx = dp(130);
        for (CrewMember m : crew) {
            int score = m.getHistoricalScore();
            int barHeight = Math.max(dp(8), (int) ((score / (float) maxScore) * chartHeightPx));

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            item.setPadding(dp(6), dp(0), dp(6), dp(0));
            LinearLayout.LayoutParams itemLp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            item.setLayoutParams(itemLp);

            TextView value = new TextView(this);
            value.setText(String.valueOf(score));
            value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            value.setTextColor(Color.parseColor("#37474F"));
            value.setGravity(Gravity.CENTER);

            View bar = new View(this);
            LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(dp(26), barHeight);
            barLp.topMargin = dp(4);
            bar.setLayoutParams(barLp);
            bar.setBackgroundColor(parseCrewColor(m.getColor()));

            TextView name = new TextView(this);
            name.setText(m.getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            name.setTextColor(Color.parseColor("#263238"));
            name.setGravity(Gravity.CENTER);
            name.setMaxEms(6);
            name.setSingleLine(true);
            name.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams nameLp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            nameLp.topMargin = dp(6);
            name.setLayoutParams(nameLp);

            item.addView(value);
            item.addView(bar);
            item.addView(name);
            container.addView(item);
        }
    }

    private int parseCrewColor(String colorName) {
        switch (colorName) {
            case "Blue":   return Color.parseColor("#1565C0");
            case "Yellow": return Color.parseColor("#1565C0");
            case "Green":  return Color.parseColor("#2E7D32");
            case "Purple": return Color.parseColor("#6A1B9A");
            case "Red":    return Color.parseColor("#C62828");
            default:       return Color.GRAY;
        }
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}
