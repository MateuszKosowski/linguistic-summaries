package org.kosowskinowak.summary;

import java.util.ArrayList;
import java.util.List;

import org.kosowskinowak.data.CarRecord;

/**
 * Forma złożona sumaryzatora/kwalifikatora: połączenie kilku wyrażeń spójnikiem
 * AND (iloczyn = minimum) lub OR (suma = maksimum). Jeżeli którakolwiek składowa jest
 * nieoznaczona dla rekordu (NaN), całość jest nieoznaczona — rekord pomijamy.
 */
public final class Connective implements LabelExpression {

    public enum Kind {
        AND(" oraz "), OR(" lub ");

        private final String joiner;

        Kind(String joiner) {
            this.joiner = joiner;
        }
    }

    private final Kind kind;
    private final List<LabelExpression> parts;

    private Connective(Kind kind, List<LabelExpression> parts) {
        if (parts.size() < 2) {
            throw new IllegalArgumentException("Forma złożona wymaga co najmniej 2 składowych");
        }
        this.kind = kind;
        this.parts = List.copyOf(parts);
    }

    public static Connective and(LabelExpression... parts) {
        return new Connective(Kind.AND, List.of(parts));
    }

    public static Connective or(LabelExpression... parts) {
        return new Connective(Kind.OR, List.of(parts));
    }

    public static Connective of(Kind kind, LabelExpression... parts) {
        return new Connective(kind, List.of(parts));
    }

    @Override
    public double degree(CarRecord record) {
        double acc = kind == Kind.AND ? 1.0 : 0.0;
        for (LabelExpression part : parts) {
            double d = part.degree(record);
            if (Double.isNaN(d)) {
                return Double.NaN;
            }
            acc = kind == Kind.AND ? Math.min(acc, d) : Math.max(acc, d);
        }
        return acc;
    }

    @Override
    public String text() {
        List<String> fragments = new ArrayList<>();
        for (LabelExpression part : parts) {
            fragments.add(part instanceof Connective ? "(" + part.text() + ")" : part.text());
        }
        return String.join(kind.joiner, fragments);
    }

    @Override
    public List<Property> atoms() {
        List<Property> all = new ArrayList<>();
        for (LabelExpression part : parts) {
            all.addAll(part.atoms());
        }
        return all;
    }
}
