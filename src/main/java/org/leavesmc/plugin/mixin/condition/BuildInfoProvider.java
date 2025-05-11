package org.leavesmc.plugin.mixin.condition;

import org.leavesmc.plugin.mixin.condition.data.BuildInfo;

public class BuildInfoProvider {
    public static final BuildInfoProvider INSTANCE = new BuildInfoProvider();
    private BuildInfo buildInfo;

    private BuildInfoProvider() {}

    public void setBuildInfo(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }
}
