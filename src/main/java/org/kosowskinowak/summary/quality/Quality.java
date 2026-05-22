package org.kosowskinowak.summary.quality;

/**
 * Komplet miar jakości podsumowania lingwistycznego T1–T11 oraz wartość podsumowania
 * optymalnego (ważona suma). Wszystkie miary w [0, 1]; wyższa wartość = lepsza.
 */
public record Quality(
        double t1,  // stopień prawdziwości
        double t2,  // stopień nieprecyzyjności sumaryzatora
        double t3,  // stopień pokrycia
        double t4,  // stopień trafności
        double t5,  // długość podsumowania (sumaryzatora)
        double t6,  // stopień nieprecyzyjności kwantyfikatora
        double t7,  // stopień kardynalności kwantyfikatora
        double t8,  // stopień kardynalności sumaryzatora
        double t9,  // stopień nieprecyzyjności kwalifikatora
        double t10, // stopień kardynalności kwalifikatora
        double t11, // długość kwalifikatora
        double optimal) {

    public double[] toArray() {
        return new double[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11};
    }

    public static final String[] NAMES = {
            "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11"
    };
}
