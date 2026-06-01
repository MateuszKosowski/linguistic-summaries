package org.kosowskinowak.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.data.Columns;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.fuzzy.mf.TriangularMf;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;

final class FuzzyConfigAdvancedTest {

    @Test
    void addsRuntimeLabelToExistingLinguisticVariable() {
        FuzzyConfig config = FuzzyConfig.defaults();
        int before = config.variable(Columns.ENGINE_HP).labels().size();

        config.addLabel(Columns.ENGINE_HP, "testowy pik", new TriangularMf(100, 150, 200));

        var variable = config.variable(Columns.ENGINE_HP);
        var label = variable.label("testowy pik");
        assertEquals(before + 1, variable.labels().size());
        assertTrue(label.isPresent());
        assertEquals(1.0, variable.degreeOf(label.orElseThrow(), 150), 1e-9);
    }

    @Test
    void rejectsDuplicateRuntimeLabels() {
        FuzzyConfig config = FuzzyConfig.defaults();

        config.addLabel(Columns.ENGINE_HP, "testowy pik", new TriangularMf(100, 150, 200));

        assertThrows(IllegalArgumentException.class,
                () -> config.addLabel(Columns.ENGINE_HP, "testowy pik", new TriangularMf(200, 250, 300)));
    }

    @Test
    void addsRuntimeRelativeAndAbsoluteQuantifiers() {
        FuzzyConfig config = FuzzyConfig.defaults();
        int relativeBefore = config.relativeQuantifiers().size();
        int absoluteBefore = config.absoluteQuantifiers().size();

        config.addRelativeQuantifier("testowo sporo", new TrapezoidalMf(0.4, 0.5, 0.8, 0.9));
        config.addAbsoluteQuantifier("testowe sztuki", new TrapezoidalMf(1000, 2000, 3000, 4000));

        Quantifier relative = config.relativeQuantifiers().get(relativeBefore);
        Quantifier absolute = config.absoluteQuantifiers().get(absoluteBefore);
        assertEquals(relativeBefore + 1, config.relativeQuantifiers().size());
        assertEquals(absoluteBefore + 1, config.absoluteQuantifiers().size());
        assertEquals(Quantifier.Type.RELATIVE, relative.type());
        assertEquals(Quantifier.Type.ABSOLUTE, absolute.type());
        assertEquals(1.0, relative.degree(0.6), 1e-9);
        assertEquals(1.0, absolute.degree(2500), 1e-9);
    }
}
