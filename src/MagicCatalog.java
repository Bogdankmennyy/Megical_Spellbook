import java.io.*;
import java.util.*;

// Enum for SpellType
enum SpellType {
    FIRE(50, "Risky"),
    ICE(40, "Safe"),
    HEALING(30, "Safe"),
    NECROMANCY(80, "Forbidden"),
    ILLUSION(20, "Safe");

    private final int manaCost;
    private final String dangerLevel;

    SpellType(int manaCost, String dangerLevel) {
        this.manaCost = manaCost;
        this.dangerLevel = dangerLevel;
    }

    public int getManaCost() {
        return manaCost;
    }

    public String getDangerLevel() {
        return dangerLevel;
    }
}

// Spell class implementing Comparable
class Spell implements Comparable<Spell>, Serializable {
    private final String name;
    private final SpellType type;
    private final int powerLevel;

    public Spell(String name, SpellType type, int powerLevel) {
        this.name = name;
        this.type = type;
        this.powerLevel = powerLevel;
    }

    public String getName() { return name; }
    public SpellType getType() { return type; }
    public int getPowerLevel() { return powerLevel; }

    @Override
    public int compareTo(Spell other) {
        return Integer.compare(other.powerLevel, this.powerLevel);
    }

    @Override
    public String toString() {
        return name + " (" + type + ", Power: " + powerLevel + ")";
    }
}

// Custom Comparator for sorting spells by danger level then name
class SpellComparator implements Comparator<Spell> {
    @Override
    public int compare(Spell s1, Spell s2) {
        int dangerComparison = s1.getType().getDangerLevel().compareTo(s2.getType().getDangerLevel());
        return (dangerComparison != 0) ? dangerComparison : s1.getName().compareTo(s2.getName());
    }
}

// Spellbook Collection
class Spellbook implements Serializable {
    private final Map<SpellType, List<Spell>> spellsByType = new HashMap<>();

    public void addSpell(Spell spell) {
        spellsByType.computeIfAbsent(spell.getType(), k -> new ArrayList<>()).add(spell);
    }

    public List<Spell> getSpellsByType(SpellType type) {
        return spellsByType.getOrDefault(type, Collections.emptyList());
    }

    public Spell getMostPowerfulSpell() {
        return spellsByType.values().stream()
                .flatMap(List::stream)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    public double getAverageManaCostForDangerLevel(String dangerLevel) {
        return spellsByType.values().stream()
                .flatMap(List::stream)
                .filter(spell -> spell.getType().getDangerLevel().equals(dangerLevel))
                .mapToInt(spell -> spell.getType().getManaCost())
                .average()
                .orElse(0);
    }

    public List<Spell> getTopSpells(int n) {
        return spellsByType.values().stream()
                .flatMap(List::stream)
                .sorted()
                .limit(n)
                .toList();
    }

    public void displaySpellsByType() {
        System.out.println("=== Spells by Type ===");
        spellsByType.forEach((type, spells) ->
                System.out.println(type + ": " + spells));
    }

    public void spellDuel(Spell s1, Spell s2) {
        System.out.println("Duel: " + s1.getName() + " vs " + s2.getName());
        if ((s1.getType() == SpellType.FIRE && s2.getType() == SpellType.ICE) ||
                (s1.getType() == SpellType.ICE && s2.getType() == SpellType.FIRE)) {
            System.out.println((s1.getType() == SpellType.FIRE ? s1.getName() : s2.getName()) + " wins! FIRE beats ICE.");
        } else {
            System.out.println((s1.getPowerLevel() > s2.getPowerLevel() ? s1.getName() : s2.getName()) + " wins!");
        }
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static Spellbook loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Spellbook) in.readObject();
        }
    }
}

// Main class for demonstration
public class MagicCatalog {
    public static void main(String[] args) {
        Spellbook spellbook = new Spellbook();

        spellbook.addSpell(new Spell("Fireball", SpellType.FIRE, 80));
        spellbook.addSpell(new Spell("Inferno", SpellType.FIRE, 95));
        spellbook.addSpell(new Spell("Blizzard", SpellType.ICE, 90));
        spellbook.addSpell(new Spell("Cure Wounds", SpellType.HEALING, 30));
        spellbook.addSpell(new Spell("Resurrection", SpellType.NECROMANCY, 85));
        spellbook.addSpell(new Spell("Apocalypse", SpellType.NECROMANCY, 100));
        spellbook.addSpell(new Spell("Shadow Veil", SpellType.ILLUSION, 40));

        spellbook.displaySpellsByType();

        System.out.println("=== Top 3 Spells ===");
        spellbook.getTopSpells(3).forEach(System.out::println);

        System.out.println("=== Forbidden Spells Avg Mana Cost ===");
        System.out.println(spellbook.getAverageManaCostForDangerLevel("Forbidden"));

        System.out.println("=== Spell Duel ===");
        spellbook.spellDuel(new Spell("Fireball", SpellType.FIRE, 80), new Spell("Blizzard", SpellType.ICE, 90));
    }
}
