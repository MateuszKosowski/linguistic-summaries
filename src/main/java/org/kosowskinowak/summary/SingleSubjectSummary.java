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
        SentenceGrammar.Phrase phrase = SentenceGrammar.quantifiedCars(quantifier, "");
        if (qualifier == null) {
            return phrase.subject() + " " + phrase.verb() + ": " + summarizer.text() + ".";
        }
        return phrase.subject() + ", " + phrase.relativeClause() + " [" + qualifier.text() + "], "
                + phrase.verb() + ": " + summarizer.text() + ".";
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

}
