package org.kosowskinowak.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.data.Columns;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;

class FuzzyConfigTest {

    private final FuzzyConfig config = FuzzyConfig.defaults();

    @Test
    void structureSizes() {
        assertEquals(12, config.variables().size());
        assertEquals(5, config.relativeQuantifiers().size());
        assertEquals(4, config.absoluteQuantifiers().size());
        for (LinguisticVariable v : config.variables()) {
            assertEquals(5, v.labels().size(), "Każda zmienna ma 5 etykiet: " + v.name());
        }
    }

    @Test
    void variableLookupAndUniverse() {
        LinguisticVariable hp = config.variable(Columns.ENGINE_HP);
        assertEquals(0.0, hp.universe().min(), 1e-9);
        assertEquals(800.0, hp.universe().max(), 1e-9);
        assertTrue(hp.label("dynamiczny").isPresent());
        // etykieta "dynamiczny" = trapez (180,220,300,400): μ(260)=1.0
        assertEquals(1.0, hp.degreeOf(hp.label("dynamiczny").orElseThrow(), 260), 1e-6);
    }

    @Test
    void unknownVariableThrows() {
        assertThrows(IllegalArgumentException.class, () -> config.variable("nieistnieje"));
    }
}
