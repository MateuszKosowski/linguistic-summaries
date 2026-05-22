package org.kosowskinowak.summary;

import java.util.Optional;

import org.kosowskinowak.fuzzy.linguistic.Quantifier;

/**
 * Podsumowanie lingwistyczne jednopodmiotowe. Forma pierwsza (bez kwalifikatora):
 * <pre>[Kwantyfikator] aut ma [Sumaryzator]</pre>
 * Forma druga (z kwalifikatorem):
 * <pre>[Kwantyfikator] aut, które są [Kwalifikator], ma [Sumaryzator]</pre>
 */
public final class SingleSubjectSummary {

    private final Quantifier quantifier;
    private final LabelExpression qualifier; // null => forma pierwsza
    private final LabelExpression summarizer;

    public SingleSubjectSummary(Quantifier quantifier, LabelExpression summarizer) {
        this(quantifier, null, summarizer);
    }

    public SingleSubjectSummary(Quantifier quantifier, LabelExpression qualifier,
                                LabelExpression summarizer) {
        this.quantifier = quantifier;
        this.qualifier = qualifier;
        this.summarizer = summarizer;
    }

    public boolean hasQualifier() {
        return qualifier != null;
    }

    /** Zdanie w języku quasi-naturalnym. */
    public String sentence() {
        String q = capitalize(quantifier.name());
        if (qualifier == null) {
            return q + " aut ma: " + summarizer.text() + ".";
        }
        return q + " aut, które spełniają [" + qualifier.text() + "], ma: " + summarizer.text() + ".";
    }

    public Quantifier quantifier() {
        return quantifier;
    }

    public Optional<LabelExpression> qualifier() {
        return Optional.ofNullable(qualifier);
    }

    public LabelExpression summarizer() {
        return summarizer;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
