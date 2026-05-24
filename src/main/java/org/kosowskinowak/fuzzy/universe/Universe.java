package org.kosowskinowak.fuzzy.universe;

public interface Universe {
    double min();
    double max();
    boolean contains(double x);
}
