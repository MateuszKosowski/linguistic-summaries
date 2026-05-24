package org.kosowskinowak.summary.multi;

import java.util.function.Predicate;

import org.kosowskinowak.data.CarRecord;

/**
 * Podmiot podsumowania wielopodmiotowego (P₁, P₂): nazwany predykat wyróżniający rozłączny
 * podzbiór rekordów tej samej tabeli (np. jedną markę aut). Podział na podmioty odbywa się po
 * atrybucie nominalnym — zgodnie z Niewiadomski, Superson 2014 (rozdz. 2, „splitting set").
 */
public final class Subject {

    private final String name;
    private final Predicate<CarRecord> predicate;

    public Subject(String name, Predicate<CarRecord> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    /** Podmiot wyznaczony przez markę pojazdu (kolumna {@code Make}). */
    public static Subject ofMake(String make) {
        return new Subject(make, record -> make.equals(record.make()));
    }

    /** Czy rekord należy do tego podmiotu (dᵢ ∈* P). */
    public boolean contains(CarRecord record) {
        return predicate.test(record);
    }

    public String name() {
        return name;
    }
}
