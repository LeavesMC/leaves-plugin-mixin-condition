package test;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.BuildInfo;
import org.leavesmc.plugin.mixin.condition.BuildInfoManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leavesmc.plugin.mixin.condition.MinecraftVersion.minecraftVersion;

public class BuildInfoManagerTest {
    @Test
    public void testSetAndGetBuildInfo() {
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.5"), 114514);
        BuildInfoManager.INSTANCE.setBuildInfo(buildInfo);
        BuildInfo gettedBuildInfo = BuildInfoManager.INSTANCE.getBuildInfo();
        assertEquals(buildInfo, gettedBuildInfo);
    }
}
