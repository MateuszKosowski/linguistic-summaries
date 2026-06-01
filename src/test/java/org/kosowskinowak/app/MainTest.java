package org.kosowskinowak.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class MainTest {

    @Test
    void detectsGuiModeArgument() {
        assertTrue(Main.isGuiMode(new String[]{"--gui"}));
        assertTrue(Main.isGuiMode(new String[]{"--something", "--gui"}));
        assertFalse(Main.isGuiMode(new String[0]));
        assertFalse(Main.isGuiMode(new String[]{"--console"}));
    }
}
