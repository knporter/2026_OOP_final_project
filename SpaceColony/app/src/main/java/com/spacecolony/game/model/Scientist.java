package com.spacecolony.game.model;

public class Scientist extends CrewMember {
    public Scientist(String name) {
        super(name, "Scientist", 8, 1, 17, "Purple");
    }

    @Override
    public int getAttackPower() {
        return getSkill() + 3; // Science bonus
    }
}
