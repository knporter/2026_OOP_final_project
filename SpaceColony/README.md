# Space Colony — OOP Android Project

## How to Run

1. Open Android Studio (Hedgehog 2023.1.1 or newer)
2. File -> Open -> select the `SpaceColony` folder
3. Wait for Gradle sync to complete
4. Run on an emulator or physical device (minSdk 24 / Android 7.0)

---

## Team Composition

- Student: [Your Name]
- Student ID: [Your ID]
- Group size: 1

---

## Division of Work

All design, implementation, and testing was done by the single team member.

---

## Tools Used

- Android Studio Hedgehog 2023.1.1
- Java 8
- Gradle 8.2
- Material Components for Android 1.11.0
- Git / GitHub

---

## AI Usage Disclaimer

No AI tools were used in any part of this project.
All code, design decisions, and documentation were written entirely by the student.

---

## GitHub Repository

[Add your public GitHub URL here]

## Video

[Add your demo video URL here]

---

## Project Description

Space Colony is an Android game where the player manages a crew of space colonists.
The player recruits crew members with different specializations, trains them in a Simulator,
and sends them on cooperative turn-based missions against system-generated threats.

---

## Requirements Traceability
### Where each PDF requirement is implemented in the code

---

### 1. Object-Oriented Programming Principles (PDF p.7, req f/g/h)

**f. Coded according to OOP paradigm**
- All game logic is in dedicated model classes, not in Activities.
- `model/CrewMember.java` — abstract base class
- `model/Storage.java` — singleton data manager
- `model/MissionControl.java` — mission logic class
- `model/Threat.java` — enemy class

**g. Appropriate classes: CrewMember, Storage, MissionControl, Threat**
- `model/CrewMember.java` — CrewMember base class
- `model/Storage.java` — Storage class with HashMap<Integer, CrewMember>
- `model/MissionControl.java` — MissionControl class
- `model/Threat.java` — Threat class

**h. Encapsulation, inheritance, polymorphism**
- Encapsulation: all fields in `CrewMember.java` are private; accessed via getters/setters
- Inheritance: `Pilot.java`, `Engineer.java`, `Medic.java`, `Scientist.java`, `Soldier.java` all extend `CrewMember`
- Polymorphism: each subclass overrides `getAttackPower()` in `CrewMember.java`

---

### 2. Code Language (PDF p.7, req i)

**i. All code, comments, and documentation in English**
- All `.java` files use English identifiers and comments
- All XML layout strings are in English
- This README and all documentation are in English

---

### 3. Android App Development (PDF p.7, req j/k)

**j. Must run on Android devices**
- `app/build.gradle` — minSdk 24, targetSdk 34
- `AndroidManifest.xml` — declares all 6 Activities

**k. Developed using Java in Android Studio**
- All source files under `app/src/main/java/` are `.java`
- `app/build.gradle` — `compileOptions { sourceCompatibility JavaVersion.VERSION_1_8 }`

---

### 4. Basic Functionality (PDF p.7, req l–o)

**l-i. Create different types of crew members (Pilot, Engineer, Medic, Scientist, Soldier)**
- `ui/RecruitActivity.java` lines 63–81 — factory method creates the correct subclass
- `model/Pilot.java`, `model/Engineer.java`, `model/Medic.java`, `model/Scientist.java`, `model/Soldier.java`

**l-ii. Newly recruited crew members placed in Quarters**
- `model/Storage.java` line 32 — `quartersIds.add(member.getId())` inside `addCrew()`

**l-iii. Users can move crew to Simulator or Mission Control**
- `ui/QuartersActivity.java` lines 37–50 — "Move to Simulator" and "Move to Mission Control" buttons
- `model/Storage.java` lines 55–70 — `moveToSimulator()` and `moveToMissionControl()`

**m-iv. Crew gain XP when trained in Simulator**
- `ui/SimulatorActivity.java` lines 42–59 — "Train Selected" button calls `gainExperience(2)`
- `model/CrewMember.java` lines 34–42 — `gainExperience()` method

**m-v. XP increases skill power (XP=2 -> skill +2)**
- `model/CrewMember.java` lines 34–42 — `gainExperience(xp)`: when xp==2, `skill += 2`

**n-vi. Select two crew members for a mission**
- `ui/MissionControlActivity.java` lines 52–69 — checks `ids.size() != 2` before launching

**n-vii. Threat generated with stats based on scaling formula**
- `model/Threat.java` lines 17–25 — `generate(missionCount)`: skill = 4 + missionCount (matches PDF p.2)
- `model/MissionControl.java` line 17 — calls `Threat.generate(missionCounter)`

