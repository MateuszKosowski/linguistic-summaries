package org.kosowskinowak.summary;

import org.kosowskinowak.fuzzy.linguistic.Quantifier;

/** Minimalna odmiana fraz używanych w generowanych zdaniach quasi-naturalnych. */
public final class SentenceGrammar {

    private SentenceGrammar() {
    }

    public static Phrase quantifiedCars(Quantifier quantifier, String suffix) {
        String q = capitalize(quantifier.name());
        return switch (quantifier.name()) {
            case "prawie żaden" -> new Phrase(q + " samochód" + suffix, "ma",
                    "spełniający", "który spełnia");
            case "prawie wszystkie" -> new Phrase(q + " samochody" + suffix, "mają",
                    "spełniające", "które spełniają");
            default -> new Phrase(q + " samochodów" + suffix, "ma",
                    "spełniających", "które spełniają");
        };
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public record Phrase(String subject, String verb, String qualifierAdjective, String relativeClause) {
    }
}
