package org.kosowskinowak.summary.multi;

import java.util.Optional;

import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.summary.LabelExpression;

/**
 * Podsumowanie lingwistyczne wielopodmiotowe porównujące dwa rozłączne podmioty P₁, P₂
 * (Niewiadomski, Superson 2014). Cztery formy:
 * <ul>
 *   <li><b>I</b> — {@code Q P₁ w porównaniu do P₂ ma S₁} (wzór 4);</li>
 *   <li><b>II</b> — {@code Q P₁ w porównaniu do P₂ będących S₂ ma S₁} — kwalifikator dotyczy obu (14);</li>
 *   <li><b>III</b> — {@code Q P₁ będących S₂ w porównaniu do P₂ ma S₁} — kwalifikator tylko P₁ (20);</li>
 *   <li><b>IV</b> — {@code Więcej P₁ niż P₂ ma S₁} — bez kwantyfikatora (24).</li>
 * </ul>
 */
public final class MultiSubjectSummary {

    public enum Form {I, II, III, IV}

    private final Form form;
    private final Quantifier quantifier; // null dla formy IV
    private final Subject p1;
    private final Subject p2;
    private final LabelExpression summarizer; // S₁
    private final LabelExpression qualifier;  // S₂ — tylko formy II, III

    private MultiSubjectSummary(Form form, Quantifier quantifier, Subject p1, Subject p2,
                               LabelExpression summarizer, LabelExpression qualifier) {
        this.form = form;
        this.quantifier = quantifier;
        this.p1 = p1;
        this.p2 = p2;
        this.summarizer = summarizer;
        this.qualifier = qualifier;
    }

    public static MultiSubjectSummary formI(Quantifier q, Subject p1, Subject p2, LabelExpression s1) {
        return new MultiSubjectSummary(Form.I, q, p1, p2, s1, null);
    }

    public static MultiSubjectSummary formII(Quantifier q, Subject p1, Subject p2,
                                             LabelExpression s2, LabelExpression s1) {
        return new MultiSubjectSummary(Form.II, q, p1, p2, s1, s2);
    }

    public static MultiSubjectSummary formIII(Quantifier q, Subject p1, Subject p2,
                                              LabelExpression s2, LabelExpression s1) {
        return new MultiSubjectSummary(Form.III, q, p1, p2, s1, s2);
    }

    public static MultiSubjectSummary formIV(Subject p1, Subject p2, LabelExpression s1) {
        return new MultiSubjectSummary(Form.IV, null, p1, p2, s1, null);
    }

    /** Zdanie w języku quasi-naturalnym. */
    public String sentence() {
        String s1 = summarizer.text();
        return switch (form) {
            case I -> capitalize(quantifier.name()) + " aut " + p1.name()
                    + " w porównaniu do " + p2.name() + " ma: " + s1 + ".";
            case II -> capitalize(quantifier.name()) + " aut " + p1.name()
                    + " spełniających [" + qualifier.text() + "] w porównaniu do aut " + p2.name()
                    + " spełniających [" + qualifier.text() + "] ma: " + s1 + ".";
            case III -> capitalize(quantifier.name()) + " aut " + p1.name()
                    + ", które spełniają [" + qualifier.text() + "], w porównaniu do " + p2.name()
                    + " ma: " + s1 + ".";
            case IV -> "Więcej aut " + p1.name() + " niż " + p2.name() + " ma: " + s1 + ".";
        };
    }

    public Form form() {
        return form;
    }

    public Optional<Quantifier> quantifier() {
        return Optional.ofNullable(quantifier);
    }

    public Subject p1() {
        return p1;
    }

    public Subject p2() {
        return p2;
    }

    public LabelExpression summarizer() {
        return summarizer;
    }

    public Optional<LabelExpression> qualifier() {
        return Optional.ofNullable(qualifier);
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
