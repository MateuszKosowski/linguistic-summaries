package org.kosowskinowak.fuzzy.linguistic;

import org.kosowskinowak.fuzzy.sets.FuzzySet;

public final class Quantifier {
    private final String name; // For example: "Most of..."
    private final Type type;
    private final FuzzySet set;

    public Quantifier(String name, Type type, FuzzySet set) {
        this.name = name;
        this.type = type;
        this.set = set;
    }

    public double degree(double quantity) {
        return set.calculateMembership(quantity);
    }

    public boolean isRelative() { return type == Type.RELATIVE; }

    public String getName() { return name; }
    public Type getType()   { return type; }
    public FuzzySet getSet() { return set; }

}
