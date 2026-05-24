package org.kosowskinowak.fuzzy.sets;

import org.kosowskinowak.fuzzy.universe.Universe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClassicSet {
    private final String name;
    private final Universe universe;
    private final List<Interval> intervals;


    public ClassicSet(String name, Universe universe, List<Interval> intervals) {
        this.name = name;
        this.universe = universe;
        this.intervals = normalize(intervals);
    }

    public ClassicSet(String name, Universe universe, double start, double end) {
        this.name = name;
        this.universe = universe;
        this.intervals = List.of(new Interval(start, end));
    }

    public boolean contains(double x) {
        for (Interval i : intervals) {
            if (i.contains(x)) return true;
        }
        return false;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public String getName() {
        return name;
    }

    public Universe universe() {
        return universe;
    }

    // Sort by start, merge
    private static List<Interval> normalize(List<Interval> raw) {
        if (raw.isEmpty()) return List.of();
        List<Interval> sorted = new ArrayList<>(raw);
        sorted.sort(Comparator.comparingDouble(Interval::start));

        List<Interval> merged = new ArrayList<>();
        Interval current = sorted.get(0);
        for (int i = 1; i < sorted.size(); i++) {
            Interval next = sorted.get(i);
            if (next.start() <= current.end()) {
                current = new Interval(current.start(), Math.max(current.end(), next.start()));
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return List.copyOf(merged);
    }
}
