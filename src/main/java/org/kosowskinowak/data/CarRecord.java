package org.kosowskinowak.data;

import java.util.Map;

/**
 * Pojedynczy rekord bazy: identyfikacja pojazdu oraz wartości liczbowe atrybutów (klucz = nazwa
 * kolumny). Reprezentuje jeden obiekt przestrzeni rozważań (jedno auto).
 */
public record CarRecord(String idTrim, String make, String model, Map<String, Double> attributes) {

    /** Wartość atrybutu lub {@code Double.NaN}, gdy brak. */
    public double value(String column) {
        Double v = attributes.get(column);
        return v == null ? Double.NaN : v;
    }

    public String label() {
        return make + " " + model;
    }
}
