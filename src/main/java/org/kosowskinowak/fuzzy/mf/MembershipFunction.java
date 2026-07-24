package org.kosowskinowak.fuzzy.mf;

/**
 * Funkcja przynależności: przelicza surową wartość {@code x} na stopień przynależności
 * z przedziału [0, 1]. Własny, minimalny interfejs nadrzędny nad implementacjami
 * z biblioteki jFuzzyLogic (pakiet hybrydowy).
 */
@FunctionalInterface
public interface MembershipFunction {

    /** @return stopień przynależności {@code x} do zbioru, zawsze w [0, 1]. */
    double degree(double x);
}
