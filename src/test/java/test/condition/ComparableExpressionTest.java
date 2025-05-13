package test.condition;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.condition.ComparableExpression;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComparableExpressionTest {

    @Contract("_ -> new")
    private @NotNull MinecraftVersionData v(String str) {
        return MinecraftVersionData.fromString(str);
    }

    ComparableExpression<MinecraftVersionData> expr(String expr) {
        return ComparableExpression.parse(expr, MinecraftVersionData::fromString);
    }

    @Test
    void testEquality() {
        assertTrue(expr("1.20.4").matches(v("1.20.4")));
        assertFalse(expr("1.20.4").matches(v("1.20.5")));
    }

    @Test
    void testGreaterLess() {
        assertTrue(expr(">1.20.4").matches(v("1.20.5")));
        assertFalse(expr(">1.20.4").matches(v("1.20.4")));

        assertTrue(expr(">=1.18.2").matches(v("1.18.2")));
        assertTrue(expr(">=1.18.2").matches(v("1.19.0")));
        assertFalse(expr(">=1.18.2").matches(v("1.18.1")));

        assertTrue(expr("<1.18.2").matches(v("1.18.1")));
        assertFalse(expr("<1.18.2").matches(v("1.18.2")));

        assertTrue(expr("<=1.18.2").matches(v("1.18.2")));
        assertFalse(expr("<=1.18.2").matches(v("1.18.3")));
    }

    @Test
    void testAndOrNot() {
        ComparableExpression<MinecraftVersionData> e1 = expr(">=1.18.2 && <1.20.0");
        assertTrue(e1.matches(v("1.18.2")));
        assertTrue(e1.matches(v("1.19.9")));
        assertFalse(e1.matches(v("1.20.0")));
        assertFalse(e1.matches(v("1.18.1")));

        ComparableExpression<MinecraftVersionData> e2 = expr("1.16.5 || 1.20.4");
        assertTrue(e2.matches(v("1.16.5")));
        assertFalse(e2.matches(v("1.17.0")));
        assertTrue(e2.matches(v("1.20.4")));

        ComparableExpression<MinecraftVersionData> e3 = expr("!(<1.18.2)");
        assertTrue(e3.matches(v("1.18.2")));
        assertTrue(e3.matches(v("2.0.0")));
        assertFalse(e3.matches(v("1.15.2")));

        ComparableExpression<MinecraftVersionData> e4 = expr("!1.18.2");
        assertFalse(e4.matches(v("1.18.2")));
        assertTrue(e4.matches(v("2.0.0")));
        assertTrue(e4.matches(v("1.15.2")));

        ComparableExpression<MinecraftVersionData> e5 = expr("1.16.5 && 1.20.4");
        assertFalse(e5.matches(v("1.16.5")));
        assertFalse(e5.matches(v("1.17.0")));
        assertFalse(e5.matches(v("1.20.4")));
    }

    @Test
    void testParenthesesPriority() {
        ComparableExpression<MinecraftVersionData> e1 = expr(">=1.19.4 && (<1.20.0 || 1.18.2)");
        assertTrue(e1.matches(v("1.19.4")));
        assertFalse(e1.matches(v("1.18.2")));
        assertFalse(e1.matches(v("1.20.0")));
        assertFalse(e1.matches(v("1.18.0")));

        ComparableExpression<MinecraftVersionData> e2 = expr(">=1.19.4 && !(<1.20.0 || 1.18.2)");
        assertFalse(e2.matches(v("1.19.4")));
        assertFalse(e2.matches(v("1.18.2")));
        assertTrue(e2.matches(v("1.20.0")));
        assertFalse(e2.matches(v("1.18.0")));
    }

    @Test
    void testInvalidSyntax() {
        assertThrows(IllegalArgumentException.class, () -> expr("!(>=1.18.0").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> expr("[1.18.0, 1.21]").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> expr(">=").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> expr("1.21(").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> expr("1.21!").matches(v("1.19.0")));
    }
}