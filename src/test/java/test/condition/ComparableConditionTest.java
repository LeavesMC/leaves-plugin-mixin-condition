package test.condition;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.condition.ComparableCondition;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComparableConditionTest {

    private MinecraftVersionData v(String str) {
        return MinecraftVersionData.fromString(str);
    }

    ComparableCondition<MinecraftVersionData> cond(String expr) {
        return ComparableCondition.parse(expr, MinecraftVersionData::fromString);
    }

    @Test
    void testEquality() {
        assertTrue(cond("1.20.4").matches(v("1.20.4")));
        assertFalse(cond("1.20.4").matches(v("1.20.5")));
    }

    @Test
    void testGreaterLess() {
        assertTrue(cond(">1.20.4").matches(v("1.20.5")));
        assertFalse(cond(">1.20.4").matches(v("1.20.4")));

        assertTrue(cond(">=1.18.2").matches(v("1.18.2")));
        assertTrue(cond(">=1.18.2").matches(v("1.19.0")));
        assertFalse(cond(">=1.18.2").matches(v("1.18.1")));

        assertTrue(cond("<1.18.2").matches(v("1.18.1")));
        assertFalse(cond("<1.18.2").matches(v("1.18.2")));

        assertTrue(cond("<=1.18.2").matches(v("1.18.2")));
        assertFalse(cond("<=1.18.2").matches(v("1.18.3")));
    }

    @Test
    void testAndOrNot() {
        ComparableCondition<MinecraftVersionData> c1 = cond(">=1.18.2 && <1.20.0");
        assertTrue(c1.matches(v("1.18.2")));
        assertTrue(c1.matches(v("1.19.9")));
        assertFalse(c1.matches(v("1.20.0")));
        assertFalse(c1.matches(v("1.18.1")));

        ComparableCondition<MinecraftVersionData> c2 = cond("1.16.5 || 1.20.4");
        assertTrue(c2.matches(v("1.16.5")));
        assertFalse(c2.matches(v("1.17.0")));
        assertTrue(c2.matches(v("1.20.4")));

        ComparableCondition<MinecraftVersionData> c3 = cond("!(<1.18.2)");
        assertTrue(c3.matches(v("1.18.2")));
        assertTrue(c3.matches(v("2.0.0")));
        assertFalse(c3.matches(v("1.15.2")));

        ComparableCondition<MinecraftVersionData> c4 = cond("!1.18.2");
        assertFalse(c4.matches(v("1.18.2")));
        assertTrue(c4.matches(v("2.0.0")));
        assertTrue(c4.matches(v("1.15.2")));

        ComparableCondition<MinecraftVersionData> C5 = cond("1.16.5 && 1.20.4");
        assertFalse(C5.matches(v("1.16.5")));
        assertFalse(C5.matches(v("1.17.0")));
        assertFalse(C5.matches(v("1.20.4")));
    }

    @Test
    void testParenthesesPriority() {
        ComparableCondition<MinecraftVersionData> r1 = cond(">=1.19.4 && (<1.20.0 || 1.18.2)");
        assertTrue(r1.matches(v("1.19.4")));
        assertFalse(r1.matches(v("1.18.2")));
        assertFalse(r1.matches(v("1.20.0")));
        assertFalse(r1.matches(v("1.18.0")));

        ComparableCondition<MinecraftVersionData> r2 = cond(">=1.19.4 && !(<1.20.0 || 1.18.2)");
        assertFalse(r2.matches(v("1.19.4")));
        assertFalse(r2.matches(v("1.18.2")));
        assertTrue(r2.matches(v("1.20.0")));
        assertFalse(r2.matches(v("1.18.0")));
    }

    @Test
    void testInvalidSyntax() {
        assertThrows(IllegalArgumentException.class, () -> cond("!(>=1.18.0").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> cond("[1.18.0, 1.21]").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> cond(">=").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> cond("1.21(").matches(v("1.19.0")));
        assertThrows(IllegalArgumentException.class, () -> cond("1.21!").matches(v("1.19.0")));
    }
}