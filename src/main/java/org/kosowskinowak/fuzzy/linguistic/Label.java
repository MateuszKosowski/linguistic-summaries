package org.kosowskinowak.fuzzy.linguistic;

import org.kosowskinowak.fuzzy.sets.FuzzySet;

public class Label {
    private final String name; // For example "Short"
    private final FuzzySet set; // For example trapezoidal with parameters (1500, 1500, 3500, 3900)

    public Label(String name, FuzzySet set) {
        this.name = name;
        this.set = set;
    }

    public double degreeOf(double x) {
        return set.calculateMembership(x);
    }

    public String getName() { return name; }
    public FuzzySet getSet() { return set; }
}
