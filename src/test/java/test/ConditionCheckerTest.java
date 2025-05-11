package test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.annotations.Condition;
import org.leavesmc.plugin.mixin.condition.annotations.MinecraftVersion;
import org.leavesmc.plugin.mixin.condition.data.BuildInfo;
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.*;
import static org.leavesmc.plugin.mixin.condition.condition.ConditionChecker.shouldApplyMixin;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class ConditionCheckerTest {
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Condition(TestAnnotation2.Checker.class)
    @interface TestAnnotation2 {
        class Checker {
        }
    }

    @TestAnnotation1
    @MinecraftVersion("1.21.4")
    static class TestMixin1 {
    }

    @MinecraftVersion("1.21.5")
    static class TestMixin2 {
    }

    @TestAnnotation2
    static class TestMixin3 {
    }

    @MinecraftVersion("1.21.4")
    @TestAnnotation2
    static class TestMixin4 {
    }

    static class TestMixin5 {
    }

    @BeforeAll
    static void setup() {
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.4"), 114514);
        BuildInfoProvider.INSTANCE.setBuildInfo(buildInfo);
    }

    @Test
    public void testShouldApplyMixin() {
        assertTrue(shouldApplyMixin(TestMixin1.class.getName()));
        assertFalse(shouldApplyMixin(TestMixin2.class.getName()));
        assertTrue(shouldApplyMixin(TestMixin5.class.getName()));
    }

    @Test
    public void testShouldApplyThrow() {
        assertThrows(RuntimeException.class, () -> shouldApplyMixin(TestMixin3.class.getName()));
        assertThrows(RuntimeException.class, () -> shouldApplyMixin("aaa.bbb"));
        assertThrows(IllegalArgumentException.class, () -> shouldApplyMixin(TestMixin4.class.getName()));
    }
}
