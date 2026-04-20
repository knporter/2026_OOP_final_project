package com.spacecolony.game.model;

public class Pilot extends CrewMember {
    public Pilot(String name) {
        super(name, "Pilot", 5, 4, 20, "Blue");
    }

    @Override
    public int getAttackPower() {
        // Pilot specialization bonus: +1 on attack
        return getSkill() + 1;
    }
}