**n-viii. Turn-based mission system**
- `model/MissionControl.java` lines 35–80 — `executeRound()` method
- Order: A attacks -> threat retaliates A -> B attacks -> threat retaliates B (matches PDF p.2 algorithm)
- Threat retaliation happens immediately after each crew action (not batched)
- Defeated member exits immediately; surviving member continues alone

**Surviving crew gain XP**
- `model/MissionControl.java` lines 85–92 — `finishSuccess()` calls `gainExperience(1)` for each survivor

**Defeated crew removed from program**
- `model/MissionControl.java` lines 100–110 — `handleDefeated()` calls `Storage.removeCrew()`

**o-ix. Crew Recovery: energy restored when returning to Quarters, XP retained**
- `model/Storage.java` lines 73–80 — `moveToQuarters()` calls `member.restoreEnergy()` (energy = maxEnergy)
- `model/CrewMember.java` lines 44–46 — `restoreEnergy()` only resets `currentEnergy`, not `experience`
- `ui/MissionControlActivity.java` lines 82–95 — "Return Home" button calls `store.moveToQuarters()`
- `ui/SimulatorActivity.java` lines 61–70 — "Send to Quarters" button also triggers recovery

---

### 5. Data Structures (PDF p.8, req p/q/r)

**p. Program uses data structures effectively**
- `model/Storage.java` — central data store using both HashMap and ArrayList

**q. HashMap<Integer, CrewMember> for storing crew members and their IDs**
- `model/Storage.java` line 11 — `private final HashMap<Integer, CrewMember> allCrew = new HashMap<>()`

**r. ArrayList for managing lists of crew members, especially with RecyclerView**
- `model/Storage.java` lines 14–16 — `quartersIds`, `simulatorIds`, `missionControlIds` are all `ArrayList<Integer>`
- `adapter/CrewAdapter.java` — RecyclerView adapter takes `List<CrewMember>` (backed by ArrayList)

---

## Bonus Features (PDF p.9–10)

| Bonus | Points | Where implemented |
|---|---|---|
| RecyclerView | +1 | `adapter/CrewAdapter.java`; used in QuartersActivity, SimulatorActivity, MissionControlActivity, StatisticsActivity |
| Statistics | +1 | `ui/StatisticsActivity.java` + `res/layout/activity_statistics.xml` — shows per-colony stats and all crew |

---

## UML Class Diagram

```
CrewMember (abstract)
  - id: int
  - name: String
  - specialization: String
  - skill: int
  - resistance: int
  - maxEnergy: int
  - currentEnergy: int
  - experience: int
  - color: String
  + getAttackPower(): int        [overridden in each subclass]
  + gainExperience(xp: int)
  + takeDamage(damage: int)
  + restoreEnergy()
  + isDefeated(): boolean
       |
       +-- Pilot        (skill=5, res=4, energy=20, color=Blue)
       +-- Engineer     (skill=6, res=3, energy=19, color=Yellow)
       +-- Medic        (skill=7, res=2, energy=18, color=Green)
       +-- Scientist    (skill=8, res=1, energy=17, color=Purple)
       +-- Soldier      (skill=9, res=0, energy=16, color=Red)

Storage (Singleton)
  - allCrew: HashMap<Integer, CrewMember>
  - quartersIds: ArrayList<Integer>
  - simulatorIds: ArrayList<Integer>
  - missionControlIds: ArrayList<Integer>
  + addCrew(member)              -> places in Quarters
  + removeCrew(id)
  + moveToSimulator(id)
  + moveToMissionControl(id)
  + moveToQuarters(id)           -> also calls restoreEnergy()

Threat
  + name: String
  + skill: int
  + resistance: int
  + energy: int
  + generate(missionCount): Threat   [static, skill = 4 + missionCount]
  + takeDamage(damage: int)
  + attack(): int
  + isDefeated(): boolean

MissionControl
  - memberA: CrewMember
  - memberB: CrewMember
  - threat: Threat
  - log: List<String>
  + executeRound(): boolean      [turn order: A->threat->B->threat]
  + isMissionComplete(): boolean
  + isMissionSuccess(): boolean
```

---

## Application Use-Flow

```
Home Screen
  |
  +-- Recruit
  |     Enter name + pick specialization -> crew added to Quarters
  |
  +-- Quarters
  |     Select crew -> Move to Simulator | Move to Mission Control
  |
  +-- Simulator
  |     Select crew -> Train (XP+2, Skill+2) | Send to Quarters (energy restored)
  |
  +-- Mission Control
  |     Select 2 crew -> Launch Mission
  |       Next Round:
  |         A attacks -> threat retaliates A
  |         B attacks -> threat retaliates B
  |         (defeated member exits immediately)
  |       Mission Complete: survivors gain XP
  |       Mission Failed: all crew removed
  |       Return Home: survivors sent to Quarters (energy restored, XP kept)
  |
  +-- Statistics
        Colony overview + all crew stats
```
