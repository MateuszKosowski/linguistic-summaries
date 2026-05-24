package org.kosowskinowak.fuzzy.sets;

import org.kosowskinowak.fuzzy.universe.Universe;

public class ClassicSet {
    private final String name;
    private final Universe universe;
    private final double min;
    private final double max;

    public ClassicSet(String name, Universe universe, double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }

        if (!universe.contains(min) || !universe.contains(max)) {
            throw new IllegalArgumentException("min or max of Classic Set is out of universe bounds");
        }

        this.name = name;
        this.universe = universe;
        this.min = min;
        this.max = max;
    }

    public boolean contains(double x) {
        return x >= min && x <= max;
    }

    public String getName() {
        return name;
    }
}
