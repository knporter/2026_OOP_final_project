package com.spacecolony.game.model;

public class Medic extends CrewMember {
    public Medic(String name) {
        super(name, "Medic", 7, 2, 18, "Green");
    }

    @Override
    public int getAttackPower() {
        return getSkill();
    }
}
