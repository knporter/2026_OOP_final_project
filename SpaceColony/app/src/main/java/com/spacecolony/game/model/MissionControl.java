package com.spacecolony.game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages cooperative mission execution.
 * Turn order (per spec): A acts -> threat retaliates A -> B acts -> threat retaliates B.
 * A defeated member is removed immediately and the other continues alone.
 */
public class MissionControl {
    private static int missionCounter = 0;

    private final CrewMember memberA;
    private final CrewMember memberB;
    private final Threat threat;
    private final List<String> log = new ArrayList<>();
    private boolean missionComplete = false;
    private boolean missionSuccess = false;
    private int roundCounter = 0;

    public MissionControl(CrewMember a, CrewMember b) {
        this.memberA = a;
        this.memberB = b;
        this.threat = Threat.generate(missionCounter);
        log.add("=== MISSION: " + threat.getMissionTitle() + " ===");
        log.add("Threat: " + threat.getName() + " (skill: " + threat.getSkill()
                + ", resilience: " + threat.getResistance()
                + ", energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy() + ")");
        log.add("");
        log.add("Crew Member A: " + formatCrew(memberA));
        log.add("Crew Member B: " + formatCrew(memberB));
        log.add("---");
    }

    /**
     * Execute one full round.
     * Correct order: A attacks -> threat retaliates A -> (if threat alive) B attacks -> threat retaliates B.
     * Returns true when the mission is over.
     */
    public boolean executeRound() {
        if (missionComplete) return true;
        roundCounter++;

        log.add("");
        log.add("--- Round " + roundCounter + " ---");

        // --- Member A acts (if still alive) ---
        if (!memberA.isDefeated()) {
            crewAction(memberA);

            if (threat.isDefeated()) { finishSuccess(); return true; }

            // Threat retaliates against A immediately after A's action
            int atk = threat.attack();
            int reduced = Math.max(0, atk - memberA.getResistance());
            memberA.takeDamage(atk);
            log.add(threat.getName() + " retaliates against " + formatActor(memberA));
            log.add("Damage dealt: " + atk + " - " + memberA.getResistance() + " = " + reduced);
            log.add(formatActor(memberA) + " energy: " + memberA.getCurrentEnergy() + "/" + memberA.getMaxEnergy());

            if (memberA.isDefeated()) {
                log.add(memberA.getName() + " is down!");
            }
        }

        // Check if both are now defeated (A just went down, B was already down)
        if (memberA.isDefeated() && memberB.isDefeated()) { finishFail(); return true; }

        // --- Member B acts (if still alive) ---
        if (!memberB.isDefeated()) {
            log.add("");
            crewAction(memberB);

            if (threat.isDefeated()) { finishSuccess(); return true; }

            // Threat retaliates against B immediately after B's action
            int atk = threat.attack();
            int reduced = Math.max(0, atk - memberB.getResistance());
            memberB.takeDamage(atk);
            log.add(threat.getName() + " retaliates against " + formatActor(memberB));
            log.add("Damage dealt: " + atk + " - " + memberB.getResistance() + " = " + reduced);
            log.add(formatActor(memberB) + " energy: " + memberB.getCurrentEnergy() + "/" + memberB.getMaxEnergy());

            if (memberB.isDefeated()) {
                log.add(memberB.getName() + " is down!");
            }
        }

        // Both defeated?
        if (memberA.isDefeated() && memberB.isDefeated()) { finishFail(); return true; }

        return false;
    }

    private void crewAction(CrewMember member) {
        boolean critical = Math.random() < 0.25;
        int baseDamage = member.getAttackPower();
        int attackDamage = critical ? baseDamage * 2 : baseDamage;
        int finalDamage = Math.max(0, attackDamage - threat.getResistance());

        log.add(formatActor(member) + " acts against " + threat.getName());
        if (critical) {
            log.add("CRITICAL HIT! " + formatActor(member) + " damage x2");
        }
        log.add("Damage dealt: " + attackDamage + " - " + threat.getResistance() + " = " + finalDamage);
        threat.takeDamage(attackDamage);
        log.add(threat.getName() + " energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy());
    }

    private String formatCrew(CrewMember member) {
        return formatActor(member)
                + " skill: " + member.getSkill()
                + "; res: " + member.getResistance()
                + "; exp: " + member.getExperience()
                + "; energy: " + member.getCurrentEnergy() + "/" + member.getMaxEnergy();
    }

    private String formatActor(CrewMember member) {
        return member.getSpecialization() + "(" + member.getName() + ")";
    }

    private void finishSuccess() {
        missionComplete = true;
        missionSuccess = true;
        missionCounter++;
        Storage store = Storage.getInstance();
        store.incrementMissionCount();
        store.addWin();

        if (!memberA.isDefeated()) {
            memberA.gainExperience(2);
            log.add(memberA.getName() + " gains 2 XP -> total XP=" + memberA.getExperience());
        }
        if (!memberB.isDefeated()) {
            memberB.gainExperience(2);
            log.add(memberB.getName() + " gains 2 XP -> total XP=" + memberB.getExperience());
        }

        handleDefeated();
        log.add("=== MISSION COMPLETE: " + threat.getName() + " neutralized! ===");
    }

    private void finishFail() {
        missionComplete = true;
        missionSuccess = false;
        missionCounter++;
        Storage.getInstance().incrementMissionCount();
        handleDefeated();
        log.add("=== MISSION FAILED: All crew members lost. ===");
    }

    private void handleDefeated() {
        Storage store = Storage.getInstance();
        if (memberA.isDefeated()) {
            store.removeCrew(memberA.getId());
            log.add(memberA.getName() + " removed from roster.");
        }
        if (memberB.isDefeated()) {
            store.removeCrew(memberB.getId());
            log.add(memberB.getName() + " removed from roster.");
        }
    }

    public List<String> getLog() { return log; }
    public boolean isMissionComplete() { return missionComplete; }
    public boolean isMissionSuccess() { return missionSuccess; }
    public CrewMember getMemberA() { return memberA; }
    public CrewMember getMemberB() { return memberB; }
    public Threat getThreat() { return threat; }
}
