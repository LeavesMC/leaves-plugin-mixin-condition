package test.annotation.conditions;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.ConditionChecker;
import org.leavesmc.plugin.mixin.condition.annotation.conditions.ServerBuild;
import org.leavesmc.plugin.mixin.condition.build.BuildInfo;
import org.leavesmc.plugin.mixin.condition.build.BuildInfoManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class ServerBuildTest {
    @ServerBuild(minecraft = "1.21.4", build = ">=114514")
    static class TestMixin1 {
    }

    @ServerBuild(minecraft = "1.21.5", build = ">=114514")
    static class TestMixin2 {
    }

    @Test
    public void testShouldApplyMixin() {
        ConditionChecker checker = new ConditionChecker();
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.4"), 1919810);
        BuildInfoManager.INSTANCE.setBuildInfo(buildInfo);
        assertTrue(checker.shouldApplyMixin(TestMixin1.class.getName()));
        assertFalse(checker.shouldApplyMixin(TestMixin2.class.getName()));
    }
}
