package org.kosowskinowak.fuzzy.linguistic;

import java.util.List;
import java.util.Optional;

import org.kosowskinowak.fuzzy.set.Universe;

/**
 * Zmienna lingwistyczna: badana cecha obiektu (np. „Moc silnika") wraz z przestrzenią rozważań
 * i zestawem etykiet (zbiorów rozmytych) ją opisujących. Powiązana z konkretną kolumną bazy danych.
 */
public final class LinguisticVariable {

    private final String name;
    private final String column;
    private final Universe universe;
    private final List<Label> labels;

    public LinguisticVariable(String name, String column, Universe universe, List<Label> labels) {
        this.name = name;
        this.column = column;
        this.universe = universe;
        this.labels = List.copyOf(labels);
    }

    public Optional<Label> label(String labelName) {
        return labels.stream().filter(l -> l.name().equals(labelName)).findFirst();
    }

    /**
     * Czy surowa wartość atrybutu jest poprawna dla tej zmiennej. Wartości ≤ 0 dla cech, których
     * przestrzeń zaczyna się powyżej zera (np. masa, rozstaw osi), traktujemy jako brak danych.
     */
    public boolean isValid(double rawValue) {
        if (Double.isNaN(rawValue) || rawValue < 0.0) {
            return false;
        }
        return !(rawValue == 0.0 && universe.min() > 0.0);
    }

    /** Stopień przynależności wartości do etykiety, z przycięciem do przestrzeni (etykiety połówkowe). */
    public double degreeOf(Label label, double rawValue) {
        return label.degree(universe.clamp(rawValue));
    }

    public String name() {
        return name;
    }

    public String column() {
        return column;
    }

    public Universe universe() {
        return universe;
    }

    public List<Label> labels() {
        return labels;
    }
}
