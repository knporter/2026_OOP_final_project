package com.spacecolony.game.ui;

import android.os.Bundle;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.spacecolony.game.R;
import com.spacecolony.game.model.*;

// Recruit Crew Member Screen
public class RecruitActivity extends AppCompatActivity {

    private EditText etName;
    private RadioGroup rgSpec;
    private TextView tvPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        etName = findViewById(R.id.etName);
        rgSpec = findViewById(R.id.rgSpec);
        tvPreview = findViewById(R.id.tvPreview);
        applyRoleVisuals();

        rgSpec.setOnCheckedChangeListener((group, id) -> updatePreview());

        findViewById(R.id.btnCreate).setOnClickListener(v -> recruit());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void updatePreview() {
        int checked = rgSpec.getCheckedRadioButtonId();
        String spec = getSpecFromRadio(checked);
        CrewMember preview = buildMember("Preview", spec);
        if (preview != null) {
            tvPreview.setText("Skill: " + preview.getSkill()
                    + "  Resistance: " + preview.getResistance()
                    + "  Max Energy: " + preview.getMaxEnergy()
                    + "  Color: " + preview.getColor());
        }
    }

    private void recruit() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        int checked = rgSpec.getCheckedRadioButtonId();
        if (checked == -1) {
            Toast.makeText(this, "Select a specialization", Toast.LENGTH_SHORT).show();
            return;
        }
        String spec = getSpecFromRadio(checked);
        CrewMember member = buildMember(name, spec);
        if (member != null) {
            Storage.getInstance().addCrew(member);
            Toast.makeText(this, name + " recruited and placed in Quarters!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getSpecFromRadio(int radioId) {
        if (radioId == R.id.rbPilot) return "Pilot";
        if (radioId == R.id.rbEngineer) return "Engineer";
        if (radioId == R.id.rbMedic) return "Medic";
        if (radioId == R.id.rbScientist) return "Scientist";
        if (radioId == R.id.rbSoldier) return "Soldier";
        return "Pilot";
    }

    // Add a role-colored smiley for quick visual distinction.
    private void applyRoleVisuals() {
        setRoleSmiley(R.id.rbPilot, "#1565C0");
        setRoleSmiley(R.id.rbEngineer, "#F9A825");
        setRoleSmiley(R.id.rbMedic, "#2E7D32");
        setRoleSmiley(R.id.rbScientist, "#6A1B9A");
        setRoleSmiley(R.id.rbSoldier, "#C62828");
    }

    private void setRoleSmiley(int radioId, String colorHex) {
        RadioButton button = findViewById(radioId);
        if (button == null) return;

        String baseText = button.getText().toString().replace("  ☺", "").replace("  ☻", "");
        String roleText = baseText + "  ☻";
        SpannableString styled = new SpannableString(roleText);
        int smileyIndex = roleText.length() - 1;
        styled.setSpan(
                new ForegroundColorSpan(Color.parseColor(colorHex)),
                smileyIndex,
                roleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        styled.setSpan(
                new RelativeSizeSpan(1.6f),
                smileyIndex,
                roleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        button.setText(styled);
    }

    // Factory method — demonstrates polymorphism
    private CrewMember buildMember(String name, String spec) {
        switch (spec) {
            case "Pilot":     return new Pilot(name);
            case "Engineer":  return new Engineer(name);
            case "Medic":     return new Medic(name);
            case "Scientist": return new Scientist(name);
            case "Soldier":   return new Soldier(name);
            default:          return null;
        }
    }
}
