package com.spacecolony.game.model;

// System-generated enemy
public class Threat {
    private final String name;
    private final String missionTitle;
    private final int skill;
    private final int resistance;
    private final int maxEnergy;
    private int energy;

    public Threat(String missionTitle, String name, int skill, int resistance, int energy) {
        this.missionTitle = missionTitle;
        this.name = name;
        this.skill = skill;
        this.resistance = resistance;
        this.maxEnergy = energy;
        this.energy = energy;
    }

    // Randomly generate only two fixed mission threats.
    public static Threat generate(int ignoredMissionCount) {
        if (Math.random() < 0.5) {
            // Asteroid Storm: skill 6, resilience 2, energy 25/25
            return new Threat("Asteroid Field Navigation", "Asteroid Storm", 6, 2, 25);
        }
        // Alien Probe: skill 10, resilience 4, energy 20/20
        return new Threat("Probe Interception", "Alien Probe", 10, 4, 20);
    }

    public void takeDamage(int damage) {
        int actual = Math.max(0, damage - resistance);
        energy -= actual;
        if (energy < 0) energy = 0;
    }

    public int attack() {
        return skill;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public String getMissionTitle() { return missionTitle; }
    public String getName() { return name; }
    public int getSkill() { return skill; }
    public int getResistance() { return resistance; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
}
