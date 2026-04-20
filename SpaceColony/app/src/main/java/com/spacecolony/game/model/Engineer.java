package com.spacecolony.game.model;

public class Engineer extends CrewMember {
    public Engineer(String name) {
        super(name, "Engineer", 6, 3, 19, "Yellow");
    }

    @Override
    public int getAttackPower() {
        // Engineer bonus: repair mission +2 skill bonus
        return getSkill() + 2;
    }
}
