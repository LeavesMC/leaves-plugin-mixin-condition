package test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.ConditionChecker;
import org.leavesmc.plugin.mixin.condition.annotation.Condition;
import org.leavesmc.plugin.mixin.condition.annotation.conditions.MinecraftVersion;
import org.leavesmc.plugin.mixin.condition.build.BuildInfo;
import org.leavesmc.plugin.mixin.condition.build.BuildInfoManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.*;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class ConditionCheckerTest {
    private static final ConditionChecker checker = new ConditionChecker();

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
        BuildInfoManager.INSTANCE.setBuildInfo(buildInfo);
    }

    @Test
    public void testShouldApplyMixin() {
        assertTrue(checker.shouldApplyMixin(TestMixin1.class.getName()));
        assertFalse(checker.shouldApplyMixin(TestMixin2.class.getName()));
        assertTrue(checker.shouldApplyMixin(TestMixin5.class.getName()));
    }

    @Test
    public void testShouldApplyThrow() {
        assertThrows(RuntimeException.class, () -> checker.shouldApplyMixin(TestMixin3.class.getName()));
        assertThrows(RuntimeException.class, () -> checker.shouldApplyMixin("aaa.bbb"));
        assertThrows(IllegalArgumentException.class, () -> checker.shouldApplyMixin(TestMixin4.class.getName()));
    }
}
