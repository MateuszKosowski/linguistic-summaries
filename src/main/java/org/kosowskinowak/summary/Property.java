package org.kosowskinowak.summary;

import java.util.List;

import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.fuzzy.linguistic.Label;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;

/**
 * Prosta (atomowa) forma sumaryzatora/kwalifikatora: jedna etykieta jednej zmiennej lingwistycznej,
 * np. „moc silnika = dynamiczny".
 */
public final class Property implements LabelExpression {

    private final LinguisticVariable variable;
    private final Label label;

    public Property(LinguisticVariable variable, Label label) {
        this.variable = variable;
        this.label = label;
    }

    @Override
    public double degree(CarRecord record) {
        double raw = record.value(variable.column());
        if (!variable.isValid(raw)) {
            return Double.NaN;
        }
        return variable.degreeOf(label, raw);
    }

    @Override
    public String text() {
        return variable.name() + " = " + label.name();
    }

    @Override
    public List<Property> atoms() {
        return List.of(this);
    }

    public LinguisticVariable variable() {
        return variable;
    }

    public Label label() {
        return label;
    }
}
