package org.kosowskinowak.summary;

import java.util.List;

import org.kosowskinowak.data.CarRecord;

/**
 * Sumaryzator lub kwalifikator jako zbiór rozmyty: prosty (jedna etykieta) albo złożony
 * (połączony spójnikiem AND/OR). Wylicza stopień przynależności rekordu i opisuje się tekstowo.
 */
public interface LabelExpression {

    /**
     * Stopień przynależności rekordu do tego (złożonego) zbioru rozmytego.
     *
     * @return wartość w [0, 1] albo {@code Double.NaN}, gdy rekord nie ma poprawnych danych dla
     * któregokolwiek z użytych atrybutów (rekord należy wtedy pominąć w obliczeniach).
     */
    double degree(CarRecord record);

    /** Fragment opisu w języku quasi-naturalnym. */
    String text();

    /** Atomowe składowe (proste etykiety) tworzące to wyrażenie. */
    List<Property> atoms();

    /** Długość (kardynalność) wyrażenia: liczba atomowych etykiet. */
    default int length() {
        return atoms().size();
    }
}
