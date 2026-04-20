package com.spacecolony.game.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

// Singleton storage — uses HashMap<Integer, CrewMember> as required
public class Storage {
    private static Storage instance;

    // Requirement: HashMap<Integer, CrewMember> for storing crew members and their IDs
    private final HashMap<Integer, CrewMember> allCrew = new HashMap<>();

    // Requirement: ArrayList for managing lists of crew members (used with RecyclerView)
    private final ArrayList<Integer> quartersIds = new ArrayList<>();
    private final ArrayList<Integer> simulatorIds = new ArrayList<>();
    private final ArrayList<Integer> missionControlIds = new ArrayList<>();

    private int missionCount = 0;
    private int totalMissions = 0;
    private int wins = 0;

    private Storage() {}

    public static Storage getInstance() {
        if (instance == null) instance = new Storage();
        return instance;
    }

    public void addCrew(CrewMember member) {
        allCrew.put(member.getId(), member);
        quartersIds.add(member.getId()); // newly recruited → placed in Quarters
    }

    public CrewMember getCrew(int id) {
        return allCrew.get(id);
    }

    public List<CrewMember> getQuartersCrew() {
        List<CrewMember> list = new ArrayList<>();
        for (int id : quartersIds) list.add(allCrew.get(id));
        return list;
    }

    public List<CrewMember> getSimulatorCrew() {
        List<CrewMember> list = new ArrayList<>();
        for (int id : simulatorIds) list.add(allCrew.get(id));
        return list;
    }

    public List<CrewMember> getMissionControlCrew() {
        List<CrewMember> list = new ArrayList<>();
        for (int id : missionControlIds) list.add(allCrew.get(id));
        return list;
    }

    public List<CrewMember> getAllCrew() {
        return new ArrayList<>(allCrew.values());
    }

    public void moveToSimulator(int id) {
        quartersIds.remove(Integer.valueOf(id));
        missionControlIds.remove(Integer.valueOf(id));
        if (!simulatorIds.contains(id)) simulatorIds.add(id);
    }

    public void moveToMissionControl(int id) {
        quartersIds.remove(Integer.valueOf(id));
        simulatorIds.remove(Integer.valueOf(id));
        if (!missionControlIds.contains(id)) missionControlIds.add(id);
    }

    public void moveToQuarters(int id) {
        simulatorIds.remove(Integer.valueOf(id));
        missionControlIds.remove(Integer.valueOf(id));
        if (!quartersIds.contains(id)) quartersIds.add(id);
        // Crew Recovery: energy fully restored when returning to Quarters
        CrewMember m = allCrew.get(id);
        if (m != null) m.restoreEnergy();
    }

    public void removeCrew(int id) {
        allCrew.remove(id);
        quartersIds.remove(Integer.valueOf(id));
        simulatorIds.remove(Integer.valueOf(id));
        missionControlIds.remove(Integer.valueOf(id));
    }

    public int getMissionCount() { return missionCount; }
    public void incrementMissionCount() { missionCount++; totalMissions++; }
    public int getTotalMissions() { return totalMissions; }
    public int getWins() { return wins; }
    public void addWin() { wins++; }
    public int getTotalCrew() { return allCrew.size(); }

    public void clearAllCrewState() {
        allCrew.clear();
        quartersIds.clear();
        simulatorIds.clear();
        missionControlIds.clear();
    }

    public String exportSnapshot() {
        try {
            JSONObject root = new JSONObject();
            root.put("missionCount", missionCount);
            root.put("totalMissions", totalMissions);
            root.put("wins", wins);

            JSONArray crewArray = new JSONArray();
            List<CrewMember> sorted = new ArrayList<>(allCrew.values());
            sorted.sort(Comparator.comparingInt(CrewMember::getId));
            for (CrewMember m : sorted) {
                JSONObject item = new JSONObject();
                item.put("name", m.getName());
                item.put("specialization", m.getSpecialization());
                item.put("skill", m.getSkill());
                item.put("resistance", m.getResistance());
                item.put("maxEnergy", m.getMaxEnergy());
                item.put("currentEnergy", m.getCurrentEnergy());
                item.put("experience", m.getExperience());
                item.put("color", m.getColor());
                item.put("zone", getZoneForId(m.getId()));
                crewArray.put(item);
            }
            root.put("crew", crewArray);
            return root.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean importSnapshot(String json) {
        try {
            JSONObject root = new JSONObject(json);
            clearAllCrewState();

            missionCount = root.optInt("missionCount", 0);
            totalMissions = root.optInt("totalMissions", 0);
            wins = root.optInt("wins", 0);

            JSONArray crewArray = root.optJSONArray("crew");
            if (crewArray == null) return true;

            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject item = crewArray.getJSONObject(i);
                CrewMember m = buildBySpec(
                        item.optString("specialization", "Pilot"),
                        item.optString("name", "Crew")
                );
                if (m == null) continue;
                m.setSkill(item.optInt("skill", m.getSkill()));
                m.setResistance(item.optInt("resistance", m.getResistance()));
                m.setMaxEnergy(item.optInt("maxEnergy", m.getMaxEnergy()));
                m.setCurrentEnergy(item.optInt("currentEnergy", m.getCurrentEnergy()));
                m.setExperience(item.optInt("experience", 0));

                allCrew.put(m.getId(), m);
                String zone = item.optString("zone", "quarters");
                if ("simulator".equals(zone)) simulatorIds.add(m.getId());
                else if ("mission".equals(zone)) missionControlIds.add(m.getId());
                else quartersIds.add(m.getId());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getZoneForId(int id) {
        if (simulatorIds.contains(id)) return "simulator";
        if (missionControlIds.contains(id)) return "mission";
        return "quarters";
    }

    private CrewMember buildBySpec(String spec, String name) {
        switch (spec) {
            case "Pilot": return new Pilot(name);
            case "Engineer": return new Engineer(name);
            case "Medic": return new Medic(name);
            case "Scientist": return new Scientist(name);
            case "Soldier": return new Soldier(name);
            default: return new Pilot(name);
        }
    }
}
