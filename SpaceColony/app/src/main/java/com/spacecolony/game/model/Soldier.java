package com.spacecolony.game.model;

public class Soldier extends CrewMember {
    public Soldier(String name) {
        super(name, "Soldier", 9, 0, 16, "Red");
    }

    @Override
    public int getAttackPower() {
        return getSkill() + 4; // Combat specialist
    }
}
