package test.annotations;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.annotations.ServerBuild;
import org.leavesmc.plugin.mixin.condition.data.BuildInfo;
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.leavesmc.plugin.mixin.condition.condition.ConditionChecker.shouldApplyMixin;
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
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.4"), 1919810);
        BuildInfoProvider.INSTANCE.setBuildInfo(buildInfo);
        assertTrue(shouldApplyMixin(TestMixin1.class.getName()));
        assertFalse(shouldApplyMixin(TestMixin2.class.getName()));
    }
}
