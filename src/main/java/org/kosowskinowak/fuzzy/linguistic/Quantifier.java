package org.kosowskinowak.fuzzy.linguistic;

import org.kosowskinowak.fuzzy.set.FuzzySet;

/**
 * Kwantyfikator rozmyty odpowiadający na pytanie „ile?". Dwa rodzaje:
 * <ul>
 *   <li>{@link Type#RELATIVE względny} – określony na proporcji [0, 1] (np. „większość");</li>
 *   <li>{@link Type#ABSOLUTE bezwzględny} – określony na liczbie obiektów (np. „kilka tysięcy").</li>
 * </ul>
 */
public final class Quantifier {

    public enum Type {RELATIVE, ABSOLUTE}

    private final String name;
    private final Type type;
    private final FuzzySet set;

    public Quantifier(String name, Type type, FuzzySet set) {
        this.name = name;
        this.type = type;
        this.set = set;
    }

    /** Stopień, w jakim wartość kwantyfikatora (proporcja lub liczność) pasuje do tego słowa. */
    public double degree(double quantity) {
        return set.membership(quantity);
    }

    public boolean isRelative() {
        return type == Type.RELATIVE;
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }

    public FuzzySet set() {
        return set;
    }
}
