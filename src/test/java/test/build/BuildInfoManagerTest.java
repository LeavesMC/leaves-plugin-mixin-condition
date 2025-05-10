package test.build;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.build.BuildInfo;
import org.leavesmc.plugin.mixin.condition.build.BuildInfoManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class BuildInfoManagerTest {
    @Test
    public void testSetAndGetBuildInfo() {
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.5"), 114514);
        BuildInfoManager.INSTANCE.setBuildInfo(buildInfo);
        BuildInfo gettedBuildInfo = BuildInfoManager.INSTANCE.getBuildInfo();
        assertEquals(buildInfo, gettedBuildInfo);
    }
}
