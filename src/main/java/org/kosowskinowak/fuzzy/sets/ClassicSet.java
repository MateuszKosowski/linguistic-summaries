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
        // Call the main constructor with a single interval
        this(name, universe, List.of(new Interval(start, end)));
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
                current = new Interval(current.start(), Math.max(current.end(), next.end()));
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return List.copyOf(merged);
    }

    public ClassicSet complement() {
        double uStart = universe.min();
        double uEnd = universe.max();
        List<Interval> result = new ArrayList<>();
        double cursor = uStart;
        for (Interval i : intervals) {
            if (cursor < i.start()) {
                result.add(new Interval(cursor, i.start()));
            }
            cursor = Math.max(cursor, i.end());
        }
        if (cursor < uEnd) {
            result.add(new Interval(cursor, uEnd));
        }
        return new ClassicSet("not " + name, universe, result);
    }

    public ClassicSet union(ClassicSet other) {
        List<Interval> combined = new ArrayList<>(intervals);
        combined.addAll(other.intervals);
        return new ClassicSet(name + " ∪ " + other.name, universe, combined);
    }

    public ClassicSet intersect(ClassicSet other) {
        List<Interval> result = new ArrayList<>();
        for (Interval ia : intervals) {
            for (Interval ib : other.intervals) {
                double start = Math.max(ia.start(), ib.start());
                double end = Math.min(ia.end(), ib.end());
                if (start <= end) {
                    result.add(new Interval(start, end));
                }
            }
        }
        return new ClassicSet(name + " ∩ " + other.name, universe, result);
    }

}
