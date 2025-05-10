package org.leavesmc.plugin.mixin.condition.build;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

public record BuildInfo(
    String projectName,
    MinecraftVersionData minecraftVersion,
    int buildNumber
) {
    @Contract("_ -> new")
    public static @NotNull BuildInfo fromString(@NotNull String buildInfoString) {
        String[] split = buildInfoString.split("\t");
        if (split.length != 3) {
            throw new IllegalArgumentException("Invalid build info string: " + buildInfoString);
        }
        return new BuildInfo(
            split[0],
            MinecraftVersionData.minecraftVersion(split[1]),
            Integer.parseInt(split[2])
        );
    }
}
