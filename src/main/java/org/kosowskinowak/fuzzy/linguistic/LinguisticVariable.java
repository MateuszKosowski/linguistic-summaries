package org.kosowskinowak.fuzzy.linguistic;

import org.kosowskinowak.fuzzy.universe.Universe;

import java.util.List;
import java.util.Optional;

public class LinguisticVariable {
    private final String name; // For example "Vehicle length"
    private final Universe universe;
    private final List<Label> labels; // For example "Short", "Medium", "Long"

    public LinguisticVariable(String name, Universe universe, List<Label> labels) {
        this.name = name;
        this.universe = universe;
        this.labels = List.copyOf(labels);
    }

    public Optional<Label> findLabel(String labelName) {
        return labels.stream()
                .filter(l -> l.getName().equals(labelName))
                .findFirst();
    }

    public List<Label> getLabels() { return labels; }
    public String getName() { return name; }
    public Universe getUniverse() { return universe; }
}
