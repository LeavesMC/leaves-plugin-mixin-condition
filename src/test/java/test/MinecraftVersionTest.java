package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.leavesmc.plugin.mixin.condition.MinecraftVersion.minecraftVersion;

public class MinecraftVersionTest {
    @Test
    public void testConstructor1() {
        var mcVersion = minecraftVersion("1.21.5");
        assertEquals(1, mcVersion.major());
        assertEquals(21, mcVersion.minor());
        assertEquals(5, mcVersion.patch());
    }

    @Test
    public void testConstructor2() {
        var mcVersion = minecraftVersion("1.21");
        assertEquals(1, mcVersion.major());
        assertEquals(21, mcVersion.minor());
        assertEquals(0, mcVersion.patch());
    }

    @Test
    public void testConstructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> minecraftVersion("1.21.4.1"));
        assertThrows(IllegalArgumentException.class, () -> minecraftVersion("1."));
        assertThrows(IllegalArgumentException.class, () -> minecraftVersion("1.a"));
        assertThrows(IllegalArgumentException.class, () -> minecraftVersion("1.2.1.a"));
        assertThrows(IllegalArgumentException.class, () -> minecraftVersion("1"));
        assertThrows(IllegalArgumentException.class, () -> minecraftVersion(""));
        assertThrows(NumberFormatException.class, () -> minecraftVersion("1.21.a"));
    }

    @Test
    public void testPatch() {
        var low = minecraftVersion("1.21.5");
        var high = minecraftVersion("1.21.6");
        assertTrue(high.isGreaterThan(low));
        assertTrue(high.isGreaterThanOrEqualTo(low));
        assertTrue(low.isLessThan(high));
        assertTrue(low.isLessThanOrEqualTo(high));
        assertFalse(high.isLessThanOrEqualTo(low));
        assertFalse(high.isLessThan(low));
        assertFalse(low.isGreaterThan(high));
        assertFalse(low.isGreaterThan(high));
    }

    @Test
    public void testMinor() {
        var low = minecraftVersion("1.21.5");
        var high = minecraftVersion("1.22");
        assertTrue(high.isGreaterThan(low));
        assertTrue(high.isGreaterThanOrEqualTo(low));
        assertTrue(low.isLessThan(high));
        assertTrue(low.isLessThanOrEqualTo(high));
        assertFalse(high.isLessThan(low));
        assertFalse(high.isLessThanOrEqualTo(low));
        assertFalse(low.isGreaterThan(high));
        assertFalse(low.isGreaterThanOrEqualTo(high));
    }

    @Test
    public void testMajor() {
        var low = minecraftVersion("1.21.5");
        var high = minecraftVersion("2.1");
        assertTrue(high.isGreaterThan(low));
        assertTrue(high.isGreaterThanOrEqualTo(low));
        assertTrue(low.isLessThan(high));
        assertTrue(low.isLessThanOrEqualTo(high));
        assertFalse(high.isLessThan(low));
        assertFalse(high.isLessThanOrEqualTo(low));
        assertFalse(low.isGreaterThan(high));
        assertFalse(low.isGreaterThanOrEqualTo(high));
    }

    @Test
    public void testIsEqual() {
        var v1 = minecraftVersion("1.21");
        var v2 = minecraftVersion(1, 21, 0);
        assertEquals(v1, v2);
        assertTrue(v1.isEqualTo(v2));
        assertTrue(v2.isGreaterThanOrEqualTo(v1));
        assertTrue(v1.isGreaterThanOrEqualTo(v2));
    }
}
