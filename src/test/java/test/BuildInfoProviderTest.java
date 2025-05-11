package test;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.data.BuildInfo;
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class BuildInfoProviderTest {
    @Test
    public void testSetAndGetBuildInfo() {
        BuildInfo buildInfo = new BuildInfo("leaves", minecraftVersion("1.21.5"), 114514);
        BuildInfoProvider.INSTANCE.setBuildInfo(buildInfo);
        BuildInfo gettedBuildInfo = BuildInfoProvider.INSTANCE.getBuildInfo();
        assertEquals(buildInfo, gettedBuildInfo);
    }
}
