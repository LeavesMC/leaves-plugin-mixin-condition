package org.leavesmc.plugin.mixin.condition;

public class BuildInfoManager {
    public static final BuildInfoManager INSTANCE = new BuildInfoManager();
    private BuildInfo buildInfo;

    private BuildInfoManager() {}

    public void setBuildInfo(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }
}
