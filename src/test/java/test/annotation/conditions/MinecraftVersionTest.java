package test.annotation.conditions;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.ConditionChecker;
import org.leavesmc.plugin.mixin.condition.annotation.conditions.MinecraftVersion;
import org.leavesmc.plugin.mixin.condition.build.BuildInfo;
import org.leavesmc.plugin.mixin.condition.build.BuildInfoManager;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class MinecraftVersionTest {
    @MinecraftVersion("1.21.4")
    static class TestMixin {
    }

    @Test
    public void testShouldApplyMixin() {
        ConditionChecker checker = new ConditionChecker();
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.4"), 114514);
        BuildInfoManager.INSTANCE.setBuildInfo(buildInfo);
        assertTrue(checker.shouldApplyMixin(TestMixin.class.getName()));
    }
}
