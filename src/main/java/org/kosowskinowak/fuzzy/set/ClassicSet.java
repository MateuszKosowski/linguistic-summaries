package org.kosowskinowak.fuzzy.set;

import java.util.function.DoublePredicate;

/**
 * Zbiór klasyczny (crisp): twardy warunek przynależności (0 lub 1) na danej przestrzeni rozważań.
 * Udostępnia dopełnienie, sumę i iloczyn jako operacje logiczne. Stanowi punkt odniesienia dla
 * zbiorów rozmytych (wymaganie pkt 2.1–2.2 polecenia).
 */
public final class ClassicSet {

    private final String name;
    private final Universe universe;
    private final DoublePredicate predicate;

    public ClassicSet(String name, Universe universe, DoublePredicate predicate) {
        this.name = name;
        this.universe = universe;
        this.predicate = predicate;
    }

    /** @return 1.0, gdy x spełnia warunek, w przeciwnym razie 0.0. */
    public double membership(double x) {
        return predicate.test(x) ? 1.0 : 0.0;
    }

    public boolean contains(double x) {
        return predicate.test(x);
    }

    public ClassicSet complement() {
        return new ClassicSet("nie " + name, universe, x -> !predicate.test(x));
    }

    public ClassicSet union(ClassicSet other) {
        return new ClassicSet(name + " ∪ " + other.name, universe,
                x -> predicate.test(x) || other.predicate.test(x));
    }

    public ClassicSet intersect(ClassicSet other) {
        return new ClassicSet(name + " ∩ " + other.name, universe,
                x -> predicate.test(x) && other.predicate.test(x));
    }

    public String name() {
        return name;
    }

    public Universe universe() {
        return universe;
    }
}
