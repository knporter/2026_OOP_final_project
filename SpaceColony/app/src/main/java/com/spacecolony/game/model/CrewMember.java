package com.spacecolony.game.model;

import java.util.ArrayList;
import java.util.List;

// Base class for all crew members — demonstrates inheritance & polymorphism
public abstract class CrewMember {
    private static int nextId = 1;

    private final int id;
    private final String name;
    private final String specialization;
    private int skill;
    private int resistance;
    private int maxEnergy;
    private int currentEnergy;
    private int experience;
    private final String color;
    private final ArrayList<Integer> historyScores;

    public CrewMember(String name, String specialization, int skill, int resistance, int maxEnergy, String color) {
        this.id = nextId++;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resistance = resistance;
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy; // energy starts at max
        this.experience = 0;            // experience starts at zero
        this.color = color;
        this.historyScores = new ArrayList<>();
        this.historyScores.add(0);
    }

    // Polymorphic method — each subclass can override for specialization bonus
    public int getAttackPower() {
        return skill;
    }

    public void gainExperience(int xp) {
        if (xp <= 0) return;
        experience += xp;
        historyScores.add(experience);
    }

    public boolean canTrain() {
        return experience >= 2;
    }

    public boolean consumeTrainingXp() {
        if (!canTrain()) return false;
        experience -= 2;
        historyScores.add(experience);
        return true;
    }

    // Train success randomly improves one stat by +1.
    public String trainRandomStat() {
        int roll = (int) (Math.random() * 3);
        if (roll == 0) {
            skill += 1;
            return "Skill";
        }
        if (roll == 1) {
            resistance += 1;
            return "Resilience";
        }
        maxEnergy += 1;
        currentEnergy += 1;
        return "Energy";
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(0, damage - resistance);
        currentEnergy -= actualDamage;
        if (currentEnergy < 0) currentEnergy = 0;
    }

    public void restoreEnergy() {
        currentEnergy = maxEnergy;
    }

    public boolean isDefeated() {
        return currentEnergy <= 0;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getSkill() { return skill; }
    public int getResistance() { return resistance; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getCurrentEnergy() { return currentEnergy; }
    public int getExperience() { return experience; }
    public String getColor() { return color; }
    public int getHistoricalScore() { return historyScores.get(historyScores.size() - 1); }
    public List<Integer> getHistoryScores() { return new ArrayList<>(historyScores); }

    public void setSkill(int value) { this.skill = Math.max(0, value); }
    public void setResistance(int value) { this.resistance = Math.max(0, value); }
    public void setMaxEnergy(int value) {
        this.maxEnergy = Math.max(1, value);
        if (currentEnergy > this.maxEnergy) currentEnergy = this.maxEnergy;
    }
    public void setExperience(int value) {
        this.experience = Math.max(0, value);
        historyScores.add(this.experience);
    }
    public void setCurrentEnergy(int e) { this.currentEnergy = Math.max(0, Math.min(maxEnergy, e)); }

    @Override
    public String toString() {
        return name + " (" + specialization + ") | Skill:" + skill
                + " Res:" + resistance + " Energy:" + currentEnergy + "/" + maxEnergy
                + " XP:" + experience;
    }
}
