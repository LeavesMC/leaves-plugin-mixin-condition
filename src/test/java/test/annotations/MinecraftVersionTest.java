package test.annotations;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.annotations.MinecraftVersion;
import org.leavesmc.plugin.mixin.condition.data.BuildInfo;
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.leavesmc.plugin.mixin.condition.condition.ConditionChecker.shouldApplyMixin;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class MinecraftVersionTest {
    @MinecraftVersion("1.21.4")
    static class TestMixin {
    }

    @Test
    public void testShouldApplyMixin() {
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.4"), 114514);
        BuildInfoProvider.INSTANCE.setBuildInfo(buildInfo);
        assertTrue(shouldApplyMixin(TestMixin.class.getName()));
    }
}
