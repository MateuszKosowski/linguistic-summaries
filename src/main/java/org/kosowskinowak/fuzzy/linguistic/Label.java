package org.kosowskinowak.fuzzy.linguistic;

import org.kosowskinowak.fuzzy.set.FuzzySet;

/**
 * Etykieta zmiennej lingwistycznej (np. „dynamiczny" dla mocy silnika): słowo języka naturalnego
 * związane z jednym zbiorem rozmytym i jego funkcją przynależności.
 */
public record Label(String name, FuzzySet set) {

    /** Stopień, w jakim wartość {@code x} pasuje do tej etykiety. */
    public double degree(double x) {
        return set.membership(x);
    }
}
