package com.spacecolony.game.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.game.R;
import com.spacecolony.game.model.CrewMember;
import java.util.ArrayList;
import java.util.List;

// Requirement: RecyclerView for listing crew members
public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember member);
    }

    private List<CrewMember> crew;
    private final boolean showCheckbox;
    private final List<Integer> selectedIds = new ArrayList<>();
    private OnCrewClickListener listener;

    public CrewAdapter(List<CrewMember> crew, boolean showCheckbox) {
        this.crew = crew;
        this.showCheckbox = showCheckbox;
    }

    public void setOnCrewClickListener(OnCrewClickListener l) { this.listener = l; }

    public void updateData(List<CrewMember> newCrew) {
        this.crew = newCrew;
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIds() { return new ArrayList<>(selectedIds); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        CrewMember m = crew.get(position);

        h.tvName.setText(m.getName());
        h.tvSpec.setText(m.getSpecialization());
        h.tvStats.setText("Skill:" + m.getSkill() + "  Res:" + m.getResistance()
                + "  Energy:" + m.getCurrentEnergy() + "/" + m.getMaxEnergy()
                + "  XP:" + m.getExperience());

        // Color-code by specialization (bonus: different crew images via color)
        int color = parseColor(m.getColor());
        h.colorBar.setBackgroundColor(color);

        if (showCheckbox) {
            h.checkbox.setVisibility(View.VISIBLE);
            h.checkbox.setChecked(selectedIds.contains(m.getId()));
            h.checkbox.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    if (!selectedIds.contains(m.getId())) selectedIds.add(m.getId());
                } else {
                    selectedIds.remove(Integer.valueOf(m.getId()));
                }
            });
        } else {
            h.checkbox.setVisibility(View.GONE);
        }

        h.card.setOnClickListener(v -> {
            if (listener != null) listener.onCrewClick(m);
        });
    }

    private int parseColor(String colorName) {
        switch (colorName) {
            case "Blue":   return Color.parseColor("#1565C0");
            case "Yellow": return Color.parseColor("#F9A825");
            case "Green":  return Color.parseColor("#2E7D32");
            case "Purple": return Color.parseColor("#6A1B9A");
            case "Red":    return Color.parseColor("#C62828");
            default:       return Color.GRAY;
        }
    }

    @Override
    public int getItemCount() { return crew.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        View colorBar;
        TextView tvName, tvSpec, tvStats;
        CheckBox checkbox;

        ViewHolder(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            colorBar = v.findViewById(R.id.colorBar);
            tvName = v.findViewById(R.id.tvName);
            tvSpec = v.findViewById(R.id.tvSpec);
            tvStats = v.findViewById(R.id.tvStats);
            checkbox = v.findViewById(R.id.checkbox);
        }
    }
}
